package io.thoughtbox.hamdan.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.NotificationCardBinding;
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseData;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<NotificationResponseData> notificationList;

    public NotificationAdapter(ArrayList<NotificationResponseData> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.notification_card,
                parent,
                false
        );
        return new NotificationViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationResponseData notificationResponseData = notificationList.get(position);

        holder.listItemBinding.title.setText(notificationResponseData.getTitle());
        holder.listItemBinding.body.setText(notificationResponseData.getBody());
        holder.listItemBinding.date.setText(notificationResponseData.getDate());
        holder.listItemBinding.time.setText(notificationResponseData.getTime());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        NotificationCardBinding listItemBinding;

        public NotificationViewHolder(@NonNull NotificationCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
        }
    }
}
