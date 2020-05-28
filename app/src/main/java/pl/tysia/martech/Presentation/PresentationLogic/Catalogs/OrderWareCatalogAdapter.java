package pl.tysia.martech.Presentation.PresentationLogic.Catalogs;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.tysia.martech.BusinessLogic.Domain.Order;
import pl.tysia.martech.R;

public class OrderWareCatalogAdapter extends CatalogAdapter<Order> {

    public OrderWareCatalogAdapter(ArrayList<Order> items) {
        super(items);
    }

    public class WareViewHolder extends CatalogAdapter.CatalogItemViewHolder {
        View back;
        ImageView image;
        TextView title;
        TextView description;

        EditText numberPicker;
        Button numberPickerMinus;
        Button numberPickerPlus;
        ImageButton deleteButton;


        public WareViewHolder(View v) {
            super(v);

            back = v.findViewById(R.id.back_view);
            image = v.findViewById(R.id.image_view);
            title = v.findViewById(R.id.title_text_view);
            description = v.findViewById(R.id.description_text_view);
            numberPicker = v.findViewById(R.id.numberPicker);
            numberPickerMinus = v.findViewById(R.id.numberPickerMinus);
            numberPickerPlus = v.findViewById(R.id.numberPickerPlus);

            deleteButton = v.findViewById(R.id.deleteButton);

            v.setOnClickListener(this);

            numberPicker.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()){
                        int n = Integer.valueOf(s.toString());
                        shownItems.get(getAdapterPosition()).setQuantityOrdered(n);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allItems.remove(getAdapterPosition());
                    filter();
                    notifyDataSetChanged();

                    if (allItems.isEmpty())
                        for (EmptyListListener listener : emptyListeners)
                            listener.onListEmptied();
                }
            });

            numberPickerPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker.getText().toString());
                    numberPicker.setText(Integer.toString(n + 1));
                    shownItems.get(getAdapterPosition()).setQuantityOrdered(n + 1);
                    notifyDataSetChanged();
                }
            });

            numberPickerMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker.getText().toString());
                    if (n > 1) {
                        numberPicker.setText(Integer.toString(n - 1));
                        shownItems.get(getAdapterPosition()).setQuantityOrdered(n - 1);
                        notifyDataSetChanged();
                    }
                }
            });


        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            onItemClick(v, pos);
        }

    }

    @Override
    public void addItem(Order item) {
        super.addItem(item);

        item.setQuantityOrdered(1);
        filter();
        notifyDataSetChanged();
    }

    @Override
    public WareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ware_order, parent, false);

        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        WareViewHolder vh = new WareViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogAdapter.CatalogItemViewHolder catalogItemViewHolder, int i) {
        Order item = shownItems.get(i);

        WareViewHolder wareViewHolder = (WareViewHolder) catalogItemViewHolder;

        wareViewHolder.title.setText(item.getTitle());
        if (item.getShortDescription() == null) wareViewHolder.description.setVisibility(View.GONE);

        wareViewHolder.description.setText(item.getShortDescription());

        wareViewHolder.numberPicker.setText(Integer.toString(item.getQuantityOrdered()));

        if (item.getImageBitmap() != null) wareViewHolder.image.setImageBitmap(item.getImageBitmap());
        else wareViewHolder.image.setImageResource(R.drawable.baseline_insert_photo_24);
    }

}
