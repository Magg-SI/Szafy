package pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities

import android.os.AsyncTask
import android.widget.Toast
import pl.tysia.martech.BusinessLogic.Domain.User
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter
import pl.tysia.martech.BusinessLogic.Domain.Order
import pl.tysia.martech.Persistance.ApiClients.WaresClientImpl
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CollectWareCatalogAdapter
import pl.tysia.martech.R
import java.io.IOException
import java.util.ArrayList


class BackWaresCatalogActivity : WaresCatalogActivity() {

    private var sendWaresTask : SendWaresTask? = null

    override fun onFinishClicked() {
        showProgress(true)
        val task = SendWaresTask()
        task.execute()
    }

    override fun getCatalogAdapter(items : ArrayList <Order>): CatalogAdapter<Order> {
        return CollectWareCatalogAdapter(items)
    }

    override fun getCatalogItems(): ArrayList<Order> {
        return ArrayList()
    }

    override fun onItemSelected(item: Order?) {
    }


    inner class SendWaresTask  :
            AsyncTask<String, String, Int>() {
        private val waresClient = WaresClientImpl()
        var collectsSent = 0;

        override fun doInBackground(vararg params: String): Int? {

            val toSend = catalogItemsList.filter { cWare -> cWare.quantityTaken > 0 }
            //val toSendOrdered = catalogItemsList.filter { cWare -> cWare.quantityOrdered > 0 }

            val user = User.getLoggedUser(this@BackWaresCatalogActivity)

            try {
                collectsSent = waresClient.backWares(toSend, user!!.lockerID!!, user.token!!)
            }catch (e : IOException){
               return null
            }
            return collectsSent
        }

        override fun onPostExecute(result: Int?) {
            showProgress(false)
            sendWaresTask = null

            if(result==null) {
                okDialog(getString(R.string.connection_error), getString(R.string.connectoin_error_long_message))
            }
            else if (result==0) {
                finish()
                Toast.makeText(this@BackWaresCatalogActivity, getString(R.string.toast_sent_correctly), Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this@BackWaresCatalogActivity, getString(R.string.collects_not_sent), Toast.LENGTH_LONG).show()
            }

                    //if (!ordersSent && !collectsSent)  okDialog(getString(R.string.exception_occurres), getString(R.string.document_not_sent))
                    //else if (!ordersSent) Toast.makeText(this@CollectWaresCatalogActivity, getString(R.string.orders_not_sent), Toast.LENGTH_LONG).show()
                    //else if (!collectsSent) Toast.makeText(this@CollectWaresCatalogActivity, getString(R.string.collects_not_sent), Toast.LENGTH_LONG).show()



        }

        override fun onCancelled() {
            sendWaresTask = null
            showProgress(false)
        }
    }

}