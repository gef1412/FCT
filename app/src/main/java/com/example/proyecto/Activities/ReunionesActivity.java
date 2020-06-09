package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.Models.Asignaturas;
import com.example.proyecto.Models.Reuniones;
import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.example.proyecto.ViewHolders.SubjectViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReunionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName;
    private CircleImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String fechaActual, horaActual;



    private static String grupoUsuario = "";
    static String emailBBDD="";
    static String passwordBBDD="";
    static String tipoBBDD="";



    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;



    DatabaseReference reunionesRef;


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reuniones);



        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reuniones");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_reuniones);
        navigationView = findViewById(R.id.nav_view_reuniones);
        navigationView.setNavigationItemSelectedListener(this);

        //Funcionamiento del icono hamburguesa
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        this.invalidateOptionsMenu();


        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.logged_user);
        profileImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_subjects_reuniones);





        Intent intent = getIntent();
        final int tipo = intent.getIntExtra("tipo", 1);
        if (tipo == 0) {
            hide_item();

        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        getUsuarioInfo(user);

        DatabaseReference AsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(user.getUid()).child("asignaturas");
        //final DatabaseReference userAsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");


        final FirebaseRecyclerOptions<Asignaturas> opciones = new FirebaseRecyclerOptions.Builder<Asignaturas>()
                .setQuery(AsignaturasRef, Asignaturas.class)
                .build();


        final FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder> adapterSubject = new FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder>(opciones) {



            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final Asignaturas model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA




                /*if(encontrado.equals("true")){
                    //holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));


                }*/

                final boolean[] pulsado = {true};


                holder.txtName.setText(model.getNombre());
                holder.txtCourse.setText(model.getCurso());
                holder.checkBoxSubject.setVisibility(View.GONE);
                //holder.imgSubject.setVisibility(View.VISIBLE);

                if (model.getFoto() == null) {
                    Picasso.get().load(R.drawable.msn_logo).resize(80, 80).into(holder.imgSubject);
                } else {
                    Picasso.get().load(model.getFoto()).resize(80, 80).into(holder.imgSubject);
                }


                if(tipoBBDD.equals("Alumno")){
                    pulsado[0]= findReuniones(model.getNombre(),holder,position);
                }else{

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(ReunionesActivity.this,ConsultaReunionesActivity.class)
                                    .putExtra("asignatura",model.getNombre()));

                        }
                    });

                }





            }

            //ESTE MÉTODO INDICA LA VISTA UTILIZADA PARA MOSTRAR EL PRODUCTO EN LA LISTA
            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_item_layout, parent, false);
                SubjectViewHolder holder = new SubjectViewHolder(view);

                return holder;
            }
        };

        recyclerView.setAdapter(adapterSubject);
        adapterSubject.startListening();
    }

    private boolean findReuniones(final String nombre, final SubjectViewHolder holder, final int position) {

        final boolean [] pulsado={true};

        reunionesRef = FirebaseDatabase.getInstance().getReference()
                .child("Reuniones");

        reunionesRef.child(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {

                    Reuniones datosReuniones = snapShot.getValue(Reuniones.class);

                    String grupoReunion=datosReuniones.getGrupo();

                    if(grupoReunion.equals(grupoUsuario)){
                        Toast.makeText(ReunionesActivity.this, "La reunion ya existe ", Toast.LENGTH_SHORT).show();
                        //encontrado="true";
                        holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));
                        pulsado[0]=false;
                        if (snapShot.child("estado").exists()){
                            holder.check.setVisibility(View.VISIBLE);
                        }

                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(pulsado[0]){
                    //nombreAsignatura=model.getNombre();
                    holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));
                    guardarReuniones(nombre);
                    pulsado[0] =false;
                }else{
                    holder.rowContainer.setBackgroundColor(Color.parseColor("#55AEBB"));
                    borrarReunion(nombre);
                    pulsado[0] =true;
                }
            }

        });

        return pulsado[0];

    }

    private void hide_item() {
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.alumnos).setVisible(false);
        nav_menu.findItem(R.id.asignaturas).setVisible(false);
        nav_menu.findItem(R.id.grupos).setVisible(false);
        nav_menu.findItem(R.id.gestionar).setVisible(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);
        //item.setVisible(false);
        //pager.setVisibility(View.GONE);

        switch (item.getItemId()) {

            case R.id.alumnos:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber", 0));

                break;

            case R.id.grupos:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber", 1));

                break;

            case R.id.asignaturas:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber", 2));

                break;

            case R.id.reuniones:

                int tipo;
                if(tipoBBDD.equals("Alumno")){
                    tipo=0;
                }else{
                    tipo=1;
                }

                startActivity(new Intent(this, ReunionesActivity.class).putExtra("tipo",tipo));
                break;

            case R.id.ajustes:


                Intent intent= new Intent(this, CrearUsuariosActivity.class);

                String modificado= "true";
                Bundle info = new Bundle();
                info.putString("ID",user.getUid());
                info.putString("tipo", tipoBBDD);
                info.putString("email",emailBBDD);
                info.putString("password",passwordBBDD);
                info.putString("tipoActual", tipoBBDD);
                info.putString("modify",modificado);

                intent.putExtras(info);
                startActivity(intent);
                break;

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(this, ActivityMain.class));
                finish();
                break;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_reuniones);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void guardarReuniones(final String subjectName) {

        reunionesRef = FirebaseDatabase.getInstance().getReference()
                .child("Reuniones");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM,yyyy");
        fechaActual = currentDate.format(calendar.getTime());

        //OBTENEMOS LA HORA ACTUAL
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        horaActual = currentTime.format(calendar.getTime());

        //CREAMOS UNA CLAVE CON LA FECHA Y HORA OBTENIDA QUE SERVIRÁ PARA CREAR LA ID DEL PRODUCTO
        String idReunion = fechaActual + " & " + horaActual;

        reunionesRef = FirebaseDatabase.getInstance().getReference()
                .child("Reuniones");//.child(subjectName).child(grupoUsuario);


        HashMap<String, Object> reunionMap = new HashMap<>();

        reunionMap.put("ID", idReunion);
        reunionMap.put("asignaturas", subjectName);
        reunionMap.put("fecha", fechaActual);
        reunionMap.put("hora", horaActual);
        reunionMap.put("grupo", grupoUsuario);


        reunionesRef.child(subjectName).child(grupoUsuario).updateChildren(reunionMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ReunionesActivity.this, "Reunion solicitada para el grupo: " + grupoUsuario, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReunionesActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void getUsuarioInfo(final FirebaseUser user) {

        //Ruta donde buscaremos la información asociada al usuario
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    //Accedemos a la base de datos en la ruta indicada

                    //Para extraer los datos de la BBDD con ayuda de la clase Usuarios
                    Usuarios datosUsuario = snapShot.getValue(Usuarios.class);

                    //tipoUsuario = datosUsuario.getType();
                    //Se obtiene la ID del usuario actual
                    String id = user.getUid();
                    //Se obtienen los string que representan las IDs en la BBDD
                    String idBBDD = datosUsuario.getID();
                    //Si el ID del usuario actual se corresponde con alguna de las guardadas,
                    //se obtienen los datos
                    if (idBBDD.equals(id)) {

                        String fotoBBDD = null;
                        //Se obtiene el url de ubicación de la foto en caso de estar guardado
                        if (snapShot.child("foto").exists()) {
                            fotoBBDD = datosUsuario.getFoto();
                        }


                        emailBBDD=datosUsuario.getEmail();
                        passwordBBDD=datosUsuario.getPassword();

                        tipoBBDD = datosUsuario.getType();

                        //Se obtienen nombre y apellidos
                        String nombreBBDD = datosUsuario.getNombre();
                        String apellidosBBDD = datosUsuario.getApellido();

                        //Se introducen los datos obtenidos en los elementos de la vista
                        if (fotoBBDD != null) {
                            Picasso.get().load(fotoBBDD).into(profileImage);
                        }
                        userName.setText(nombreBBDD + " " + apellidosBBDD);

                        if (snapShot.child("grupo").exists()) {
                            grupoUsuario = datosUsuario.getGrupo();
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void borrarReunion(String nombreAsignatura) {
        reunionesRef= FirebaseDatabase.getInstance().getReference()
                .child("Reuniones");

        reunionesRef.child(nombreAsignatura).child(grupoUsuario).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ReunionesActivity.this,"Reunión cancelada",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

}
