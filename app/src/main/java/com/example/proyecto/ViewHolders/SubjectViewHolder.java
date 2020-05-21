package com.example.proyecto.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.Interfaces.ItemClickListener;
import com.example.proyecto.R;

public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtCourse;
    public ImageView imgSubject;
    public ItemClickListener listener;

    public SubjectViewHolder(View itemView) {
        super(itemView);
        imgSubject=(ImageView) itemView.findViewById(R.id.subject_img_item);
        txtName=(TextView) itemView.findViewById(R.id.subject_name_item);
        txtCourse=(TextView) itemView.findViewById(R.id.subject_course_item);
    }


    public void setItemClickListener(ItemClickListener listener){this.listener = listener;}

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);

    }



}
