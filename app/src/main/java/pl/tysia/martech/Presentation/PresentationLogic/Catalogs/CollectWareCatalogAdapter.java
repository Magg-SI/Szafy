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

public class CollectWareCatalogAdapter extends CatalogAdapter<Order> {

    public CollectWareCatalogAdapter(ArrayList<Order> items) {
        super(items);
    }

    public class WareViewHolder extends CatalogAdapter.CatalogItemViewHolder {
        View back;
        ImageView image;
        TextView title;
        TextView description;

        EditText numberPicker;
        EditText numberPicker2;
        Button numberPickerMinus2;
        Button numberPickerPlus2;
        Button numberPickerMinus;
        Button numberPickerPlus;
        ImageButton deleteButton;

        TextView orderTV;
        TextView orderedNumberTV;

        public WareViewHolder(View v) {
            super(v);

            back = v.findViewById(R.id.back_view);
            image = v.findViewById(R.id.image_view);
            title = v.findViewById(R.id.title_text_view);
            description = v.findViewById(R.id.description_text_view);
            numberPicker = v.findViewById(R.id.numberPicker);
            numberPickerMinus = v.findViewById(R.id.numberPickerMinus);
            numberPickerPlus = v.findViewById(R.id.numberPickerPlus);
            numberPicker2 = v.findViewById(R.id.numberPicker2);
            numberPickerMinus2 = v.findViewById(R.id.numberPickerMinus2);
            numberPickerPlus2 = v.findViewById(R.id.numberPickerPlus2);
            orderTV = v.findViewById(R.id.orderTV);
            orderedNumberTV = v.findViewById(R.id.orderedNumberTV);
            deleteButton = v.findViewById(R.id.deleteButton);

            v.setOnClickListener(this);

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


            numberPickerPlus2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker2.getText().toString());
                    numberPicker2.setText(Integer.toString(n + 1));
                    shownItems.get(getAdapterPosition()).setQuantityOrdered(n+1);
                    notifyDataSetChanged();
                }
            });

            numberPickerMinus2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker2.getText().toString());
                    if (n > 0) {
                        numberPicker2.setText(Integer.toString(n - 1));
                        shownItems.get(getAdapterPosition()).setQuantityOrdered(n - 1);
                        notifyDataSetChanged();
                    }
                }
            });

            numberPickerPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker.getText().toString());
                    numberPicker.setText(Integer.toString(n + 1));
                    shownItems.get(getAdapterPosition()).setQuantityTaken(n + 1);
                    notifyDataSetChanged();
                }
            });

            numberPickerMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberPicker.getText().toString());
                    if (n > 1) {
                        numberPicker.setText(Integer.toString(n - 1));
                        shownItems.get(getAdapterPosition()).setQuantityTaken(n - 1);
                        notifyDataSetChanged();
                    }
                }
            });

            orderTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shownItems.get(getAdapterPosition()).setQuantityOrdered(1);
                    notifyDataSetChanged();
                }
            });

            numberPicker.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()){
                        int n = Integer.valueOf(s.toString());
                        shownItems.get(getAdapterPosition()).setQuantityTaken(n);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            numberPicker2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()){
                        int n = Integer.valueOf(s.toString());
                        shownItems.get(getAdapterPosition()).setQuantityOrdered(n);

//                        if (n == 0) {
//                            filter();
//                            notifyDataSetChanged();
//                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

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

        item.setQuantityTaken(1);
        filter();
        notifyDataSetChanged();
    }

    @Override
    public WareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ware_collect, parent, false);

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

        if (item.getQuantityOrdered() > 0) {
            wareViewHolder.numberPicker2.setVisibility(View.VISIBLE);
            wareViewHolder.numberPicker2.setText(Integer.toString(item.getQuantityOrdered()));
            wareViewHolder.numberPickerMinus2.setVisibility(View.VISIBLE);
            wareViewHolder.numberPickerPlus2.setVisibility(View.VISIBLE);
            wareViewHolder.orderTV.setVisibility(View.GONE);
            wareViewHolder.orderedNumberTV.setVisibility(View.VISIBLE);
        }else {
            wareViewHolder.numberPicker2.setVisibility(View.INVISIBLE);
            wareViewHolder.numberPickerMinus2.setVisibility(View.INVISIBLE);
            wareViewHolder.numberPickerPlus2.setVisibility(View.INVISIBLE);
            //wareViewHolder.orderTV.setVisibility(View.VISIBLE);
            wareViewHolder.orderedNumberTV.setVisibility(View.INVISIBLE);
        }

        wareViewHolder.description.setText(item.getShortDescription());

        wareViewHolder.numberPicker.setText(Integer.toString(item.getQuantityTaken()));

        if (item.getImageBitmap() != null) wareViewHolder.image.setImageBitmap(item.getImageBitmap());
        else wareViewHolder.image.setImageResource(R.drawable.baseline_insert_photo_24);
    }



}
