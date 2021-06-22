package io.thoughtbox.hamdan.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ReportCardBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.reportModel.ReportResponseData;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    @Inject
    Dictionary dictionary;
    private ReportListener clickListener;
    private ArrayList<ReportResponseData> reportList;

    public ReportAdapter(ReportListener clickListener, ArrayList<ReportResponseData> reportList) {
        this.clickListener = clickListener;
        this.reportList = reportList;
        DaggerApiComponents.create().inject(this);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReportCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.report_card,
                parent,
                false
        );
        return new ReportViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportResponseData responseData = reportList.get(position);
        holder.listItemBinding.setInfo(responseData);
        holder.listItemBinding.setLanguage(dictionary);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        ReportCardBinding listItemBinding;

        public ReportViewHolder(ReportCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(reportList.get(clickedPosition));
                }
            });
            listItemBinding.details.setOnClickListener(view->{
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(reportList.get(clickedPosition));
                }
            });
            listItemBinding.repeat.setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onRepeatClick(reportList.get(clickedPosition));
                }
            });
        }
    }
}
