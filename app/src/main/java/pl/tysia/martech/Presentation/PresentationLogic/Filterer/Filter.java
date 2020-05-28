package pl.tysia.martech.Presentation.PresentationLogic.Filterer;


import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.IFilterable;

public abstract class Filter<T extends IFilterable> {

    public abstract boolean fitsFilter(T item);
}
