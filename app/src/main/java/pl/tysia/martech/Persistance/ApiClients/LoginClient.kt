package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User

interface LoginClient {
    fun authenticateUser(user: User) : Int
    fun changePassword(user : User, password : String) : Boolean


    companion object{
        const val OK = 0
        const val WRONG_PASSWORD = 2
        const val WRONG_LOGIN = 1
    }
}