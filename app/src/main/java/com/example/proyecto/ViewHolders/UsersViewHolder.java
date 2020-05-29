package com.example.proyecto.ViewHolders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.Interfaces.ItemClickListener;
import com.example.proyecto.R;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtType;
    public CheckBox checkBoxUser;
    public ImageView imgUser;
    public ItemClickListener listener;
    //public ListView asignaturas_list;

    public UsersViewHolder(View itemView) {
        super(itemView);
        imgUser = (ImageView) itemView.findViewById(R.id.alumno_img_item);
        txtName = (TextView) itemView.findViewById(R.id.alumno_name_item);
        txtType = (TextView) itemView.findViewById(R.id.alumno_type_item);
        checkBoxUser = (CheckBox) itemView.findViewById(R.id.alumno_checkbox_item);
        //asignaturas_list = (ListView) itemView.findViewById(R.id.asignaturas_list);

    }


    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);

    }

    {
    }
}
