package com.example.proyecto.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto.Interfaces.ItemClickListener;
import com.example.proyecto.R;

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNumber, txtName;

    public ItemClickListener listener;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNumber=(TextView) itemView.findViewById(R.id.group_number_item);
        txtName=(TextView) itemView.findViewById(R.id.group_name_item);

    }

    public void setItemClickListener(ItemClickListener listener){this.listener = listener;}


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
