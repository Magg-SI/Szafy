package pl.tysia.martech.Presentation.PresentationLogic.Filterer;

import java.util.ArrayList;

import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable;


public class CatalogFilterer<T extends ICatalogable> extends Filterer<T> {

    public CatalogFilterer(ArrayList<T> basicList, ArrayList<T> filteredList) {
        super(basicList, filteredList);

    }
}
/*
    ArrayList<CatalogFiltererWatcher> watchers;

    public void addWatcher(CatalogFiltererWatcher watcher){
        watchers.add(watcher);
    }

    public void removeWatcher(CatalogFiltererWatcher watcher){
        watchers.remove(watcher);
    }

    private void notifyWatchers(){
        for (CatalogFiltererWatcher watcher : watchers)
            watcher.filtersChanged();
    }



    @Override
    ArrayList<IFilterable> filter() {
        ArrayList<IFilterable> res =  super.filter();
        notifyWatchers();
        return res;
    }

    public interface CatalogFiltererWatcher{
        void filtersChanged();
    }*/
