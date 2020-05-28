package pl.tysia.martech.Persistance.ApiClients

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import pl.tysia.martech.BusinessLogic.Domain.Locker
import pl.tysia.martech.Persistance.Connection.URLConnectionManagerImpl
import java.net.URL

class LockerClientImpl() : LockerClient {
    companion object{
        private const val MAIN_URL = "http://martech.magg.pl/szafa.aspx"
        private const val JSON_ERROR_CODE = "errCode"
        private const val OK = 0
        private const val ALREADY_OPEN = 2
        private const val WAITING_TO_OPEN = 1
    }

    override fun getLockersList(token: String): List<Locker>? {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getLockersListJSON(token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        if(resCode != OK) return null

        val jsonArray = jsonRes.get("listaSzaf") as JSONArray

        var lockers = ArrayList<Locker>()

        for (i in 0 until jsonArray.length()){

            val jsonObj = jsonArray[i] as JSONObject

            val id = jsonObj.getInt("id")
            val numer = jsonObj.getString("numer")
            val firma = jsonObj.getString("firma")
            val opis = jsonObj.getString("opis")

            lockers.add(Locker(id, numer, firma, opis))

        }

        return lockers

    }

    private fun getLockersListJSON(token : String): String{
        val json = JSONObject()

        json.put("func", "listaSzaf")
        json.put("token", token)

        return json.toString()
    }

    private val connectionManager = URLConnectionManagerImpl()

    override fun openLocker(lockerNumber: Int, token : String): Boolean {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getOpenLockerJSONStr(lockerNumber, token))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)

        if (resCode == OK ||  resCode == WAITING_TO_OPEN) return false
        else if (resCode == ALREADY_OPEN ) return true

        return false
    }

    private fun getOpenLockerJSONStr(lockerNumber: Int, token : String): String{
        val json = JSONObject()

        json.put("func", "openSzafa")
        json.put("token", token)
        json.put("szafaID", lockerNumber)

        return json.toString()
    }
}