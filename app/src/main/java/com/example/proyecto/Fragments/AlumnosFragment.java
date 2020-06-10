package com.example.proyecto.Fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto.Activities.ActivityMain;
import com.example.proyecto.Activities.CrearUsuariosActivity;
import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.example.proyecto.ViewHolders.UsersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlumnosFragment extends Fragment {

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    RecyclerView recyclerView;

    static String usuarios="";

    static String emailBBDD="";
    static String passwordBBDD="";

    static String tipoBBDD="";

    static String ID="";

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private ProgressDialog barraCarga;

    public AlumnosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    //Log.i("onQueryTextChange", newText);

                    usuarios=searchView.getQuery().toString();
                    onStart();
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Log.i("onQueryTextSubmit", query);
                    usuarios=searchView.getQuery().toString();
                    onStart();
                    return false;
                }

            };

            searchView.setOnQueryTextListener(queryTextListener);


        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();




       // String email= user.getEmail();

        final String emailOriginal=emailBBDD;
        final String passwordOriginal=passwordBBDD;



        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");


        FirebaseRecyclerOptions<Usuarios> opciones = new FirebaseRecyclerOptions.Builder<Usuarios>()
                .setQuery(usersRef.orderByChild("nombre").startAt(usuarios).endAt(usuarios+"\uf8ff"),Usuarios.class)
                .build();


        FirebaseRecyclerAdapter<Usuarios, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Usuarios, UsersViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position, @NonNull final Usuarios model) {


                //INFLAMOS LOS ELEMENTOS DE LA LISTA
                holder.txtName.setText(model.getNombre());
                holder.txtType.setText(model.getType());
                holder.checkBoxUser.setVisibility(View.GONE);

                if(model.getFoto()==null){
                    Picasso.get().load(R.drawable.msn_logo).resize(80,80).into(holder.imgUser);
                }else{
                    Picasso.get().load(model.getFoto()).resize(80,80).into(holder.imgUser);
                }


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final CharSequence opciones[]= new CharSequence[]{
                                    "Modificar",
                                    "Eliminar"
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Opciones:");
                            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    //CON MODIFICAR SE CARGA EL ACTIVITY DEL PRODUCTO DONDE SE MODIFICA
                                    //LA CANTIDAD DEL PRODUCTO
                                    if(i==0){
                                        usuarios="";


                                        Intent intent= new Intent(getContext(), CrearUsuariosActivity.class);

                                        String modificado= "true";
                                        Bundle info = new Bundle();
                                        info.putString("ID",model.getID());
                                        info.putString("tipo", model.getType());
                                        info.putString("email",emailBBDD);
                                        info.putString("password",passwordBBDD);
                                        info.putString("tipoActual", tipoBBDD);
                                        info.putString("modify",modificado);

                                        intent.putExtras(info);
                                        startActivity(intent);

                                    }
                                    //CON ELIMINAR, SE ACCEDE A LA UBICACIÓN DE LA LISTA DE LA COMPRA,
                                    //Y AHÍ SE ELIMINA EL ARTÍCULO, ACTUALIZANDO EL FRAGMENT ACTUAL PARA
                                    //ACTUALIZAR LA LISTA Y EL PRECIO
                                    if(i==1){

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                        builder1.setTitle("Alertas");
                                        builder1.setMessage("¿Seguro que quieres eliminar al alumno de la lista?");
                                        LayoutInflater inflater = getActivity().getLayoutInflater();

                                        // Botones de aceptar/cancelar
                                        builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                loginOtherUser(model.getEmail(),model.getPassword());

                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(model.getEmail(), model.getPassword());
                                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    usersRef.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            mAuth.signOut();
                                                                            Intent intent= new Intent(getContext(), ActivityMain.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    });

                                                                }else{
                                                                    //Toast.makeText(BorrarUsuarioActivity.this,"No se pudo eliminar",Toast.LENGTH_SHORT).show();

                                                                    Intent intent= new Intent(getContext(), ActivityMain.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
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
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alumnos_item_layout, parent, false);
                UsersViewHolder holder= new UsersViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        getUsuarioInfo(user);

        View view=inflater.inflate(R.layout.fragment_alumnos, container, false);

        RecyclerView.LayoutManager layoutManager;
        recyclerView = view.findViewById(R.id.recycler_alumnos);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        barraCarga=new ProgressDialog(getContext());




        FloatingActionButton addAlumno_btn= view.findViewById(R.id.add_alumno_btn);



        addAlumno_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarios="";

                    Intent intent= new Intent(getContext(), CrearUsuariosActivity.class)
                            .putExtra("email",emailBBDD)
                            .putExtra("password",passwordBBDD)
                            .putExtra("modify","false");
                    startActivity(intent);


            }
        });

        return view;

    }

    private void loginOtherUser(String email, String password){
        barraCarga.setTitle("Cargando perfil");
        barraCarga.setMessage("Espere por favor");
        barraCarga.setCanceledOnTouchOutside(false);
        barraCarga.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                            // Sign in success, update UI with the signed-in user's information
                            barraCarga.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            barraCarga.dismiss();
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


                                //Se obtiene el url de ubicación de la foto en caso de estar guardado

                                //Se obtienen nombre y apellidos

                                emailBBDD = datosUsuario.getEmail();
                                passwordBBDD = datosUsuario.getPassword();
                                tipoBBDD=datosUsuario.getType();
                                ID=datosUsuario.getID();




                                //Se introducen los datos obtenidos en los elementos de la vista


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
