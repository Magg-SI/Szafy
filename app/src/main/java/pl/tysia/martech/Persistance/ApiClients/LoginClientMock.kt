package pl.tysia.maggwarehouse.Persistance

import pl.tysia.maggwarehouse.BusinessLogic.Domain.User
import pl.tysia.maggwarehouse.BusinessLogic.Domain.UserType
import pl.tysia.martech.Persistance.Result

@Deprecated(message = "stop mocking me!")
class LoginClientMock : LoginClient{
    override fun changePassword(user: User, password: String): Result<Boolean> {
        return Result(true, 0, "OK")
    }

    override fun checkToken(user: User): Result<Boolean> {
        return Result(true, 0, "OK")
    }

    override fun authenticateUser(user: User): Result<User> {
        if (user.login == "worker") user.type = UserType.WORKER
        else user.type = UserType.ADMIN
        user.id = 0
        return Result(user, 0, "OK")
    }

}