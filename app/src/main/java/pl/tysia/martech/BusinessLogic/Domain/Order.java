package pl.tysia.martech.BusinessLogic.Domain;

import android.graphics.Bitmap;

import java.io.Serializable;

import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable;
import pl.tysia.martech.R;
import pl.tysia.martech.BusinessLogic.Domain.Ware;

public class Order implements ICatalogable, Serializable {
    private Ware ware;
    private int quantityOrdered = 0;
    private int quantityTaken = 0;
    private Bitmap imageBitmap;


    public Order(Ware ware) {
        this.ware = ware;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Ware getWare() {
        return ware;
    }

    public void setWare(Ware ware) {
        this.ware = ware;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public int getQuantityTaken() {
        return quantityTaken;
    }

    public void setQuantityTaken(int quantityTaken) {
        this.quantityTaken = quantityTaken;
    }

    @Override
    public String getTitle() {
        return ware.getTitle();
    }

    @Override
    public String getShortDescription() {
        return ware.getIndeks();
    }

    @Override
    public Bitmap getImage() {
        return imageBitmap;
    }

}