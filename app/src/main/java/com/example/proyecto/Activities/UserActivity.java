package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;


import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.Adapters.PagerAdapter;

import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener { //Este activity muestra la pantalla de usuario


    private TextView userName;
    private CircleImageView profileImage;


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    static String emailRecibido="";
    static String passwordRecibido="";
    static String emailBBDD="";
    static String passwordBBDD="";
    static String tipoBBDD="";

    ViewPager pager;
    TabLayout mTabLayout;
    TabItem tabAlumnos,tabGrupos,tabAsignaturas;
    PagerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        //Obtenemos el usuario cuya sesión está abierta
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();



        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Usuarios");
        setSupportActionBar(toolbar);


        pager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tablayout);

        tabAlumnos = findViewById(R.id.alumnos);
        tabGrupos = findViewById(R.id.grupos);
        tabAsignaturas = findViewById(R.id.asignaturas);



        drawerLayout= findViewById(R.id.drawer);
        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Funcionamiento del icono hamburguesa
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        String fragmentID="";
        int idFragment = 0;
        try{
            fragmentID= getIntent().getExtras().get("fragNumber").toString();
            idFragment= Integer.parseInt(fragmentID);
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        adapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,mTabLayout.getTabCount());
        pager.setAdapter(adapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                getFragmentTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        //En caso de iniciar la aplicación desde el inicio, se ejecuta el caso 0
        if(savedInstanceState==null){

                switch (idFragment){

                    case 0:
                        pager.setCurrentItem(0);
                        break;

                    case 1:
                        pager.setCurrentItem(1);
                        break;

                    case 2:
                        pager.setCurrentItem(2);
                        break;
                }

                getFragmentTitle(idFragment);

        }


        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.logged_user);
        profileImage=(CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);


        //Obtenemos la info del usuario
        getUsuarioInfo(user);


    }

    private void getFragmentTitle(int title) {

        switch (title){

            case 0:
                toolbar.setTitle("Usuarios");
                break;

            case 1:
                toolbar.setTitle("Grupos");
                break;

            case 2:
                toolbar.setTitle("Asignaturas");
                break;
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

                            if(user.getUid()==null){
                                startActivity(new Intent(UserActivity.this,ActivityMain.class));
                            }


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

                                emailBBDD=datosUsuario.getEmail();
                                passwordBBDD=datosUsuario.getPassword();
                                tipoBBDD= datosUsuario.getType();


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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        //pager.setVisibility(View.GONE);

        switch (item.getItemId()) {

            case R.id.alumnos:
                pager.setCurrentItem(0);

                break;

            case R.id.grupos:

                pager.setCurrentItem(1);

                break;

            case R.id.asignaturas:

                pager.setCurrentItem(2);

                break;

            case R.id.reuniones:

                startActivity(new Intent(this, ReunionesActivity.class));
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
                startActivity(new Intent(this,ActivityMain.class));
                finish();
                break;
        }


        return true;
    }

}
