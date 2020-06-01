package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.martech.Persistance.Result

interface LoginClient {
    fun authenticateUser(user: User) : Result<User>
    fun changePassword(user : User, password : String) : Result<Boolean>
    fun checkToken(user: User) : Result<Boolean>


    companion object{
        const val OK = 0
        const val WRONG_PASSWORD = 2
        const val WRONG_LOGIN = 1
    }
}