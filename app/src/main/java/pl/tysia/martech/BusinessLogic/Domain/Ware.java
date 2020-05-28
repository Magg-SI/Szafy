package pl.tysia.martech.BusinessLogic.Domain;

import java.io.Serializable;

public class Ware implements Serializable {
    private String title;
    private int towID;
    private String indeks;
    private boolean isFoto = false;

    public Ware(String title) {
        this.title = title;
    }

    public Ware(String title, int towID, String indeks, boolean isFoto) {
        this.title = title;
        this.towID = towID;
        this.indeks = indeks;
        this.isFoto = isFoto;
    }

    public String getTitle() {
        return title;
    }

    public int getTowID() {
        return towID;
    }

    public String getIndeks() {
        return indeks;
    }

    public boolean isFoto() {
        return isFoto;
    }
}
