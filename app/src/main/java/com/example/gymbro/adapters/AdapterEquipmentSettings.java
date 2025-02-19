package com.example.gymbro.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymbro.R;
import com.example.gymbro.models.Equipment;

import java.util.ArrayList;

public class AdapterEquipmentSettings extends RecyclerView.Adapter<AdapterEquipmentSettings.MyViewHolder> {

    private ArrayList<Equipment> equipmentEquipments;

    public AdapterEquipmentSettings(ArrayList<Equipment> equipmentEquipments) {
        this.equipmentEquipments = equipmentEquipments;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameEquipment;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEquipment = itemView.findViewById(R.id.text_name_equipment);
            // imageEquipment = itemView.findViewById(R.id.imageView2);
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

        Equipment equipment = equipmentEquipments.get(position);
        holder.nameEquipment.setText(equipmentEquipments.get(position).getName());
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);


        holder.itemView.setOnClickListener(view -> {
            view.setSelected(!view.isSelected());
            equipment.setSelected(!equipment.isSelected());
            notifyItemChanged(position);

            // You can also pass other data if needed
            // bundle.putInt("imageResId", selectedItem.getImageResId());
            // Example: Navigation.findNavController(view).navigate(R.id.your_destination, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return equipmentEquipments.size();
    }
}