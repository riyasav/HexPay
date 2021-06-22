package io.thoughtbox.hamdan.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.RateCardBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {
    @Inject
    Dictionary dictionary;
    private ArrayList<RateResponseData> rateList;

    public RateAdapter(ArrayList<RateResponseData> rateList) {
        this.rateList = rateList;
        DaggerApiComponents.create().inject(this);
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RateCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.rate_card,
                parent,
                false
        );
        return new RateViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        RateResponseData rateResponseData = rateList.get(position);
        holder.listItemBinding.setInfo(rateResponseData);
        holder.listItemBinding.setLanguage(dictionary);
        if (rateResponseData.getTxntype().equals("Bank Transfer")) {
            holder.listItemBinding.icon.setImageResource(R.drawable.ic_bank_transfer);
        } else {
            holder.listItemBinding.icon.setImageResource(R.drawable.ic_cash_pickup);
        }
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        RateCardBinding listItemBinding;

        public RateViewHolder(RateCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
        }
    }
}
