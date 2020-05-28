package pl.tysia.martech.Presentation.PresentationLogic.Filterer;

import java.util.ArrayList;
import java.util.LinkedList;

import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.IFilterable;

public abstract class Filterer<T extends IFilterable> {
    ArrayList<T> basicList;
    ArrayList<T> filteredList;

    LinkedList<Filter> filters;

    public Filterer(ArrayList<T> basicList, ArrayList<T> filteredList) {
        this.basicList = basicList;
        this.filteredList = filteredList;

        this.filters = new LinkedList<>();
    }

    public void addFilter(Filter filter){
        filters.add(filter);
        filter();
    }

    public void removeFilter(Filter filter){
        filters.remove(filter);
        filter();
    }

    public void notifyFilterChange(){
        filter();
    }

    public ArrayList<T> filter(){
        filteredList.clear();

        if (filters.isEmpty()) filteredList.addAll(basicList);

        else for (T filterable: basicList){
            boolean fits = true;

            for (Filter filter : filters) {
                if (!filter.fitsFilter(filterable))
                    fits = false;
                    break;
            }

            if (fits)
            filteredList.add(filterable);

        }

        return filteredList;
    }

    public ArrayList<T> getBasicList() {
        return basicList;
    }

    public void setBasicList(ArrayList<T> basicList) {
        this.basicList = basicList;
    }

    public ArrayList<T> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(ArrayList<T> filteredList) {
        this.filteredList = filteredList;
    }
}
