package pl.tysia.martech.Presentation.UserInterface.Activities.WaresActivities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_colect_wares.*
import pl.tysia.martech.BusinessLogic.Domain.User
import pl.tysia.martech.BusinessLogic.Domain.Order
import pl.tysia.martech.BusinessLogic.Domain.Ware
import pl.tysia.martech.Persistance.ApiClients.WaresClientImpl
import pl.tysia.martech.Presentation.UserInterface.Activities.ScannerActivity
import java.io.IOException

abstract class WaresCatalogActivity : CatalogActivity<Order>() {
    private val MY_CAMERA_REQUEST_CODE = 100


    override fun onScanClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            val arr = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, arr , MY_CAMERA_REQUEST_CODE)

        }else{
            val intent = Intent(this@WaresCatalogActivity, ScannerActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            val item = data!!.getSerializableExtra("scanner_result") as Ware
            val order = Order(item)
            adapter.addItem(order)
            adapter.filter()
            adapter.notifyDataSetChanged()

            finish_button.isEnabled = true

            if (item.isFoto) GetPhotoTask(order).execute()
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //TODO
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this@WaresCatalogActivity, ScannerActivity::class.java)
                startActivity(intent)
            } else {
            }
        }
    }

    override fun onListEmptied() {
        finish_button.isEnabled = false
    }

    inner class GetPhotoTask(val order: Order)  :
            AsyncTask<Int, String, Bitmap?>() {
        private val waresClient = WaresClientImpl()

        override fun doInBackground(vararg params: Int?): Bitmap? {
            val user = User.getLoggedUser(this@WaresCatalogActivity)

            return try {
                waresClient.getPhoto(order.ware.towID, user!!.token!!)
            }catch (e : IOException){
                return null
            }
        }


        override fun onPostExecute(result: Bitmap?) {

//            if (result == null) Toast.makeText(this@WaresCatalogActivity, "Do tego produktu nie przypisano zdjęcia", Toast.LENGTH_SHORT).show()
//            else{
//                order.imageBitmap = result
//                adapter.filter()
//                adapter.notifyDataSetChanged()
//
//            }

            if (result != null){
                order.imageBitmap = result
                adapter.filter()
                adapter.notifyDataSetChanged()

            }

        }

        override fun onCancelled() {
        }
    }
}