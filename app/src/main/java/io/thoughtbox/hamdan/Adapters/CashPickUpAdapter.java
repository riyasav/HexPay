package io.thoughtbox.hamdan.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.CashpickupBenCardBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;

public class CashPickUpAdapter extends RecyclerView.Adapter<CashPickUpAdapter.CashPickUpViewHolder> implements Filterable {
    @Inject
    Dictionary dictionary;
    private OnCashPickUpClicked clickListener;
    private ArrayList<CashPickUpResponseData> mainBenList;
    private ArrayList<CashPickUpResponseData> filterBenList;
    private String[] mColors = {"73c9e2", "d4145a", "4cdea5", "7042ce", "3e4475"};

    public CashPickUpAdapter(OnCashPickUpClicked clickListener, ArrayList<CashPickUpResponseData> mainBenList) {
        this.clickListener = clickListener;
        this.mainBenList = mainBenList;
        DaggerApiComponents.create().inject(this);
    }

    @NonNull
    @Override
    public CashPickUpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CashpickupBenCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.cashpickup_ben_card,
                parent,
                false
        );
        return new CashPickUpViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CashPickUpViewHolder holder, int position) {
        CashPickUpResponseData cashPickUpResponseData = mainBenList.get(position);
        try {
            int i = new Random().nextInt(5);
            holder.listItemBinding.icon.setCardBackgroundColor(Color.parseColor("#" + mColors[new Random().nextInt(i)]));
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }

        holder.listItemBinding.titleLetter.setText(getFirstLetterUppercase(cashPickUpResponseData.getName()));
        holder.listItemBinding.setCardData(cashPickUpResponseData);
        holder.listItemBinding.setLanguage(dictionary);

    }

    @Override
    public int getItemCount() {
        return mainBenList.size();
    }

    public String setUpperCase(String name) {
        if (name != null && name.length() > 1) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }

    public String getFirstLetterUppercase(String name) {
        if (name.length() > 1) {
            return String.valueOf(setUpperCase(name).charAt(0));
        }
        return "";
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterBenList = mainBenList;
                } else {
                    ArrayList<CashPickUpResponseData> filteredList = new ArrayList<>();
                    for (CashPickUpResponseData row : mainBenList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getAgent().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filterBenList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterBenList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterBenList = (ArrayList<CashPickUpResponseData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CashPickUpViewHolder extends RecyclerView.ViewHolder {
        CashpickupBenCardBinding listItemBinding;

        public CashPickUpViewHolder(@NonNull CashpickupBenCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;

            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onItemclicked(mainBenList.get(clickedPosition));
                }
            });
        }
    }
}
