package pl.tysia.martech.Presentation.PresentationLogic.Catalogs;

import android.graphics.Bitmap;

public interface ICatalogable extends IFilterable {
    String getTitle();
    String getShortDescription();
    Bitmap getImage();

}
