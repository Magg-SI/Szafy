package pl.tysia.martech.Presentation.UserInterface.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_login.*
import pl.tysia.martech.R
import pl.tysia.martech.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.Persistance.LoginClient
import pl.tysia.martech.Persistance.ApiClients.LoginClientImpl
import pl.tysia.martech.Persistance.Result

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (User.getLoggedUser(applicationContext) != null)
            TokenTask().execute()

        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

        val toolbar = findViewById<Toolbar>(R.id.themedToolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true

    }

    private fun openMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()
        val lockerStr = locker.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute()
        }
    }

    private fun isEmailValid(login: String): Boolean {
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return true
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            email_login_form.visibility = if (show) View.GONE else View.VISIBLE
            email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
            email_login_form.animate()
            email_sign_in_button.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        email_login_form.visibility = if (show) View.GONE else View.VISIBLE
                        email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            email_login_form.visibility = if (show) View.GONE else View.VISIBLE
            email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(
            this,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(
                ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
            ), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
                ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE
            ),

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        )
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        val lockers = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            lockers.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(
            this@LoginActivity,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection
        )

        email.setAdapter(adapter)
    }


    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        )
        val ADDRESS = 0
        val LOCKER = 0
        val IS_PRIMARY = 1
    }

    inner class TokenTask internal constructor() :
            AsyncTask<String, String, Result<Boolean>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }

        override fun doInBackground(vararg params: String): Result<Boolean>? {

            val loginService = LoginClientImpl()

            return loginService.checkToken(User.getLoggedUser(this@LoginActivity)!!)
        }

        override fun onPostExecute(result: Result<Boolean>?) {
            mAuthTask = null
            showProgress(false)

            when (result?.resultCode) {
                Result.RESULT_OK ->
                    openMainActivity()
            }

            showProgress(false)
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    inner class UserLoginTask internal constructor(mEmail: String, mPassword: String) :
        AsyncTask<String, String, Result<User>>() {
        private var user : User = User(mEmail, mPassword)


        override fun doInBackground(vararg params: String): Result<User>? {

            val loginService = LoginClientImpl()

            return loginService.authenticateUser(user)

        }

        override fun onPostExecute(result: Result<User>?) {
            mAuthTask = null
            showProgress(false)

            when {
                result?.resultCode == Result.RESULT_OK -> {
                    openMainActivity()
                    user.setLogged(this@LoginActivity)
                }
                result?.resultCode == LoginClient.WRONG_PASSWORD -> {
                    password.error = getString(R.string.error_incorrect_password)
                    password.requestFocus()
                }
                result?.resultCode == LoginClient.WRONG_LOGIN -> {
                    email.error = getString(R.string.error_no_such_user)
                    email.requestFocus()
                }
                result != null -> okDialog(getString(R.string.exception_occurres), result.resultMessage)
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    private fun okDialog(title : String, message : String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
            //get what you need here!
        })
        val b = dialogBuilder.create()
        b.show()
    }
}
