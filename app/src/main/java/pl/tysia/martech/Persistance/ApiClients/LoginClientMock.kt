package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType

@Deprecated(message = "stop mocking me!")
class LoginClientMock : LoginClient{
    override fun changePassword(user: User, password: String): Boolean {
        return true
    }

    override fun authenticateUser(user: User): Int {
        if (user.login == "worker") user.type = UserType.WORKER
        else user.type = UserType.ADMIN
        user.id = 0
        return LoginClient.OK
    }

}