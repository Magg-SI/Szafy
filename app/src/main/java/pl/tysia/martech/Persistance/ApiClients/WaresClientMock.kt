package pl.tysia.martech.Persistance.ApiClients

import android.graphics.Bitmap
import pl.tysia.martech.BusinessLogic.Domain.Ware
import pl.tysia.martech.BusinessLogic.Domain.Order

class WaresClientMock : WaresClient {
    override fun getPhoto(id: Int, token: String): Bitmap {
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }

    override fun stocktake(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        return true
    }

    override fun orderWares(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        return true
    }

    override fun takeWares(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        return true
    }

    override fun getWare(qrCode: String, lockerID : Int, token: String): Ware {
        return Ware("Test ware ")
    }



}