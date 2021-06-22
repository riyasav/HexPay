package io.thoughtbox.hamdan.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.NotificationCardBinding;
import io.thoughtbox.hamdan.databinding.RewardCardBinding;
import io.thoughtbox.hamdan.model.rewardsModel.RewardsResponseData;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder> {
    private ArrayList<RewardsResponseData> rewardsList;
    private RewardClickListener rewardClickListener;

    public RewardsAdapter(ArrayList<RewardsResponseData> rewardsList, RewardClickListener rewardClickListener) {
        this.rewardsList = rewardsList;
        this.rewardClickListener = rewardClickListener;
    }

    @NonNull
    @Override
    public RewardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RewardCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.reward_card,
                parent,
                false
        );
        return new RewardsViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardsViewHolder holder, int position) {
        RewardsResponseData rewardsResponseData = rewardsList.get(position);
        holder.listItemBinding.setInfo(rewardsResponseData);
    }

    @Override
    public int getItemCount() {
        return rewardsList.size();
    }

    public class RewardsViewHolder extends RecyclerView.ViewHolder {
        RewardCardBinding listItemBinding;

        public RewardsViewHolder(@NonNull RewardCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (rewardClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    rewardClickListener.onRewardClicked(rewardsList.get(clickedPosition));
                }
            });
        }
    }
}
