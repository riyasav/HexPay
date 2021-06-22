package io.thoughtbox.hamdan.views.branches;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.LocationCardBinding;
import io.thoughtbox.hamdan.model.mapModel.Data;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> implements Filterable {
    Context context;
    public  BranchCallback clickListener;
    private ArrayList<Data> mainList;
    private ArrayList<Data> filterableList;

    public LocationAdapter(Context context, BranchCallback clickListener, ArrayList<Data> mainList) {
        this.context = context;
        this.clickListener = clickListener;
        this.mainList = mainList;
        this.filterableList = mainList;
    }

    @NonNull
    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocationCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.location_card,
                parent,
                false
        );
        return new LocationViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.LocationViewHolder holder, int position) {
        Data locationData = filterableList.get(position);
        holder.listItemBinding.setLocation(locationData);
//        holder.listItemBinding.call.setOnClickListener(view -> clickListener.onCallClicked(locationData.getContact()));
//        holder.listItemBinding.locate.setOnClickListener(view -> clickListener.onLocateClicked(locationData));
    }

    @Override
    public int getItemCount() {
        return filterableList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterableList = mainList;
                } else {
                    ArrayList<Data> filteredList = new ArrayList<>();
                    for (Data row : mainList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filterableList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterableList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterableList = (ArrayList<Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        LocationCardBinding listItemBinding;

        public LocationViewHolder(LocationCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onLocateClicked(filterableList.get(clickedPosition));
                }
            });
        }

    }
}