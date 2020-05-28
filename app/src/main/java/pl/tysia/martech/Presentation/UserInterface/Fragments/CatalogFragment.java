package pl.tysia.martech.Presentation.UserInterface.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.CatalogAdapter;
import pl.tysia.martech.Presentation.PresentationLogic.Catalogs.ICatalogable;
import pl.tysia.martech.Presentation.PresentationLogic.Filterer.Filterer;
import pl.tysia.martech.Presentation.PresentationLogic.Filterer.StringFilter;
import pl.tysia.martech.R;

@Deprecated
public abstract class CatalogFragment<T extends ICatalogable> extends Fragment implements TextWatcher, CatalogAdapter.ItemSelectedListener<T> {
    private ArrayList<T> catalogItems;
    protected CatalogAdapter<T> adapter;
    private StringFilter filter;

    private RecyclerView recyclerView;
    private EditText searchEditText;

    protected abstract ArrayList<T> getCatalogItems();

    public CatalogFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           /* catalogItems =  getCatalogItems();
            adapter = new CatalogAdapter<T>(catalogItems);
            adapter.addItemSelectedListener(this);

            Filterer filterer = adapter.getFilterer();
            filter = new StringFilter();
            filterer.addFilter(filter);
*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);

        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.filter();
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        filter.setFilteredString(s.toString());
        adapter.filter();
        adapter.notifyDataSetChanged();

    }


}
