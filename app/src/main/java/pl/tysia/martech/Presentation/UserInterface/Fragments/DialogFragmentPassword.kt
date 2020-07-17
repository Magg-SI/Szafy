package pl.tysia.martech.Presentation.UserInterface.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import pl.tysia.maggwarehouse.Persistance.LoginClient
import pl.tysia.martech.BusinessLogic.Domain.User
import pl.tysia.martech.Persistance.ApiClients.LoginClientImpl
import pl.tysia.martech.Persistance.Result
import pl.tysia.martech.Presentation.UserInterface.Activities.LoginActivity
import pl.tysia.martech.Presentation.UserInterface.Activities.PasswordChangeActivity
import pl.tysia.martech.R


class DialogFragmentPassword : DialogFragment() {
    private var thisView : View? = null
    private var mListener: OnFragmentInteractionListener? = null

    private var message: String = ""
    private var mAuthTask: UserLoginTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(MESSAGE)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = getActivity()!!.getLayoutInflater()

        val dialog = Dialog(getActivity()!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        thisView = inflater.inflate(R.layout.fragment_dialog_password, null)
        dialog.setContentView(thisView)

        val acceptButton = thisView!!.findViewById<Button>(R.id.accept_button)
        acceptButton.setOnClickListener({ attemptLogin() })

        val messageTV = thisView!!.findViewById<TextView>(R.id.message_tv)
        messageTV.text = message

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

    }

    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        val passwordInput = thisView!!.findViewById<TextView>(R.id.password_input)
        passwordInput.error = null

        // Store values at the time of the login attempt.
        val passwordStr = passwordInput.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            passwordInput.error = getString(R.string.error_invalid_password)
            focusView = passwordInput
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
            mAuthTask = UserLoginTask(passwordStr)
            mAuthTask!!.execute()
        }
    }


    private fun isPasswordValid(password: String): Boolean {
        return true
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val password_form = thisView!!.findViewById<LinearLayout>(R.id.password_form)
        val progressBar = thisView!!.findViewById<ProgressBar>(R.id.progressBar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            password_form.visibility = if (show) View.GONE else View.VISIBLE
            password_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        password_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            progressBar.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressBar.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            password_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    override fun onStart() {
        super.onStart()

        val dialog = getDialog()
        if (dialog != null) {
            dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }




    interface OnFragmentInteractionListener {
        fun onTypeSelected(checks: BooleanArray?)
    }


    companion object {
        private val MESSAGE = "dialog_fragment_message"

        @JvmStatic
        fun newInstance(message: String) =
            DialogFragmentPassword().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                }
            }
    }

    inner class UserLoginTask internal constructor(private val mPassword: String) :
        AsyncTask<String, String, Result<User>>() {

        override fun doInBackground(vararg params: String): Result<User>? {

            val loginService = LoginClientImpl()
            val user = User.getLoggedUser(activity?.applicationContext!!)

            return if (user != null){
                user.password = mPassword

                val res = loginService.authenticateUser(user)
                user.setLogged(activity!!)
                res

            }else{
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
                null
            }

        }

        override fun onPostExecute(result: Result<User>?) {
            mAuthTask = null
            showProgress(false)

            val passwordET = thisView!!.findViewById<EditText>(R.id.password_input)

            when {
                result?.resultCode == Result.RESULT_OK -> {
                    val intent = Intent(activity, PasswordChangeActivity::class.java)
                    startActivity(intent)
                    dismiss()
                }
                result?.resultCode == LoginClient.WRONG_PASSWORD -> {
                    passwordET.error = getString(R.string.error_incorrect_password)
                    passwordET.requestFocus()
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
        val dialogBuilder = AlertDialog.Builder(activity!!.applicationContext)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
            //get what you need here!
        })
        val b = dialogBuilder.create()
        b.show()
    }
}