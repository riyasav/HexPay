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
import io.thoughtbox.hamdan.databinding.BankbenCardBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;

public class BankTransferAdapter extends RecyclerView.Adapter<BankTransferAdapter.BankTransferViewHolder> implements Filterable {
    @Inject
    Dictionary dictionary;
    private OnItemClicked clickListener;
    private ArrayList<BankBeneficiaryResponseData> mainBenList;
    private ArrayList<BankBeneficiaryResponseData> filterableBenList;

    private String[] mColors = {"73c9e2", "d4145a", "4cdea5", "7042ce", "3e4475"};

    public BankTransferAdapter(OnItemClicked clickListener, ArrayList<BankBeneficiaryResponseData> mainBenList) {
        this.clickListener = clickListener;
        this.mainBenList = mainBenList;
        DaggerApiComponents.create().inject(this);
        this.filterableBenList = mainBenList;
    }

    @NonNull
    @Override
    public BankTransferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BankbenCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.bankben_card,
                parent,
                false
        );
        return new BankTransferViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BankTransferViewHolder holder, int position) {
        BankBeneficiaryResponseData bankBeneficiaryResponseData = filterableBenList.get(position);

        try {
            int i = new Random().nextInt(5);
            holder.listItemBinding.icon.setCardBackgroundColor(Color.parseColor("#" + mColors[new Random().nextInt(i)]));
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }

        holder.listItemBinding.titleLetter.setText(getFirstLetterUppercase(bankBeneficiaryResponseData.getName()));
        holder.listItemBinding.setBankBenCardData(bankBeneficiaryResponseData);
        holder.listItemBinding.setLanguage(dictionary);
    }

    @Override
    public int getItemCount() {
        return filterableBenList.size();
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
                    filterableBenList = mainBenList;
                } else {
                    ArrayList<BankBeneficiaryResponseData> filteredList = new ArrayList<>();
                    for (BankBeneficiaryResponseData row : mainBenList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getAccountno().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filterableBenList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterableBenList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterableBenList = (ArrayList<BankBeneficiaryResponseData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class BankTransferViewHolder extends RecyclerView.ViewHolder {
        BankbenCardBinding listItemBinding;

        public BankTransferViewHolder(BankbenCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onBenItemClicked(filterableBenList.get(clickedPosition));
                }
            });
        }
    }
}
