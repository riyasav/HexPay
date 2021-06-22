package io.thoughtbox.hamdan.fragments.rates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.CurrencyListItemBinding;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;


public class CurrencySelectionBottomSheetAdapter extends RecyclerView.Adapter<CurrencySelectionBottomSheetAdapter.SelectionBottoSheetViewHolder> implements Filterable {

    CurrencyRatesListener bottomSheetClickListner;
    ArrayList<RateResponseData> listItems;
    ArrayList<RateResponseData> listFiltered;
    String tag;
    Context context;

    public CurrencySelectionBottomSheetAdapter(Context context, CurrencyRatesListener bottomSheetClickListner, ArrayList<RateResponseData> listItems, String tag) {
        this.context = context;
        this.bottomSheetClickListner = bottomSheetClickListner;
        this.listItems = listItems;
        this.tag = tag;
        this.listFiltered = listItems;
    }

    @NonNull
    @Override
    public CurrencySelectionBottomSheetAdapter.SelectionBottoSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CurrencyListItemBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.currency_list_item,
                parent,
                false
        );
        return new SelectionBottoSheetViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencySelectionBottomSheetAdapter.SelectionBottoSheetViewHolder holder, int position) {
        RateResponseData selectionModal = listFiltered.get(position);
        holder.listItemBinding.setValues(selectionModal);
        Glide.with(context)
                .load(selectionModal.getCountryflag())
                .into(new CustomTarget<Drawable>(40,40) {

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                    {
                        holder.listItemBinding.title.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder)
                    {
                        holder.listItemBinding.title.setCompoundDrawablesWithIntrinsicBounds(placeholder,null , null, null);
                    }
                });


        if (position % 2 == 1) {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.listItemBinding.view.setCardBackgroundColor(Color.parseColor("#F8F8F8"));
        }
        holder.listItemBinding.getRoot().setOnClickListener(view -> bottomSheetClickListner.onCurrencyListClickListener(selectionModal));
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
                    ArrayList<RateResponseData> filteredList = new ArrayList<>();
                    for (RateResponseData row : listItems) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCurrency().toLowerCase().contains(charString.toLowerCase()) || row.getCurrency().contains(charSequence)) {
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
                listFiltered = (ArrayList<RateResponseData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class SelectionBottoSheetViewHolder extends RecyclerView.ViewHolder {
        CurrencyListItemBinding listItemBinding;

        public SelectionBottoSheetViewHolder(CurrencyListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
        }
    }
}
