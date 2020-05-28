package pl.tysia.martech.Presentation.PresentationLogic.Catalogs;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.tysia.martech.BusinessLogic.Domain.Locker;
import pl.tysia.martech.R;

public class LockersCatalogAdapter extends CatalogAdapter<Locker> {
    public LockersCatalogAdapter(ArrayList items) {
        super(items);
    }

    public class LockerViewHolder extends CatalogAdapter.CatalogItemViewHolder {
        TextView title;
        TextView description;
        View back;

        public LockerViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.locker_name_tv);
            description = v.findViewById(R.id.locker_desc_tv);
            back = v.findViewById(R.id.back);


            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            onItemClick(v, pos);
        }

    }

    @Override
    public LockerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_locker, parent, false);

        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        LockerViewHolder vh = new LockerViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.CatalogItemViewHolder catalogItemViewHolder, int i) {
        Locker item = shownItems.get(i);

        LockerViewHolder lockerViewHolder = (LockerViewHolder) catalogItemViewHolder;

        lockerViewHolder.title.setText(item.getTitle());
        item.getShortDescription();

        if (item.equals(selectedItem)) lockerViewHolder.back.setBackgroundResource(R.color.lightSelectionGray);

        lockerViewHolder.description.setText(item.getShortDescription());

    }
}
