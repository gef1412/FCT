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
                //holder.imgMeeting.setVisibility(View.VISIBLE);


                reunionesRef.child(model.getGrupo()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reunionesRef.child(model.getGrupo()).child(dataSnapshot.getKey());
                        if(dataSnapshot.child("estado").exists()){
                            holder.check.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CharSequence opciones[]= new CharSequence[]{
                                "Confirmar reunión",
                                "Eliminar"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaReunionesActivity.this);
                        builder.setTitle("Opciones:");
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //CON MODIFICAR SE CARGA EL ACTIVITY DEL PRODUCTO DONDE SE MODIFICA
                                //LA CANTIDAD DEL PRODUCTO
                                if(i==0){


                                    Map<String, Object> mapMeeting = new HashMap<>();
                                    mapMeeting.put("estado", "confirmado");



                                    reunionesRef.child(model.getGrupo()).updateChildren(mapMeeting)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ConsultaReunionesActivity.this, "Reunión con "+ model.getGrupo() +" confirmada", Toast.LENGTH_SHORT).show();
                                                    holder.check.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }
                                //CON ELIMINAR, SE ACCEDE A LA UBICACIÓN DE LA LISTA DE LA COMPRA,
                                //Y AHÍ SE ELIMINA EL ARTÍCULO, ACTUALIZANDO EL FRAGMENT ACTUAL PARA
                                //ACTUALIZAR LA LISTA Y EL PRECIO
                                if(i==1){

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ConsultaReunionesActivity.this);
                                    builder1.setTitle("Alertas");
                                    builder1.setMessage("¿Seguro que quieres eliminar la reunión?");
                                    //LayoutInflater inflater = getActivity().getLayoutInflater();

                                    // Botones de aceptar/cancelar
                                    builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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

                                    builder1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder1.show();
                                }
                            }
                        });
                        builder.show();

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
