package pl.tysia.martech.Presentation.PresentationLogic.Filterer;

import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable;

public class StringFilter extends Filter<ICatalogable> {
    private String filteredString;

    public StringFilter() {
    }

    public StringFilter(String filteredString) {
        this.filteredString = filteredString;
    }

    @Override
    public boolean fitsFilter(ICatalogable item) {
        if (filteredString == null) return true;
        else return item.getTitle().toLowerCase().contains(filteredString.toLowerCase());
    }

    public String getFilteredString() {
        return filteredString;
    }

    public void setFilteredString(String filteredString) {
        this.filteredString = filteredString;
    }
}
