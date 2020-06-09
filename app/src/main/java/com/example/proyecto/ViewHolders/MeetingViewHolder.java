package com.example.proyecto.ViewHolders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.Interfaces.ItemClickListener;
import com.example.proyecto.R;

public class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtDate;
    public TextView group;
    public LinearLayout rowContainer;
    public ItemClickListener listener;
    public ImageView check;

    public MeetingViewHolder(@NonNull View itemView) {
        super(itemView);
        group=(TextView) itemView.findViewById(R.id.meeting_name_item);
        txtName=(TextView) itemView.findViewById(R.id.meeting_subj_item);
        txtDate=(TextView) itemView.findViewById(R.id.meeting_course_item);
        rowContainer=(LinearLayout) itemView.findViewById(R.id.row_meet_container);
        check=(ImageView) itemView.findViewById(R.id.confirmado);

    }


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
