package com.spritelab.netconnect.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spritelab.netconnect.model.ServiceModel;
import com.spritelab.netconnect.R;
import com.spritelab.netconnect.model.ServiceModel;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceModel> serviceList;

    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ServiceViewHolder holder, int position) {
        ServiceModel current = serviceList.get(position);

        holder.url.setText(current.getUrl());

        if (current.isSuccessfully()) {
            holder.isSuccessfully.setImageResource(R.drawable.yes);
            holder.url.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.isSuccessfully.setImageResource(R.drawable.no);
            holder.url.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.crimson));
        }

        holder.isSuccessfully.setTranslationX(-50f);
        holder.isSuccessfully.animate()
                .setDuration(250)
                .translationX(0f)
                .start();
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView isSuccessfully;
        TextView url;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            isSuccessfully = itemView.findViewById(R.id.isSuccessfully);
            url = itemView.findViewById(R.id.url);
        }
    }
}