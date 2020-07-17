package pl.tysia.martech.BusinessLogic.Domain

import android.content.Context
import android.preference.PreferenceManager
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType

class User(var login: String) {
    var password : String? = null
    var type : UserType? = null
    var id : Int? = null
    var token : String? = null
    var lockerID : Int? = null
    var lockerNr : String? = null

    constructor(login : String, password : String) : this(login) {
        this.password = password
    }

    constructor(login: String, id: Int, type : UserType, token : String, locker : Int) : this(login){
        this.id = id
        this.type = type
        this.token = token
        this.lockerID = locker
    }

    fun setLogged(context : Context){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(LOGIN, login)
        if (id != null) editor.putInt(ID, id!!)
        editor.putInt(LOCKER_ID, lockerID!!)
        editor.putString(LOCKER_NR, lockerNr)
        editor.putString(TOKEN, token)
        editor.putString(TYPE, type?.typeName)
        editor.commit()
    }


    companion object {
        const val LOGIN = "logged_user_login"
        const val ID = "logged_user_id"
        const val LOCKER_ID = "logged_user_locker"
        const val LOCKER_NR = "logged_user_locker_nr"
        const val TOKEN = "logged_user_token"
        const val TYPE = "logged_user_type"

        fun getLoggedUser(context : Context) : User?{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val login = sharedPreferences.getString(LOGIN, null)
            val id = sharedPreferences.getInt(ID, -1)
            val locker = sharedPreferences.getInt(LOCKER_ID, 0)
            val lockerNr = sharedPreferences.getString(LOCKER_NR, null)
            val typeStr = sharedPreferences.getString(TYPE, null)
            val type: UserType? = UserType.fromString(typeStr)
            val token: String? = sharedPreferences.getString(TOKEN, null)

            return if (login != null && id>=0 && type != null && token != null){
                val user = User(login, id, type, token, locker)
                user.lockerNr = lockerNr
                user
            }else null

        }

        fun logout(context : Context){
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()

            editor.putString(LOGIN, null)
            editor.putInt(ID, -1)
            editor.putInt(LOCKER_ID, -1)
            editor.putString(LOCKER_NR, null)
            editor.putString(TYPE, null)
            editor.putString(TOKEN, null)
            editor.commit()
        }
    }
}