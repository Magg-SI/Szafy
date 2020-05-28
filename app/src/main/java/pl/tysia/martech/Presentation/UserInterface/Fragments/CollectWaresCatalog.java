package pl.tysia.martech.Presentation.UserInterface.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pl.tysia.martech.Persistance.MockWaresDAO;
import pl.tysia.martech.BusinessLogic.Domain.Order;
import pl.tysia.martech.Presentation.UserInterface.Activities.ScannerActivity;
import pl.tysia.martech.R;

@Deprecated
public class CollectWaresCatalog extends CatalogFragment<Order> {

    @Override
    protected ArrayList<Order> getCatalogItems() {
        return MockWaresDAO.getWares();
    }

    public CollectWaresCatalog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        FloatingActionButton scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSlabClicked();
            }
        });

        return view;
    }

    public void onAddSlabClicked(){
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){
            Order ware= (Order) data.getSerializableExtra("scanner_result");
            adapter.addItem(ware);
            adapter.filter();
            adapter.notifyDataSetChanged();

        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //TODO
        }
    }


    public static CollectWaresCatalog newInstance() {
        CollectWaresCatalog fragment = new CollectWaresCatalog();

        return fragment;
    }


    @Override
    public void onItemSelected(Order item) {

    }
}
