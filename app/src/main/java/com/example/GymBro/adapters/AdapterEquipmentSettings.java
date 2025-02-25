package com.example.GymBro.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GymBro.R;
import com.example.GymBro.models.EquipmentModel;

import java.util.ArrayList;

public class AdapterEquipmentSettings extends RecyclerView.Adapter<AdapterEquipmentSettings.MyViewHolder> {

    private ArrayList<EquipmentModel> equipmentEquipments;

    public AdapterEquipmentSettings(ArrayList<EquipmentModel> equipmentEquipments) {
        this.equipmentEquipments = equipmentEquipments;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameEquipment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEquipment = itemView.findViewById(R.id.text_name_equipment);
        }
    }




    @NonNull
    @Override
    public AdapterEquipmentSettings.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_equipment_settings, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return  myViewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull AdapterEquipmentSettings.MyViewHolder holder, int position) {
        EquipmentModel equipment = equipmentEquipments.get(position);
        holder.nameEquipment.setText(equipment.getName());

        // Update button appearance based on selection state
        if (equipment.isSelected()) {
            // Selected state: Make the button appear "pressed"
            holder.nameEquipment.setBackgroundColor(Color.LTGRAY); // Change background color
            holder.nameEquipment.setTextColor(Color.WHITE);
        } else {
            // Unselected state: Make the button appear "unpressed"
            holder.nameEquipment.setBackgroundColor(Color.TRANSPARENT); // Reset background color
            holder.nameEquipment.setTextColor(Color.BLACK);
        }

        // Set click listener to toggle selection state
        holder.nameEquipment.setOnClickListener(view -> {
            // Toggle selection state
            equipment.setSelected(!equipment.isSelected());
            notifyItemChanged(position); // Refresh the item view
        });
    }

    @Override
    public int getItemCount() {
        return equipmentEquipments.size();
    }
}