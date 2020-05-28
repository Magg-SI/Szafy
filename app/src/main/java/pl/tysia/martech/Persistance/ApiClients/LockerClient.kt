package pl.tysia.martech.Persistance.ApiClients

import pl.tysia.martech.BusinessLogic.Domain.Locker
import java.io.IOException

interface LockerClient {

    @Throws(IOException::class)
    fun openLocker(lockerNumber: Int, token : String): Boolean

    @Throws(IOException::class)
    fun getLockersList(token : String) : List<Locker>?
}
