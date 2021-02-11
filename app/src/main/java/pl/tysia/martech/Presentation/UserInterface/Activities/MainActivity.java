package pl.tysia.martech.Presentation.UserInterface.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import pl.tysia.martech.BusinessLogic.Domain.User;
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType;
import pl.tysia.martech.BusinessLogic.Domain.Locker;
import pl.tysia.martech.Persistance.ApiClients.LockerClient;
import pl.tysia.martech.Persistance.ApiClients.LockerClientImpl;
import pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities.CollectWaresCatalogActivity;
import pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities.OrderWaresCatalogActivity;
import pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities.StocktakingCatalogActivity;
import pl.tysia.martech.Presentation.UserInterface.Fragments.DialogFragmentLockersList;
import pl.tysia.martech.Presentation.UserInterface.Fragments.DialogFragmentOpenLocker;
import pl.tysia.martech.Presentation.UserInterface.Fragments.DialogFragmentPassword;
import pl.tysia.martech.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogFragmentLockersList.OnLockerChosenListener, DialogFragmentOpenLocker.OnDialogResult {
    private User user;
    private static final int IDLE_TIMEOUT = 120000;
    private static final int ACTION_STOCKTAKE = 0;
    private static final int ACTION_TAKE = 1;
    private static final int ACTION_ORDER = 2;
    private static final String OPEN_LOCKER_DIALOG_TAG = "pl.tysia.martech.open_locker_dialog";
    private int action = 1;

    private boolean autoLogout = false;
    private final Handler handler = new Handler();
    private final Runnable logoutRunnable = new Runnable() {
        @Override
        public void run() {
           new CheckLockerTask().execute();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.themedToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoLogout = preferences.getBoolean("auto_logout", false);

        user = User.Companion.getLoggedUser(getApplicationContext());
        UserType userType = user.getType();

        //TODO: naprawić prowizorkę ( WORKER może zmieniać szafę )
        String xx = user.getToken();
        if(xx.length()<4) xx="????";
        xx = xx.substring(0,4);
        boolean xzm = xx.equals("0181");
        //Toast.makeText(MainActivity.this, xx, Toast.LENGTH_LONG).show();

        switch (userType) {
            case ADMIN:
               // navigationView.getMenu().findItem(R.id.take_item).setVisible(false);
                //navigationView.getMenu().findItem(R.id.order_item).setVisible(false);
                if (user.getLockerID() == 0) changeLocker();
                action = ACTION_STOCKTAKE;
                break;
            case WORKER:
                navigationView.getMenu().findItem(R.id.stocktake_item).setVisible(false);

                //TODO: naprawić prowizorkę ( WORKER może zmieniać szafę )
                //navigationView.getMenu().findItem(R.id.change_locker).setVisible(false);
                //navigationView.getMenu().findItem(R.id.change_locker).setVisible(xzm);

                action = ACTION_TAKE;
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (autoLogout) resetTimeout();
    }

    private void changeLocker(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragmentLockersList dialogFragmentLockersList =  DialogFragmentLockersList.newInstance();
        dialogFragmentLockersList.setListener(this);
        dialogFragmentLockersList.show(fragmentManager, "TAG_CHOOSE_LOCKER");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        TextView name_tv = findViewById(R.id.name_tv);
        TextView locker_tv = findViewById(R.id.locker_tv);

        final Switch item = findViewById(R.id.switch_item);
        item.setChecked(autoLogout);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLogout = !autoLogout;
                item.setChecked(autoLogout);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                preferences.edit().putBoolean("auto_logout", autoLogout).commit();
            }
        });

        if (name_tv!= null)
            name_tv.setText("Użytkownik: " + user.getLogin());
        if (locker_tv!= null)
            locker_tv.setText("Szafa: " + user.getLockerNr());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetTimeout(){
        handler.removeCallbacks(logoutRunnable);
        handler.postDelayed(logoutRunnable, IDLE_TIMEOUT);
    }

    public void openLockerClick(View view){
        resetTimeout();
        if (user.getLockerID() == null){
            changeLocker();
        }else {
            OpenLockerTask task = new OpenLockerTask();
            task.execute();

            showProgress(true);
        }
    }

    private void openCollectWares(){
        Intent intent = new Intent(this, CollectWaresCatalogActivity.class);
        startActivity(intent);

    }

    private void openOrderWares(){
        Intent intent = new Intent(this, OrderWaresCatalogActivity.class);
        startActivity(intent);

    }

    private void openStocktaking(){
        Intent intent = new Intent(this, StocktakingCatalogActivity.class);
        startActivity(intent);

    }

    private void logout(){
        User.Companion.logout(getApplicationContext());
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutDialog(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage(R.string.logout_question);

        builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
               logout();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
        TextView textView = alert.findViewById(android.R.id.message);
        assert textView != null;
        textView.setTextSize(16);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        resetTimeout();

        int id = item.getItemId();

        if (id == R.id.take_item) {
            action = ACTION_TAKE;
            openLockerClick(null);
        }
        if (id == R.id.order_item) {
            action = ACTION_ORDER;
            openLockerClick(null);
        }
        if (id == R.id.stocktake_item) {
            action = ACTION_STOCKTAKE;
            openLockerClick(null);
        }
        if (id == R.id.account_change_password) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogFragmentPassword.newInstance(getString(R.string.password_confirmation_order)).show(fragmentManager, "TAG_CONFIRM_PASSWORD");
        }
        if (id == R.id.account_logout) {
           logoutDialog();
        }
        if (id == R.id.change_locker) {
            changeLocker();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        final ConstraintLayout mOpenLockerView = findViewById(R.id.open_locker);
        final ConstraintLayout mProgressView = findViewById(R.id.open_locker_loading);


        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mOpenLockerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mOpenLockerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mOpenLockerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onLockerChosen(@NotNull Locker locker) {
        TextView locker_tv = findViewById(R.id.locker_tv);

        user.setLockerID(locker.getId());
        user.setLockerNr(locker.getNumber());
        user.setLogged(this);

        if (locker_tv!= null)
            locker_tv.setText("Szafa: " + user.getLockerNr());
    }


    private void showOpenLockerBox(){
        DialogFragmentOpenLocker.newInstance().show(getSupportFragmentManager(), OPEN_LOCKER_DIALOG_TAG);

        /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setTitle("Otwieranie szafy");
        builder.setMessage("Naciśnij przycisk na szafie");

        builder.setPositiveButton("Wciśnięto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openLockerClick(null);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setTextSize(16);*/
    }

    private void openLocker(){
        Toast.makeText(MainActivity.this, getString(R.string.locker_opened), Toast.LENGTH_LONG).show();

        switch (action) {
            case ACTION_STOCKTAKE:
                openStocktaking();
                break;
            case ACTION_TAKE:
                openCollectWares();
                break;
            case ACTION_ORDER:
                openOrderWares();
                break;
        }
    }

    @Override
    public void lockerOpenedResult(boolean lockerOpened) {
        if (lockerOpened) openLockerClick(null);

    }

    class CheckLockerTask extends
            AsyncTask<Integer, Integer, Boolean> {
        private boolean exceptionOccured = false;
        LockerClient lockerClient;

        @Override
        protected Boolean doInBackground(Integer... params){
            User user = User.Companion.getLoggedUser(MainActivity.this);

            try {
                lockerClient = new LockerClientImpl();
                return lockerClient.openLocker(user.getLockerID(), user.getToken());
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showProgress(false);

            if (!result) logout();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    class OpenLockerTask extends
            AsyncTask<Integer, Integer, Boolean> {
        private boolean exceptionOccured = false;
        LockerClient lockerClient;

        @Override
        protected Boolean doInBackground(Integer... params){
            User user = User.Companion.getLoggedUser(MainActivity.this);

            try {
                lockerClient = new LockerClientImpl();
                return lockerClient.openLocker(user.getLockerID(), user.getToken());
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showProgress(false);


            if (result) openLocker();
            else if (exceptionOccured)
                Toast.makeText(MainActivity.this, getString(R.string.couldnt_open_locker), Toast.LENGTH_LONG).show();
            else showOpenLockerBox();

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
