package pl.tysia.martech.Persistance.ApiClients

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import pl.tysia.martech.BusinessLogic.Domain.Order
import pl.tysia.martech.BusinessLogic.Domain.Ware
import pl.tysia.martech.Persistance.Connection.URLConnectionManagerImpl
import java.net.URL



class WaresClientImpl : WaresClient {
    companion object{
        private const val MAIN_URL = "http://martech.magg.pl/szafa.aspx"
        private const val JSON_ERROR_CODE = "errCode"
        private const val OK = 0
        private const val DOC_ORDER = 1
        private const val DOC_TAKE = 2
        private const val DOC_STOCK = 3
    }

    override fun getPhoto(id: Int, token: String): Bitmap? {
        connectionManager.setConnection(URL(MAIN_URL))
        val res = connectionManager.post(getPhotoGetterJSON(id, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        return if (resCode == OK){
            return photoFromString(jsonRes.getString("foto"))
        }else{
            null
        }
    }

    private fun getPhotoGetterJSON(id: Int, token:String): String{
        val jsonObj = JSONObject()

        jsonObj.put("func", "getFoto")
        jsonObj.put("token",  token)
        jsonObj.put("towID" , id)

        return jsonObj.toString()
    }

    private fun photoFromString(string : String) :  Bitmap{
        val imageBytes = Base64.decode(string, 0)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private val connectionManager = URLConnectionManagerImpl()

    override fun getWare(qrCode: String, token: String): Ware? {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getGetWareJSONStr(qrCode, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        if (resCode != OK) return null

        val towID  = jsonRes.getInt("towID")
        val indeks  = jsonRes.getString("indeks")
        val nazwa  = jsonRes.getString("nazwa")
        val isFoto  = jsonRes.getBoolean("isFoto")

        return Ware(nazwa, towID, indeks, isFoto)
    }

    private fun getGetWareJSONStr(qrCode: String, token: String) : String{
        val json = JSONObject()

        json.put("func", "findQR")
        json.put("token", token)
        json.put("QR", qrCode)

        return json.toString()
    }

    override fun takeWares(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getSendDocJSON(wares, lockerNumber, token, DOC_TAKE, false))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        return resCode == OK
    }

    override fun orderWares(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getSendDocJSON(wares, lockerNumber, token, DOC_ORDER, true))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        return resCode == OK
    }

    override fun stocktake(wares: List<Order>, lockerNumber: Int, token : String): Boolean {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getSendDocJSON(wares, lockerNumber, token, DOC_STOCK, false))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        return resCode == OK
    }

    private fun getSendDocJSON(wares: List<Order>, lockerNumber: Int, token : String, type : Int, isOrder : Boolean) : String{
        val json = JSONObject()

        json.put("func","addDocum")
        json.put("token",token)
        json.put("szafaID",lockerNumber)
        json.put("docType",type)

        val jsonArray = JSONArray()

        for (order in wares){
            val obj = JSONObject()
            obj.put("towID", order.ware.towID)

            if (isOrder) obj.put("ilosc", order.quantityOrdered)
            else obj.put("ilosc", order.quantityTaken)

            jsonArray.put(obj)
        }

        json.put("docPozy",jsonArray)


        return json.toString()
    }
}