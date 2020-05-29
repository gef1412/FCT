package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnClickListener, View.OnClickListener {

    private TextView userName;
    private CircleImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    NavigationView navigationView;

    static String userType="";

    private CircleImageView imageProfile;
    private EditText inputName, inputLastname, inputAge, inputEmail, inputPassword;
    private Button signUp_btn;
    private ProgressDialog barraCarga;
    private TextView addImageprofile;
    Uri imageUri;
    String myUrl="";
    StorageTask uploadTask;

    //Para guardar info en el storage de Firebase
    StorageReference storageProfilePictureRef;

    //para crear la cuenta de usuario de Firebase


    private AlertDialog.Builder builder;
    private EditText editTextPassword;


    static int emailRep=0;
    static String ID;

    static String originalPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        drawerLayout= findViewById(R.id.drawer_perfil);
        navigationView=findViewById(R.id.nav_view_perfil);
        navigationView.setNavigationItemSelectedListener(this);

        //Funcionamiento del icono hamburguesa
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.logged_user);
        profileImage=(CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        ID= user.getUid();
        inputName= (EditText) findViewById(R.id.modify_nombre);
        inputLastname= (EditText) findViewById(R.id.modify_apellido);
        inputAge= (EditText) findViewById(R.id.modify_edad);
        inputEmail= (EditText) findViewById(R.id.modify_email);
        inputPassword= (EditText) findViewById(R.id.modify_input_password);
        signUp_btn= (Button) findViewById(R.id.modify_btn);
        barraCarga=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Fotos de perfil");

        imageProfile = (CircleImageView) findViewById(R.id.modify_profile_image);
        addImageprofile=(TextView) findViewById(R.id.modify_image_profile_btn);

        signUp_btn.setOnClickListener(this);


        addImageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri).setAspectRatio(1,1)
                        .start(PerfilActivity.this);
            }
        });
        getUsuarioInfo(user);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode == Activity.RESULT_OK){
                imageUri=result.getUri();
                imageProfile.setImageURI(imageUri);
            }
        }
        else{
            Toast.makeText(this,"Error, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View view) {

        //VENTANA EMERGENTE
        builder = new AlertDialog.Builder(PerfilActivity.this);
        builder.setTitle("Confirmar cambios");
        builder.setMessage("Introduce la contraseña ACTUAL");
        //EL TEXTO ESCRITO APARECERÁ EN EL CENTRO DE LA PANTALLA
        editTextPassword = new EditText(PerfilActivity.this);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(editTextPassword);

        // BOTONES DE ACEPTAR/CANCELAR
        builder.setPositiveButton("Aceptar", PerfilActivity.this);
        builder.setNegativeButton("Cancelar", PerfilActivity.this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            originalPassword = editTextPassword.getText().toString();
            if (!originalPassword.isEmpty()) {
                userInfoSaved();
            }else{
                Toast.makeText(PerfilActivity.this,"Contraseña ACTUAL requerida para guardar cambios",Toast.LENGTH_SHORT).show();
            }
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialogInterface.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_perfil);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void userInfoSaved() {


        String nombre=inputName.getText().toString().trim();
        String apellido=inputLastname.getText().toString().trim();
        String edad=inputAge.getText().toString().trim();
        String email=inputEmail.getText().toString().trim();
        String password=inputPassword.getText().toString().trim();


        if(TextUtils.isEmpty(nombre)||TextUtils.isEmpty(apellido)||TextUtils.isEmpty(edad)||
                TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(PerfilActivity.this,"Error, rellene los campos vacíos", Toast.LENGTH_SHORT).show();
        } else{
            uploadImage(nombre,apellido,edad,email,password);
        }
    }


    private void uploadImage(final String nombre, final String apellido,final String edad, final String email, final String password){
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Crear cuenta");
        progressDialog.setMessage("Comprobando que todo está en orden, espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.child("Usuarios").child(userType).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {

                    Usuarios datosUsuario = snapShot.getValue(Usuarios.class);

                    String emailBBDD = datosUsuario.getEmail();
                    String idBBDD = datosUsuario.getID();

                    if(!ID.equals(idBBDD)){

                        if (email.equals(emailBBDD)) {
                            emailRep++;
                        }
                    }
                }


                boolean emailValido;
                boolean passValido;



                if(emailRep==0){
                    emailValido=true;
                }else{
                    emailValido=false;
                    progressDialog.dismiss();
                    Toast.makeText(PerfilActivity.this,"email no disponible", Toast.LENGTH_SHORT).show();

                }
                if(password.length()<6){
                    passValido=false;
                    progressDialog.dismiss();
                    Toast.makeText(PerfilActivity.this,"Debe introducir un password de al menos 6 caracteres",Toast.LENGTH_SHORT).show();
                }else{
                    passValido=true;
                }


                if(emailValido&&passValido){

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), originalPassword); // Current Login Credentials \\
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        guardaEmail(email, RootRef);
                                        guardaPassword(password, RootRef);
                                        actualizaPerfil(nombre,apellido,edad,RootRef);
                                        actualizaImagen(imageUri,RootRef);

                                        mAuth.signOut();
                                        progressDialog.dismiss();
                                        Toast.makeText(PerfilActivity.this,"Perfil actualizado", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PerfilActivity.this, ActivityMain.class));

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PerfilActivity.this,"Contraseña ACTUAL incorrecta",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        emailRep=0;
    }



    private void guardaEmail(final String email, final DatabaseReference rootRef) {
        final String ID = user.getUid();
        final HashMap<String,Object> userdataMap=new HashMap<>();

        //inputEmail.getText().toString.trim
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userdataMap.put("email",email);
                            rootRef.child("Usuarios").child(userType).child(ID).updateChildren(userdataMap);
                        }else{
                            Toast.makeText(PerfilActivity.this,"Error en la inserción de email, el email ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardaPassword(final String password, final DatabaseReference rootRef) {
        final String ID = user.getUid();
        final HashMap<String,Object> userdataMap=new HashMap<>();
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userdataMap.put("password",password);
                    rootRef.child("Usuarios").child(userType).child(ID).updateChildren(userdataMap);
                }else{
                    Toast.makeText(PerfilActivity.this,"Erroral modificar contraseña",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void actualizaPerfil(String nombre, String apellido, String edad, DatabaseReference rootRef) {
        final String ID = user.getUid();
        HashMap<String,Object> userdataMap=new HashMap<>();
        userdataMap.put("nombre",nombre);
        userdataMap.put("apellido",apellido);
        userdataMap.put("edad",edad);
        rootRef.child("Usuarios").child(userType).child(ID).updateChildren(userdataMap);
    }


    private void actualizaImagen(final Uri imageUri, final DatabaseReference rootRef) {

        if(imageUri!=null){
            final StorageReference fileref=storageProfilePictureRef.child(user.getUid()+".jpg");
            uploadTask=fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();
                        HashMap<String,Object> userMap= new HashMap<>();
                        userMap.put("foto",myUrl);

                        //FIJAMOS FOTO DEL PERFIL DE FIREBASE
                        UserProfileChangeRequest profileUpdatesPhoto = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                        user.updateProfile(profileUpdatesPhoto);

                        //ACTUALIZAMOS LOS DATOS CUYO NODO PRINCIPAL SEA IDÉNTICO AL ID DEL USUARIO ACTUAL
                        rootRef.child("Usuarios").child(userType).child(user.getUid()).updateChildren(userMap);

                    }else{

                        Toast.makeText(PerfilActivity.this,"Error en la modificación de foto", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

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








    private void getUsuarioInfo(final FirebaseUser user) {

        //Ruta donde buscaremos la información asociada al usuario
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Usuarios").child("Profesor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    //Accedemos a la base de datos en la ruta indicada
                    RootRef.child("Usuarios").child("Profesor").child(snapShot.getKey()).addValueEventListener(new ValueEventListener() {
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
                                String edadBBDD= datosUsuario.getEdad();
                                String emailBBDD= datosUsuario.getEmail();
                                String passwordBBDD=datosUsuario.getPassword();

                                //Se introducen los datos obtenidos en los elementos de la vista
                                if(fotoBBDD!=null){
                                    Picasso.get().load(fotoBBDD).into(profileImage);
                                    Picasso.get().load(fotoBBDD).into(imageProfile);
                                }

                                userName.setText(nombreBBDD+" "+apellidosBBDD);

                                //Rellenamos los campos con los datos actuales
                                inputName.setText(nombreBBDD);
                                inputLastname.setText(apellidosBBDD);
                                inputAge.setText(edadBBDD);
                                inputEmail.setText(emailBBDD);
                                inputPassword.setText(passwordBBDD);



                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                userType="Profesor";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        RootRef.child("Usuarios").child("Alumno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    //Accedemos a la base de datos en la ruta indicada
                    RootRef.child("Usuarios").child("Alumno").child(snapShot.getKey()).addValueEventListener(new ValueEventListener() {
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
                                String edadBBDD= datosUsuario.getEdad();
                                String emailBBDD= datosUsuario.getEmail();
                                String passwordBBDD=datosUsuario.getPassword();

                                //Se introducen los datos obtenidos en los elementos de la vista
                                if(fotoBBDD!=null){
                                    Picasso.get().load(fotoBBDD).into(profileImage);
                                    Picasso.get().load(fotoBBDD).into(imageProfile);
                                }

                                userName.setText(nombreBBDD+" "+apellidosBBDD);

                                //Rellenamos los campos con los datos actuales
                                inputName.setText(nombreBBDD);
                                inputLastname.setText(apellidosBBDD);
                                inputAge.setText(edadBBDD);
                                inputEmail.setText(emailBBDD);
                                inputPassword.setText(passwordBBDD);



                            }

                            userType="Alumno";
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
