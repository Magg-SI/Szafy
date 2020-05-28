package pl.tysia.martech.BusinessLogic.Domain

import android.graphics.Bitmap
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable

class Locker(var id : Int, var number : String, var company : String, var  description : String) : ICatalogable{

    override fun getTitle(): String {
        return "$number, $company"
    }

    override fun getShortDescription(): String {
        return description
    }

    override fun getImage(): Bitmap? {
       return null
    }


}