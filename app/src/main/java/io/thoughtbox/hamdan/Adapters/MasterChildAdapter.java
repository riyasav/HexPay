package io.thoughtbox.hamdan.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.DetailListItemBinding;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;


public class MasterChildAdapter extends RecyclerView.Adapter<MasterChildAdapter.MasterSelectionBottoSheetViewHolder> implements Filterable {
    private DetailedBottomSheet bottomSheetClickListner;
    private ArrayList<DetailData> listItems;
    private ArrayList<DetailData> listFiltered;
    private String tag;

    public MasterChildAdapter(DetailedBottomSheet bottomSheetClickListner, ArrayList<DetailData> listItems, String tag) {
        this.bottomSheetClickListner = bottomSheetClickListner;
        this.listItems = listItems;
        this.tag = tag;
        this.listFiltered = listItems;
    }

    @NonNull
    @Override
    public MasterSelectionBottoSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DetailListItemBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.detail_list_item,
                parent,
                false
        );
        return new MasterSelectionBottoSheetViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterSelectionBottoSheetViewHolder holder, int position) {
        DetailData selectedItem = listFiltered.get(position);
        holder.listItemBinding.setValues(selectedItem);

        if (position % 2 == 1) {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#F8F8F8"));
        }
        holder.listItemBinding.getRoot().setOnClickListener(view -> bottomSheetClickListner.BottomDetailedItemClickListner(selectedItem, tag));

    }

    @Override
    public int getItemCount() {

        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = listItems;
                } else {
                    ArrayList<DetailData> filteredList = new ArrayList<>();
                    for (DetailData row : listItems) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<DetailData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MasterSelectionBottoSheetViewHolder extends RecyclerView.ViewHolder {
        DetailListItemBinding listItemBinding;

        public MasterSelectionBottoSheetViewHolder(DetailListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
        }
    }
}
