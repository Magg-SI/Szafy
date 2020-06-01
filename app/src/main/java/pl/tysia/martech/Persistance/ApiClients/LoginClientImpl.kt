package pl.tysia.martech.Persistance.ApiClients

import android.text.BoringLayout
import org.json.JSONObject
import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType
import pl.tysia.maggwarehouse.Persistance.LoginClient
import pl.tysia.martech.Persistance.Connection.URLConnectionManagerImpl
import pl.tysia.martech.Persistance.Result
import java.net.URL

class LoginClientImpl : LoginClient {
    private val connectionManager = URLConnectionManagerImpl()

    companion object{
        private const val MAIN_URL = "http://martech.magg.pl/szafa.aspx"
        private const val JSON_ERROR_CODE = "retCode"
        private const val JSON_ERROR_MESSAGE = "retOpis"
        private const val OK = 0
    }

    override fun changePassword(user: User, password: String): Result<Boolean> {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getChangePasswordJOSONStr(user, password))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        val resMessage = jsonRes.getString(JSON_ERROR_MESSAGE)

        return Result(resCode == OK, resCode, resMessage)
    }

    override fun checkToken(user: User): Result<Boolean> {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getCheckTokenJSONStr(user))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        val resMessage = jsonRes.getString(JSON_ERROR_MESSAGE)

        return Result(resCode == OK, resCode, resMessage)
    }

    private fun getCheckTokenJSONStr(user : User) : String {
        val json = JSONObject()

        json.put("func", "testToken")
        json.put("token", user.token)

        return json.toString()
    }

    private fun getChangePasswordJOSONStr(user : User, password: String) : String {
        val json = JSONObject()

        json.put("func",  "changePwd")
        json.put("token",  user.token)
        json.put("userID",  user.id)
        json.put("newPwd",  password)

        return json.toString()
    }

    override fun authenticateUser(user: User): Result<User> {
        connectionManager.setConnection(URL(MAIN_URL))
        val res= connectionManager.post(getAuthenticateUserJSONStr(user))
        connectionManager.closeConnection()

        val jsonRes = JSONObject(res)
        val resCode = jsonRes.getInt(JSON_ERROR_CODE)
        val resMessage = jsonRes.getString(JSON_ERROR_MESSAGE)

        val userID = jsonRes.getInt("userID")
        val token = jsonRes.getString("token")
        val isAdmin = jsonRes.getBoolean("isAdmin")
        val szafaID = jsonRes.getInt("szafaID")

        user.id = userID
        user.token = token
        user.type = if (isAdmin) UserType.ADMIN else UserType.WORKER
        user.lockerID  = szafaID

        return Result<User>(user, resCode, resMessage)
    }

    private fun getAuthenticateUserJSONStr(user: User) : String {
        val json = JSONObject()

        json.put("func" , "login")
        json.put("login" , user.login)
        json.put("password" , user.password)

        return json.toString()
    }
}