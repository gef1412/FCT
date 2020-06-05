package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto.Models.Grupos;
import com.example.proyecto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CrearGruposActivity extends AppCompatActivity {

    private EditText inputGroupName, inputGroupNumber;

    private String ID;
    String IDexistente="";

    static int numnberRepetido=0;
    static String currentNumber="";

    private DatabaseReference GroupsRef;

    private ProgressDialog barraCarga;

    private Button createGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupos);

        try{
            IDexistente=getIntent().getStringExtra("IDgroup");
            getGroupInfo(IDexistente);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        currentNumber=getIntent().getStringExtra("number");

        GroupsRef= FirebaseDatabase.getInstance().getReference().child("Grupos");
        inputGroupNumber=(EditText)findViewById(R.id.add_group_number);
        inputGroupName=(EditText)findViewById(R.id.add_group_name);
        createGroup=(Button) findViewById(R.id.add_group_btn);

        barraCarga=new ProgressDialog(this);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNumber= inputGroupNumber.getText().toString().trim();
                String groupName= inputGroupName.getText().toString().trim();


                if(TextUtils.isEmpty(groupNumber)||groupNumber.length()>3){
                    Toast.makeText(CrearGruposActivity.this,"Introduzca un número valido",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(CrearGruposActivity.this,"Se requiere un nombre para el curso",Toast.LENGTH_SHORT).show();
                }else{
                    addGroupIntoDB(groupNumber,groupName);
                }
            }
        });






    }

    private void getGroupInfo(final String IDexistente) {

        GroupsRef= FirebaseDatabase.getInstance().getReference().child("Grupos");

        GroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    GroupsRef.child(snapShot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Grupos datosGrupo = snapShot.getValue(Grupos.class);
                            String id = IDexistente;
                            String idBBDD = datosGrupo.getID();
                            if (idBBDD.equals(id)) {




                                String numberBBDD = datosGrupo.getNumero();
                                String nameBBDD = datosGrupo.getNombre();

                                inputGroupNumber.setText(numberBBDD);
                                inputGroupName.setText(nameBBDD);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addGroupIntoDB(String groupNumber, String groupName) {
        barraCarga.setTitle("Añadiendo/Modificando asignatura");
        barraCarga.setMessage("Espere un momento por favor");
        barraCarga.setCanceledOnTouchOutside(false);
        barraCarga.show();

        saveInfoGroupinBBDD(groupNumber,groupName);


    }

    private void saveInfoGroupinBBDD(final String groupNumber, final String groupName) {

        GroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //SE COMPRUEBA EL NICK DE USUARIOS GUARDADOS EN BBDD PARA COMPROBAR SI EXISTE
                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {

                    Grupos datosGrupo = snapShot.getValue(Grupos.class);
                    String numberBBDD = datosGrupo.getNumero();

                    if(groupNumber.equals(numberBBDD)){

                        numnberRepetido++;
                        barraCarga.dismiss();

                    }

                }

                if(numnberRepetido==0||groupNumber.equals(currentNumber)){

                    if(IDexistente!=null){
                        ID=IDexistente;
                    }else{
                        ID = GroupsRef.push().getKey();
                    }


                    HashMap<String,Object> subjectMap= new HashMap<>();
                    subjectMap.put("ID",ID);
                    subjectMap.put("numero",groupNumber);
                    subjectMap.put("nombre",groupName);


                    GroupsRef.child(ID).updateChildren(subjectMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        barraCarga.dismiss();
                                        Toast.makeText(CrearGruposActivity.this,"Grupos actualizadas en la base de datos",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(CrearGruposActivity.this,UserActivity.class).putExtra("fragNumber",1);
                                        startActivity(intent);
                                    }else{
                                        barraCarga.dismiss();
                                        String mensaje= task.getException().toString();
                                        Toast.makeText(CrearGruposActivity.this,"Error: "+ mensaje,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(CrearGruposActivity.this,"El número ya existe",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        numnberRepetido=0;

    }



}
