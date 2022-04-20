package pl.tysia.martech.Persistance.ApiClients

import android.graphics.Bitmap
import pl.tysia.martech.BusinessLogic.Domain.Ware
import pl.tysia.martech.BusinessLogic.Domain.Order
import java.io.IOException



interface WaresClient {

    @Throws(IOException::class)
    fun getWare(qrCode : String, lockerID : Int, token : String) : Ware?

    @Throws(IOException::class)
    fun takeWares(wares: List<Order>, lockerNumber : Int, token : String) : Int

    @Throws(IOException::class)
    fun backWares(wares: List<Order>, lockerNumber : Int, token : String) : Int

    @Throws(IOException::class)
    fun orderWares(wares: List<Order>, lockerNumber : Int, token : String) : Boolean

    @Throws(IOException::class)
    fun stocktake(wares: List<Order>, lockerNumber : Int, token : String) : Boolean

    @Throws(IOException::class)
    fun getPhoto(id : Int, token : String) : Bitmap?
}