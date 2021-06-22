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
import io.thoughtbox.hamdan.databinding.ListItemBinding;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;

public class SelectionBottomSheetAdapter extends RecyclerView.Adapter<SelectionBottomSheetAdapter.SelectionBottoSheetViewHolder> implements Filterable {

    BottomSheetClickListner bottomSheetClickListner;
    ArrayList<SelectionModal> listItems;
    ArrayList<SelectionModal> listFiltered;
    String tag;

    public SelectionBottomSheetAdapter(BottomSheetClickListner bottomSheetClickListner, ArrayList<SelectionModal> listItems, String tag) {
        this.bottomSheetClickListner = bottomSheetClickListner;
        this.listItems = listItems;
        this.tag = tag;
        this.listFiltered = listItems;
    }

    @NonNull
    @Override
    public SelectionBottoSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item,
                parent,
                false
        );
        return new SelectionBottoSheetViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionBottoSheetViewHolder holder, int position) {
        SelectionModal selectionModal = listFiltered.get(position);
        holder.listItemBinding.setValues(selectionModal);

        final String Id = listFiltered.get(position).getId();
        final String Name = listFiltered.get(position).getName();

        if (position % 2 == 1) {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#F8F8F8"));
        }
        holder.listItemBinding.getRoot().setOnClickListener(view -> bottomSheetClickListner.BottomItemClickListner(Name, Id, tag));
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
                    ArrayList<SelectionModal> filteredList = new ArrayList<>();
                    for (SelectionModal row : listItems) {

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
                listFiltered = (ArrayList<SelectionModal>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class SelectionBottoSheetViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding listItemBinding;

        public SelectionBottoSheetViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
        }
    }
}
