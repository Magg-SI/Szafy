package pl.tysia.martech.Persistance.ApiClients

import pl.tysia.martech.BusinessLogic.Domain.Locker
import java.util.concurrent.locks.Lock

@Deprecated(message = "stop mocking me! >w<")
class LockerClientMock : LockerClient {
    override fun getLockersList(token: String): List<Locker> {
        return arrayListOf(Locker(1, "numeeeer", "comp", "aaaaaaaaaadesc"))
    }

    override fun openLocker(lockerNumber: Int, token : String): Boolean {
        return true
    }
}