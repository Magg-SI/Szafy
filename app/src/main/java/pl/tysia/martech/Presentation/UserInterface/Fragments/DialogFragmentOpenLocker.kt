package pl.tysia.martech.Presentation.UserInterface.Fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import pl.tysia.martech.R

class DialogFragmentOpenLocker : DialogFragment() {
    private var thisView : View? = null
    private var mListener: OnDialogResult? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is OnDialogResult)
            mListener = activity as OnDialogResult
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = getActivity()!!.getLayoutInflater()

        val dialog = Dialog(getActivity()!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        thisView = inflater.inflate(R.layout.fragment_dialog_open_locker, null)
        dialog.setContentView(thisView)

        val openedButton = thisView!!.findViewById<Button>(R.id.openedButton)
        openedButton.setOnClickListener {
            mListener?.lockerOpenedResult(true)
            dismiss()
        }

        val cancelButton = thisView!!.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            mListener?.lockerOpenedResult(false)
            dismiss()
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

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


    interface OnDialogResult {
        fun lockerOpenedResult(lockerOpened : Boolean)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                DialogFragmentOpenLocker().apply {

                }
    }

}