package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto.Models.Reuniones;
import com.example.proyecto.Models.Reuniones;
import com.example.proyecto.R;
import com.example.proyecto.ViewHolders.MeetingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ConsultaReunionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference reunionesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_reuniones);




        recyclerView = (RecyclerView) findViewById(R.id.recycler_consulta_reuniones);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        final String subject = intent.getStringExtra("asignatura");

        reunionesRef = FirebaseDatabase.getInstance().getReference().child("Reuniones").child(subject);


        final FirebaseRecyclerOptions<Reuniones> opciones = new FirebaseRecyclerOptions.Builder<Reuniones>()
                .setQuery(reunionesRef.orderByChild("ID"), Reuniones.class)
                .build();

        final FirebaseRecyclerAdapter<Reuniones, MeetingViewHolder> adapterMeeting = new FirebaseRecyclerAdapter<Reuniones, MeetingViewHolder>(opciones) {

            @Override
            protected void onBindViewHolder(@NonNull final MeetingViewHolder holder, final int position, @NonNull final Reuniones model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA


                holder.txtName.setText(model.getAsignaturas());
                holder.txtDate.setText("Fecha: "+model.getFecha()+" / " + model.getHora());
                holder.group.setText(model.getGrupo());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reunionesRef
                                .child(model.getGrupo())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ConsultaReunionesActivity.this,"Reunión eliminada de la lista",Toast.LENGTH_SHORT).show();
                                            notifyItemRemoved(position);
                                        }
                                    }
                                });

                    }
                });

            }

            //ESTE MÉTODO INDICA LA VISTA UTILIZADA PARA MOSTRAR EL PRODUCTO EN LA LISTA
            @NonNull
            @Override
            public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meetings_item_layout, parent, false);
                MeetingViewHolder holder = new MeetingViewHolder(view);

                return holder;
            }
        };

        recyclerView.setAdapter(adapterMeeting);
        adapterMeeting.startListening();

    }
}
