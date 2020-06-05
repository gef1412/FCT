package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReunionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName;
    private CircleImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String fechaActual, horaActual;

    static boolean reunionAsig = false;

    private String grupoUsuario = "";
    private String tipoUsuario = "";
    private String nombreAsignatura = "";
    static String tipoBBDD="";


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    static List<String> subjectUser = new ArrayList<>();
    static List<Asignaturas> listaAsig = new ArrayList<>();
    //static boolean visible=true;

    DatabaseReference reunionesRef;


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reuniones);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

        getUsuarioInfo(user);

        Intent intent = getIntent();
        final int tipo = intent.getIntExtra("tipo", 1);
        if (tipo == 0) {
            hide_item();
        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference AsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(user.getUid()).child("asignaturas");
        //final DatabaseReference userAsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");


        final FirebaseRecyclerOptions<Asignaturas> opciones = new FirebaseRecyclerOptions.Builder<Asignaturas>()
                .setQuery(AsignaturasRef, Asignaturas.class)
                .build();


        final FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder> adapterSubject = new FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder>(opciones) {
            boolean pulsado = false;
            boolean grupoExistente = false;

            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final Asignaturas model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA


                holder.txtName.setText(model.getNombre());
                holder.txtCourse.setText(model.getCurso());
                holder.checkBoxSubject.setVisibility(View.GONE);
                //holder.imgSubject.setVisibility(View.VISIBLE);

                if (model.getFoto() == null) {
                    Picasso.get().load(R.drawable.msn_logo).resize(80, 80).into(holder.imgSubject);
                } else {
                    Picasso.get().load(model.getFoto()).resize(80, 80).into(holder.imgSubject);
                }




                /*if (reunionAsig){
                    holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));
                    pulsado = false;

                }else{
                    pulsado = true;
                }*/

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference myReunionesRef = FirebaseDatabase.getInstance().getReference("Reuniones");
                        myReunionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if ((dataSnapshot.exists()) && (!pulsado)){
                                   /*if (Objects.equals(dataSnapshot.getKey(), grupoUsuario)){
                                       AlertDialog.Builder myBuilder = new AlertDialog.Builder(ReunionesActivity.this);
                                       myBuilder.setTitle("Reunion Existente");
                                       myBuilder.setMessage("Esta reunion ya ha sido solicitada por otro miembro del grupo, desea cancelarla?");
                                       myBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               borrarReunion(model.getNombre());
                                               pulsado = false;
                                           }
                                       });
                                       myBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.cancel();
                                           }
                                       });
                                   }*/
                                    nombreAsignatura = model.getNombre();

                                    if(tipo==0){
                                        holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));
                                        guardarReuniones(nombreAsignatura);
                                    }else{
                                        Toast.makeText(ReunionesActivity.this, "Los profesores no pueden solicitar reuniones", Toast.LENGTH_SHORT).show();
                                    }


                                    pulsado = true;


                                }else if ((dataSnapshot.exists()) && (pulsado)) {
                                    holder.rowContainer.setBackgroundColor(Color.parseColor("#55AEBB"));

                                        borrarReunion(model.getNombre());


                                    pulsado = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        /* if (pulsado) {
                            nombreAsignatura = model.getNombre();
                            holder.rowContainer.setBackgroundColor(Color.parseColor("#00FF00"));
                            guardarReuniones(nombreAsignatura);
                            pulsado = false;
                        } else {
                            holder.rowContainer.setBackgroundColor(Color.parseColor("#55AEBB"));
                            pulsado = true;
                        }*/
                    }

                });

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
        item.setVisible(false);
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

                startActivity(new Intent(this, ReunionesActivity.class));
                break;

            case R.id.ajustes:
                startActivity(new Intent(this, CrearUsuariosActivity.class)
                        .putExtra("modify", "true").putExtra("tipo", tipoBBDD));
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

                    tipoUsuario = datosUsuario.getType();
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

                        tipoBBDD = datosUsuario.getType();


                                /*if(snapShot.child("asignaturas").exists()){
                                    subjectUser=datosUsuario.getAsignaturas();
                                }*/


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

    public void reunionPedida(String subjectName) {
        reunionesRef = FirebaseDatabase.getInstance().getReference("Reuniones").child(subjectName);
        reunionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    for (final DataSnapshot snapShotSubject : snapShot.getChildren()) {
                        for (final DataSnapshot snapShotReunion : snapShotSubject.getChildren()) {
                            Reuniones datosReuniones = snapShotReunion.getValue(Reuniones.class);
                            String grupoBBDD = datosReuniones.getGrupo();
                            if (grupoUsuario.equalsIgnoreCase(grupoBBDD)) {
                                Toast.makeText(ReunionesActivity.this, "Ya hay reunión solicitada", Toast.LENGTH_SHORT).show();
                                reunionAsig = true;
                                break;
                            }
                        }
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        if (reunionAsignada[0]){
//            return reunionPedida();
//        }else{
//            return false;
//        }
    }


}