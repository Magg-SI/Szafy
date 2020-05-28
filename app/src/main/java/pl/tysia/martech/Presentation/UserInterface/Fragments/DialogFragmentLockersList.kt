package pl.tysia.martech.Presentation.UserInterface.Fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.martech.BusinessLogic.Domain.Locker
import pl.tysia.martech.Persistance.ApiClients.LockerClientImpl
import pl.tysia.martech.Persistance.ApiClients.LockerClientMock
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.LockersCatalogAdapter

import pl.tysia.martech.R
import java.io.IOException

class DialogFragmentLockersList : DialogFragment() {
    private var thisView : View? = null
    public var listener: OnLockerChosenListener? = null
    private var lockersAdapter: LockersCatalogAdapter? = null
    private var lockers: ArrayList<Locker>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater

        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        thisView = inflater.inflate(R.layout.fragment_dialog_lockers_list, null)
        dialog.setContentView(thisView)

        lockers =  ArrayList()

        val recycler : RecyclerView = thisView!!.findViewById<RecyclerView>(R.id.lockers_list)
        lockersAdapter = LockersCatalogAdapter(lockers)
        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        recycler.adapter = lockersAdapter

        val okButton = thisView!!.findViewById<Button>(R.id.ok_button)

        okButton.isEnabled = false
        lockersAdapter?.addItemSelectedListener { okButton.isEnabled = true }

        okButton.setOnClickListener {
            listener?.onLockerChosen(lockersAdapter?.selectedItem!!)
            dismiss()
        }

        val cancelButton = thisView!!.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener { dismiss() }

        GetLockersTask().execute()

        return dialog
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLockerChosenListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLockerChosenListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun okDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!.applicationContext)
        builder.setTitle(title)
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                }
        val alert = builder.create()
        alert.show()
    }

    inner class GetLockersTask : AsyncTask<String, String, Boolean>() {
        private var lockerClient = LockerClientImpl()

        override fun doInBackground(vararg params: String?): Boolean {

            val user = User.getLoggedUser(activity!!.applicationContext)

            try{
                lockers?.clear()
                lockers?.addAll(lockerClient.getLockersList(user!!.token!!)!!)
            }catch (ex : IOException){
                return false
            }

            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)

            if (!result) okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message))
            else {
                lockersAdapter?.filter()
                lockersAdapter?.notifyDataSetChanged()
            }


        }
    }

    interface OnLockerChosenListener {
        fun onLockerChosen(locker : Locker)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                DialogFragmentLockersList().apply {

                }
    }
}
