package pl.tysia.martech.Presentation.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User;
import pl.tysia.martech.BusinessLogic.Domain.Ware;
import pl.tysia.martech.Persistance.ApiClients.WaresClient;
import pl.tysia.martech.Persistance.ApiClients.WaresClientImpl;
import pl.tysia.martech.R;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);

        mScannerView = new ZXingScannerView(this);

        FrameLayout cameraFrame = findViewById(R.id.cameraFrame);
        cameraFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        final String code = rawResult.getText();

        GetWareTask task = new GetWareTask();
        task.execute(code);


        //mScannerView.resumeCameraPreview(this);
    }


    private void YNMessageBox(String question, final Runnable action){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage(question);

        builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                action.run();
            }
        });

        builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(16);
    }

    private void returnWare(Ware ware){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("scanner_result",ware);
        setResult(1, returnIntent);
        finish();
    }

    private void showSendingState(boolean state){
        ProgressBar bar = findViewById(R.id.progressBar);

        if (state) bar.setVisibility(View.VISIBLE);
        else bar.setVisibility(View.GONE);
    }

    private void okDialog(String title, String message ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class GetWareTask extends
            AsyncTask<String, String, Ware> {
        private boolean exceptionOccured = false;
        WaresClient waresClient;

        @Override
        protected Ware doInBackground(String... params){

            User user = User.Companion.getLoggedUser(ScannerActivity.this);

            try {

                waresClient = new WaresClientImpl();
                return waresClient.getWare(params[0], user.getToken());
            }catch (IOException e){
                exceptionOccured = true;
                return null;
            }

        }

        @Override
        protected void onPostExecute(Ware ware) {
            showSendingState(false);

            if (ware != null)
                returnWare(ware);
            else if(exceptionOccured){
                finish();
                okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message));
            }else{
                Toast.makeText(ScannerActivity.this,
                        getString(R.string.product_not_found), Toast.LENGTH_LONG)
                        .show();
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }

        @Override
        protected void onCancelled() {
            showSendingState(false);
        }
    }

}
