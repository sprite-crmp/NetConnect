package com.spritelab.netconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spritelab.netconnect.model.InformationModel;
import com.spritelab.netconnect.R;

import java.util.List;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InfoViewHolder> {

    private List<InformationModel> infoList;

    public InformationAdapter(List<InformationModel> serviceList) {
        this.infoList = serviceList;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_information, parent, false);
        return new InformationAdapter.InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationAdapter.InfoViewHolder holder, int position) {
        InformationModel current = infoList.get(position);

        holder.textInfo.setText(current.getTextInfo());
        holder.imgInfo.setImageResource(current.getImgInfo());

        holder.itemView.setTranslationX(-200f);
        holder.itemView.postDelayed(() -> {
            holder.itemView.animate()
                    .setDuration(250)
                    .translationX(0f)
                    .start();
        }, 50);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView textInfo;
        ImageView imgInfo;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);

            textInfo = itemView.findViewById(R.id.textInfo);
            imgInfo = itemView.findViewById(R.id.imgInfo);
        }
    }
}