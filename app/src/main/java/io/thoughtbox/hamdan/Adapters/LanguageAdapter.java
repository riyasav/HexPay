package io.thoughtbox.hamdan.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.LangCardBinding;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponseData;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewholder> {
    private ArrayList<LanguageResponseData> langList;
    private LanguageClickListner clickListener;
    private String langaugeUsed;

    public LanguageAdapter(ArrayList<LanguageResponseData> langList, LanguageClickListner clickListener, String langaugeUsed) {
        this.langList = langList;
        this.clickListener = clickListener;
        this.langaugeUsed = langaugeUsed;
    }

    @NonNull
    @Override
    public LanguageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LangCardBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.lang_card,
                parent,
                false
        );
        return new LanguageViewholder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewholder holder, int position) {

        holder.listItemBinding.name.setText(langList.get(position).getText());
        if (langList.get(position).getName().equals(langaugeUsed)) {
            holder.listItemBinding.card.setBackgroundColor(Color.parseColor("#333333"));
            holder.listItemBinding.name.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.listItemBinding.card.setBackgroundColor(Color.parseColor("#20fed604"));
            holder.listItemBinding.name.setTextColor(Color.parseColor("#212121"));
        }
    }

    @Override
    public int getItemCount() {
        return langList.size();
    }

    public class LanguageViewholder extends RecyclerView.ViewHolder {
        LangCardBinding listItemBinding;

        public LanguageViewholder(@NonNull LangCardBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> {
                int clickedPosition = getAdapterPosition();
//                row_index = clickedPosition;
                notifyDataSetChanged();
                if (clickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    clickListener.onLangClicked(langList.get(clickedPosition));
                }
            });
        }
    }
}
