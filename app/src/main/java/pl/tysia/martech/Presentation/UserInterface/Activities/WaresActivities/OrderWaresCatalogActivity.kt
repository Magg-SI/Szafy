package pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities

import android.os.AsyncTask
import android.widget.Toast
import pl.tysia.martech.BusinessLogic.Domain.User
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter
import pl.tysia.martech.BusinessLogic.Domain.Order
import pl.tysia.martech.Persistance.ApiClients.WaresClientImpl
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.OrderWareCatalogAdapter
import pl.tysia.martech.R
import java.io.IOException
import java.util.ArrayList

class OrderWaresCatalogActivity : WaresCatalogActivity() {
    private var sendWaresTask : SendWaresTask? = null

    override fun onFinishClicked() {
        showProgress(true)
        val task = SendWaresTask()
        task.execute()
    }

    override fun getCatalogAdapter(items: ArrayList<Order>): CatalogAdapter<Order> {
        return OrderWareCatalogAdapter(items)
    }

    override fun onItemSelected(item: Order?) {

    }

    override fun getCatalogItems(): ArrayList<Order> {
        return ArrayList()
    }


    inner class SendWaresTask  :
            AsyncTask<String, String, Boolean>() {
        private val waresClient = WaresClientImpl()

        override fun doInBackground(vararg params: String): Boolean? {

            val toSend = catalogItemsList.filter { cWare -> cWare.quantityOrdered > 0 }

            val user = User.getLoggedUser(this@OrderWaresCatalogActivity)

            return try {
                waresClient.orderWares(toSend, user!!.lockerID!!, user.token!!)
            }catch (e : IOException){
                return null
            }


        }

        override fun onPostExecute(result: Boolean?) {
            showProgress(false)
            sendWaresTask = null

            when (result){
                true ->{
                    finish()
                    Toast.makeText(this@OrderWaresCatalogActivity, getString(R.string.toast_sent_correctly), Toast.LENGTH_LONG).show()
                }
                null ->  okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message))

                false ->  okDialog(getString(R.string.exception_occurres), getString(R.string.document_not_sent))
            }
        }

        override fun onCancelled() {
            sendWaresTask = null
            showProgress(false)
        }
    }
}