package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReunionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName;
    private CircleImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;



    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reuniones);

        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Reuniones");
        setSupportActionBar(toolbar);

        drawerLayout= findViewById(R.id.drawer_reuniones);
        navigationView=findViewById(R.id.nav_view_reuniones);
        navigationView.setNavigationItemSelectedListener(this);

        //Funcionamiento del icono hamburguesa
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.logged_user);
        profileImage=(CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        getUsuarioInfo(user);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);
        //pager.setVisibility(View.GONE);

        switch (item.getItemId()) {

            case R.id.alumnos:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber",0));

                break;

            case R.id.grupos:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber",1));

                break;

            case R.id.asignaturas:

                startActivity(new Intent(this, UserActivity.class).putExtra("fragNumber",2));

                break;

            case R.id.reuniones:

                startActivity(new Intent(this, ReunionesActivity.class));
                break;

            case R.id.ajustes:
                startActivity(new Intent(this, PerfilActivity.class));
                break;

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(this,ActivityMain.class));
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



    private void getUsuarioInfo(final FirebaseUser user) {

        //Ruta donde buscaremos la información asociada al usuario
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    //Accedemos a la base de datos en la ruta indicada
                    RootRef.child("Usuarios").child(snapShot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Para extraer los datos de la BBDD con ayuda de la clase Usuarios
                            Usuarios datosUsuario = snapShot.getValue(Usuarios.class);
                            //Se obtiene la ID del usuario actual
                            String id = user.getUid();
                            //Se obtienen los string que representan las IDs en la BBDD
                            String idBBDD = datosUsuario.getID();
                            //Si el ID del usuario actual se corresponde con alguna de las guardadas,
                            //se obtienen los datos
                            if (idBBDD.equals(id)) {

                                String fotoBBDD = null;
                                //Se obtiene el url de ubicación de la foto en caso de estar guardado
                                if(snapShot.child("foto").exists()){
                                    fotoBBDD=datosUsuario.getFoto();
                                }
                                //Se obtienen nombre y apellidos
                                String nombreBBDD = datosUsuario.getNombre();
                                String apellidosBBDD = datosUsuario.getApellido();

                                //Se introducen los datos obtenidos en los elementos de la vista
                                if(fotoBBDD!=null){
                                    Picasso.get().load(fotoBBDD).into(profileImage);
                                }
                                userName.setText(nombreBBDD+" "+apellidosBBDD);

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

}
