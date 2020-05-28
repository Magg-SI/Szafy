package pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities

import android.os.AsyncTask
import android.widget.Toast
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.martech.BusinessLogic.Domain.Order
import pl.tysia.martech.Persistance.ApiClients.WaresClientImpl
import pl.tysia.martech.Persistance.ApiClients.WaresClientMock
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.StocktakeCatalogAdapter
import pl.tysia.martech.R
import java.io.IOException
import java.util.ArrayList

class StocktakingCatalogActivity : WaresCatalogActivity() {
    private var sendWaresTask : SendWaresTask? = null


    override fun onFinishClicked() {
        val task = SendWaresTask()
        task.execute()
    }

    override fun getCatalogAdapter(items: ArrayList<Order>): CatalogAdapter<Order> {
        return StocktakeCatalogAdapter(items)
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

            val toSend = catalogItemsList.filter { cWare -> cWare.quantityTaken > 0 }

            val user = User.getLoggedUser(this@StocktakingCatalogActivity)

            return try {
                waresClient.stocktake(toSend, user!!.lockerID!!, user.token!!)
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
                    Toast.makeText(this@StocktakingCatalogActivity, getString(R.string.toast_sent_correctly), Toast.LENGTH_LONG).show()
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