package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.Models.Asignaturas;
import com.example.proyecto.Models.Grupos;
import com.example.proyecto.Models.Usuarios;
import com.example.proyecto.R;
import com.example.proyecto.ViewHolders.SubjectViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CrearUsuariosActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener { //implements AdapterView.OnItemSelectedListener

    //Creamos los elementos que vamos a necesitar para recoger los datos de registro
    private CircleImageView imageProfile;
    private EditText inputName, inputLastname, inputAge, inputEmail, inputPassword;
    private Button signUp_btn;
    private ProgressDialog barraCarga;
    private TextView addImageprofile, txtGrupo, txtTipo,txtTitulo, lbl_email, lbl_password,lbl_asignaturas;

    private Spinner userType;
    private Spinner studentGroup;

    static String tipoUsuario = "";
    static String grupoEstudiante = "";
    static List<String> subjectList = new ArrayList<String>();
    static List<String> subjectBBDD = new ArrayList<String>();
    static Boolean usuarioLogueado=false;

    static String grupoBBDD = "";
    static String modificar = "";
    private String idChecked;

    static String emailUserActual = "";
    static String passwordUserActual = "";
    static String IDobtenido = "";
    static String tipoObtenido = "";
    static String tipoUsuarioActual = "";




    private String nombreChecked;
    private String cursoChecked;
    private String descripcionChecked;
    private String fotoChecked;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;

    //Para guardar info en el storage de Firebase
    StorageReference storageProfilePictureRef;

    DatabaseReference gruposRef;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //para crear la cuenta de usuario de Firebase



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuarios);

        txtTitulo = (TextView) findViewById(R.id.tv_login_crear);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        String IDactual = user.getUid();

        Bundle bundle = this.getIntent().getExtras();

        //Referenciamos los elementos de la vista
        userType = (Spinner) findViewById(R.id.tipo_usuario_crear);
        studentGroup = (Spinner) findViewById(R.id.grupo_alumno_crear);

        lbl_email = (TextView) findViewById(R.id.lbl_email);
        lbl_password= (TextView) findViewById(R.id.lbl_password);
        txtTipo = (TextView) findViewById(R.id.lbl_tipousuario);
        txtGrupo = (TextView) findViewById(R.id.lbl_grupo_crear);
        lbl_asignaturas = (TextView) findViewById(R.id.lbl_asignaturas_crear);
        inputName = (EditText) findViewById(R.id.sign_nombre_crear);
        inputLastname = (EditText) findViewById(R.id.sign_apellido_crear);
        inputAge = (EditText) findViewById(R.id.sign_Edad_crear);
        inputEmail = (EditText) findViewById(R.id.sign_email_crear);
        inputPassword = (EditText) findViewById(R.id.sign_input_password_crear);
        signUp_btn = (Button) findViewById(R.id.sign_btn_crear);

        imageProfile = (CircleImageView) findViewById(R.id.sign_profile_image_crear);
        addImageprofile = (TextView) findViewById(R.id.sign_image_profile_btn_crear);
        recyclerView = findViewById(R.id.sign_recycler_subjects_crear);

        barraCarga = new ProgressDialog(this);


        if(bundle!=null){

            modificar = bundle.getString("modify");
            emailUserActual = bundle.getString("email");
            passwordUserActual = bundle.getString("password");
            IDobtenido = bundle.getString("ID");
            tipoObtenido = bundle.getString("tipo");
            tipoUsuarioActual = bundle.getString("tipoActual");



            if (modificar.equals("true")) {
                txtTitulo.setText("Modificar perfil");
                getUsuarioInfo(IDobtenido);
                userType.setVisibility(View.GONE);
                if(IDobtenido.equals(IDactual)){
                    usuarioLogueado=true;
                }else{
                    inputEmail.setVisibility(View.GONE);
                    inputPassword.setVisibility(View.GONE);
                    lbl_email.setVisibility(View.GONE);
                    lbl_password.setVisibility(View.GONE);
                }

                if(tipoObtenido.equals("Profesor")){
                    txtGrupo.setVisibility(View.GONE);
                    studentGroup.setVisibility(View.GONE);
                }

                if(tipoUsuarioActual.equals("Alumno")){
                    lbl_asignaturas.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    txtGrupo.setVisibility(View.GONE);
                    studentGroup.setVisibility(View.GONE);
                }

            }

        }else{
            Toast.makeText(this, "No se recibe nada", Toast.LENGTH_SHORT).show();
        }

        if(tipoObtenido!=null){
            txtTipo.setText("Tipo de usuario: "+tipoObtenido);
        }


        subjectList.clear();


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final DatabaseReference AsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Asignaturas");

        FirebaseRecyclerOptions<Asignaturas> opciones = new FirebaseRecyclerOptions.Builder<Asignaturas>()
                .setQuery(AsignaturasRef, Asignaturas.class)
                .build();

        final FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder> adapterSubject = new FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final Asignaturas model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA
                holder.txtName.setText(model.getNombre());
                holder.txtCourse.setText(model.getCurso());
                holder.checkBoxSubject.setVisibility(View.VISIBLE);
                holder.imgSubject.setVisibility(View.GONE);

                if (model.getFoto() == null) {
                    Picasso.get().load(R.drawable.msn_logo).resize(80, 80).into(holder.imgSubject);
                } else {
                    Picasso.get().load(model.getFoto()).resize(80, 80).into(holder.imgSubject);
                }



                /*List<CheckBox> items = new ArrayList<CheckBox>();

                items.add(holder.checkBoxSubject);

                for (final CheckBox item : items) {

                    item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if (item.isChecked()) {
                                idChecked = model.getID();
                                nombreChecked= model.getNombre();
                                cursoChecked= model.getCurso();
                                descripcionChecked= model.getDescripcion();
                                if(model.getFoto()!=null){
                                    fotoChecked= model.getFoto();
                                }
                                subjectList.add(idChecked);
                            } else {

                                subjectList.remove(model.getID());

                            }

                        }
                    });


                }*/

                checkingSubject(holder,model);




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

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Fotos de perfil");
        gruposRef = FirebaseDatabase.getInstance().getReference();
        loadGrupos();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipos_usuario, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
        userType.setOnItemSelectedListener(this);

        //Al pulsar el botón se guardan los datos de usuario y se crea la cuenta
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crearModCuenta();
            }
        });

        //Para añadir imagen en la BBDD, lleva a otro activity donde seleccionas la imagen
        addImageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity(imageUri).setAspectRatio(1, 1)
                        .start(CrearUsuariosActivity.this);

            }
        });
    }

    private void checkingSubject(final SubjectViewHolder holder, final Asignaturas model) {


        if(modificar.equals("true")){

            DatabaseReference checkedRef = FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios").child(IDobtenido).child("asignaturas");


            checkedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){

                        for (final DataSnapshot snapShot : dataSnapshot.getChildren()){
                            Asignaturas datosAsignaturas = snapShot.getValue(Asignaturas.class);
                            String idSubjectBBDD=datosAsignaturas.getID();

                            if(idSubjectBBDD.equals(model.getID())){

                                holder.checkBoxSubject.setChecked(true);

                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }





        List<CheckBox> items = new ArrayList<CheckBox>();

                items.add(holder.checkBoxSubject);

                for (final CheckBox item : items) {

                    item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if (item.isChecked()) {
                                idChecked = model.getID();
                                nombreChecked= model.getNombre();
                                cursoChecked= model.getCurso();
                                descripcionChecked= model.getDescripcion();
                                if(model.getFoto()!=null){
                                    fotoChecked= model.getFoto();
                                }
                                subjectList.add(idChecked);
                            } else {

                                subjectList.remove(model.getID());

                            }

                        }
                    });


                }




    }


    //Nos permite sacar una imagen para el perfil a partir de otra imagen, acotando el área a mostrar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.getUri();
                imageProfile.setImageURI(imageUri);

            }
        } else {
            Toast.makeText(this, "Error al obtener foto, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    private void loadGrupos() {
        final List<Grupos> grupos = new ArrayList<>();
        grupos.add(new Grupos(null, null, "Ninguno"));

        ArrayAdapter<Grupos> arrayAdapter = new ArrayAdapter<>(CrearUsuariosActivity.this, android.R.layout.simple_dropdown_item_1line, grupos);
        studentGroup.setAdapter(arrayAdapter);

        gruposRef.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {





            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("Usuarios")
                        .child(IDobtenido);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuarios datosUsuario=dataSnapshot.getValue(Usuarios.class);
                        grupo
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/


                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String ID = ds.getKey();
                        String numero = ds.child("numero").getValue().toString();
                        String nombre = ds.child("nombre").getValue().toString();
                        grupos.add(new Grupos(ID, numero, nombre));

                        for(int i=0; i<grupos.size(); i++){

                            if(grupoBBDD.equals(grupos.get(i).getNombre())){
                                studentGroup.setSelection(i);
                            }

                        }

                    }


                    studentGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            //Toast.makeText(CrearUsuariosActivity.this,
                                   // parent.getItemAtPosition(position).toString(),
                                   // Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //En este método nos aseguramos de que los campos estén rellenos
    private void crearModCuenta() {
        String name = inputName.getText().toString().trim();
        String lastname = inputLastname.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(age) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Asegúrese de rellenar los campos obligatorios", Toast.LENGTH_SHORT).show();

        } else {
            //Ahora se carga una ventana emergente de proceso y se procede a guardar los datos
            barraCarga.setTitle("Crear cuenta");
            barraCarga.setMessage("Comprobando que todo está en orden, espere por favor");
            barraCarga.setCanceledOnTouchOutside(false);
            barraCarga.show();
            guardaDatosUsuario(name, lastname, age, email, password);
        }
    }


    //método empleado en guardar los datos
    private void guardaDatosUsuario(final String nombre, final String apellido, final String edad,
                                    final String email, final String password) {

        //Creamos la referencia o ruta donde guardamos los datos
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        //Bajo el nodo de "Usuarios", guardamos los datos recogidos en subnodos

        //RootRef.child("Usuarios").addValueEventListener(new ValueEventListener()
        RootRef.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Nos aseguramos de que la contraseña tenga 6 caracteres
                if (password.length() < 6) {
                    Toast.makeText(CrearUsuariosActivity.this, "Debe introducir un password de al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    barraCarga.dismiss();
                } else {

                    //Método de Firebase que crea la cuenta con el email y contraseña recogidos
                    if (modificar.equals("true")) {

                        updateUser(email, password, nombre, apellido, edad, user, imageUri, mAuth, RootRef, barraCarga);

                    } else {

                        if (tipoUsuario.equals("--")) {
                            Toast.makeText(CrearUsuariosActivity.this, "Debe seleccionar un tipo", Toast.LENGTH_SHORT).show();
                            barraCarga.dismiss();
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(CrearUsuariosActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            //Una vez realizada la tarea, si se ha realizado con éxito
                                            // se procede a guardar los datos en la ruta proporcionad
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information

                                                guardaenFirebase(nombre, apellido, edad, email, password, RootRef);
                                                //Se quita la ventana emergente de proceso
                                                barraCarga.dismiss();
                                                //Mensaje emergente de éxito
                                                Toast.makeText(CrearUsuariosActivity.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                                                //Volvemos a la página principal

                                                mAuth.signOut();
                                                startActivity(new Intent(CrearUsuariosActivity.this, ActivityMain.class));

                                                //loginActualUser();
                                                subjectList.clear();


                                            } else {
                                                // En caso de fallo, se muestra un mensaje emergente de error

                                                barraCarga.dismiss();
                                                Toast.makeText(CrearUsuariosActivity.this, "Error, email ya existe o no tiene formato adecuado", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loginActualUser() {
        mAuth.signInWithEmailAndPassword(emailUserActual, passwordUserActual)
                .addOnCompleteListener(CrearUsuariosActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                            // Sign in success, update UI with the signed-in user's information
                            barraCarga.dismiss();

                            getUsuarioInfo(mAuth.getUid());
                            Toast.makeText(CrearUsuariosActivity.this,"Todo bien",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(CrearUsuariosActivity.this, ActivityMain.class));
                            mAuth.signOut();


                            if(tipoUsuarioActual.equals("Profesor")){
                                int tipo=1;
                                Toast.makeText(CrearUsuariosActivity.this,tipoUsuarioActual,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CrearUsuariosActivity.this, UserActivity.class).putExtra("tipo",tipo));
                                finish();
                            }else{
                                int tipo=0;
                                Toast.makeText(CrearUsuariosActivity.this,tipoUsuarioActual,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CrearUsuariosActivity.this, ReunionesActivity.class).putExtra("tipo",tipo));
                                finish();
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CrearUsuariosActivity.this, "No se retornó al usuario actual", Toast.LENGTH_SHORT).show();
                            barraCarga.dismiss();
                        }
                    }
                });

    }


    private void updateUser(final String email, final String password, final String nombre,
                            final String apellido, final String edad, FirebaseUser user,
                            final Uri imageUri, final FirebaseAuth mAuth,
                            final DatabaseReference rootRef, final ProgressDialog progressDialog) {





        if(usuarioLogueado){

            assert emailUserActual != null;
            assert passwordUserActual != null;

            AuthCredential credential = EmailAuthProvider
                    .getCredential(emailUserActual, passwordUserActual); // Current Login Credentials \\
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                guardaEmail(email, rootRef);
                                guardaPassword(password, rootRef);
                                actualizaPerfil(nombre, apellido, edad, rootRef);
                                actualizaImagenPerfil(imageUri, rootRef);

                                if(tipoObtenido.equals("Profesor")){
                                    int tipo=1;
                                    startActivity(new Intent(CrearUsuariosActivity.this, UserActivity.class).putExtra("tipo",tipo));
                                }else{
                                    int tipo=0;
                                    startActivity(new Intent(CrearUsuariosActivity.this, ReunionesActivity.class).putExtra("tipo",tipo));
                                }

                                progressDialog.dismiss();


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(CrearUsuariosActivity.this, "Contraseña ACTUAL incorrecta", Toast.LENGTH_SHORT).show();
                            }

                        }

                    });

        } else {

            actualizaPerfil(nombre, apellido, edad, rootRef);
            actualizaImagenPerfil(imageUri, rootRef);

            if(tipoObtenido.equals("Profesor")){
                int tipo=1;
                startActivity(new Intent(CrearUsuariosActivity.this, UserActivity.class).putExtra("tipo",tipo));
            }else{
                int tipo=0;
                startActivity(new Intent(CrearUsuariosActivity.this, ReunionesActivity.class).putExtra("tipo",tipo));
            }

            progressDialog.dismiss();


        }







    }


    private void guardaEmail(final String email, final DatabaseReference rootRef) {
        final String ID = user.getUid();
        final HashMap<String, Object> userdataMap = new HashMap<>();

        //inputEmail.getText().toString.trim
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userdataMap.put("email", email);
                            rootRef.child("Usuarios").child(ID).updateChildren(userdataMap);
                        } else {
                            Toast.makeText(CrearUsuariosActivity.this, "Error en la inserción de email, el email ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardaPassword(final String password, final DatabaseReference rootRef) {
        final String ID = user.getUid();
        final HashMap<String, Object> userdataMap = new HashMap<>();
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userdataMap.put("password", password);
                    rootRef.child("Usuarios").child(ID).updateChildren(userdataMap);
                } else {
                    Toast.makeText(CrearUsuariosActivity.this, "Contraseña no modificada", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void actualizaPerfil(String nombre, String apellido, String edad, DatabaseReference rootRef) {
        final String ID =IDobtenido;
        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("nombre", nombre);
        userdataMap.put("apellido", apellido);
        userdataMap.put("edad", edad);

        grupoEstudiante=studentGroup.getSelectedItem().toString();

        if(tipoObtenido.equals("Alumno")){
            userdataMap.put("grupo", grupoEstudiante);
        }


        if (!subjectList.isEmpty()) {

            FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios")
                    .child(ID)
                    .child("asignaturas")
                    .removeValue();

            //userdataMap.remove("asignaturas");

            //userdataMap.put("asignaturas", subjectList);


            for(int i=0;i<subjectList.size();i++){

                String id= subjectList.get(i);

                DatabaseReference fromPath= FirebaseDatabase.getInstance().getReference()
                        .child("Asignaturas").child(id);

                final DatabaseReference subjRef = FirebaseDatabase.getInstance().getReference()
                        .child("Usuarios")
                        .child(ID);


                DatabaseReference toPath = subjRef.child("asignaturas").child(id);

                copyRecord(fromPath,toPath);


            }

        }

        rootRef.child("Usuarios").child(ID).updateChildren(userdataMap);

    }


    private void actualizaImagenPerfil(final Uri imageUri, final DatabaseReference rootRef) {

        if (imageUri != null) {
            final StorageReference fileref = storageProfilePictureRef.child(IDobtenido + ".jpg");
            uploadTask = fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("foto", myUrl);

                        //FIJAMOS FOTO DEL PERFIL DE FIREBASE
                        UserProfileChangeRequest profileUpdatesPhoto = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                        user.updateProfile(profileUpdatesPhoto);

                        //ACTUALIZAMOS LOS DATOS CUYO NODO PRINCIPAL SEA IDÉNTICO AL ID DEL USUARIO ACTUAL
                        rootRef.child("Usuarios").child(IDobtenido).updateChildren(userMap);

                    } else {

                        Toast.makeText(CrearUsuariosActivity.this, "Error en la modificación de foto", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //SE COPIAN LOS NODOS DESDE LA RUTA DE ORIGEN A SU NUEVO DESTINO
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }







    //Método empleado para guardar datos en Firebase Realtime Databasee
    private void guardaenFirebase(String nombre, String apellido, String edad, String email, String password, DatabaseReference rootRef) {
        final String ID = mAuth.getUid();

        //Sirve para mapear los datos a guardar, a la izquierda el nombre del nodo,
        //a la derecha el valor quese guarda en dicho nodo
        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("ID", ID);
        userdataMap.put("nombre", nombre);
        userdataMap.put("apellido", apellido);
        userdataMap.put("edad", edad);
        userdataMap.put("email", email);
        userdataMap.put("password", password);
        userdataMap.put("type", tipoUsuario);


        grupoEstudiante=studentGroup.getSelectedItem().toString();

        if (tipoUsuario.equals("Alumno")) {
            userdataMap.put("grupo", grupoEstudiante);
        }

        //Se guardan los datos de usuario dentro de la tabla/nodo "Usuarios", bajo el nodo de la ID
        rootRef.child("Usuarios").child(ID).updateChildren(userdataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) { //En caso exitoso
                            Toast.makeText(CrearUsuariosActivity.this, "Datos en BBDD insertados", Toast.LENGTH_SHORT).show();

                        } else { //En caso de error
                            Toast.makeText(CrearUsuariosActivity.this, "Error en la inserción en la BBDD", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        for(int i=0;i<subjectList.size();i++){

            String id= subjectList.get(i);

            DatabaseReference fromPath= FirebaseDatabase.getInstance().getReference()
                    .child("Asignaturas").child(id);

            final DatabaseReference subjRef = FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios")
                    .child(ID);


            DatabaseReference toPath = subjRef.child("asignaturas").child(id);

            copyRecord(fromPath,toPath);

        }


        //Para crear la ruta donde se guardará la imágen de perfil
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        //Método para guardar la foto de perfil, se le pasa la referencia Uri de la foto, y el
        // elemento que servirá para crear la ruta de ubicación de la foto en Storage

        //Este try controla la excepción en caso de que no haya foto seleccionada y el Uri sea null
        try {
            actualizaImagen(imageUri, ID, RootRef);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Se cierra la sesión de autenticación, de lo contrario se podría quedar abierta con el usuario
        //recién creado

    }


    private void actualizaImagen(final Uri imageUri, final String ID, final DatabaseReference rootRef) {

        final FirebaseUser user = mAuth.getCurrentUser();
        if (imageUri != null) {
            //Ruta donde se guarda la foto de usuario en Firebase Storage
            final StorageReference fileref = storageProfilePictureRef.child(ID + ".jpg");
            uploadTask = fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("foto", myUrl);

                        //FIJAMOS FOTO DEL PERFIL DE FIREBASE
                        UserProfileChangeRequest profileUpdatesPhoto = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                        user.updateProfile(profileUpdatesPhoto);
                        //ACTUALIZAMOS LOS DATOS CUYO NODO PRINCIPAL SEA IDÉNTICO AL ID DEL USUARIO ACTUAL
                        rootRef.child("Usuarios").child(ID).updateChildren(userMap);

                    } else {
                        Toast.makeText(CrearUsuariosActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void getUsuarioInfo(final String ID) {

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
                            //String id = user.getUid();
                            //Se obtienen los string que representan las IDs en la BBDD
                            String idBBDD = datosUsuario.getID();
                            //Si el ID del usuario actual se corresponde con alguna de las guardadas,
                            //se obtienen los datos
                            if (idBBDD.equals(ID)) {

                                String fotoBBDD = null;
                                //Se obtiene el url de ubicación de la foto en caso de estar guardado
                                if (snapShot.child("foto").exists()) {
                                    fotoBBDD = datosUsuario.getFoto();
                                }
                                //Se obtienen nombre y apellidos
                                String nombreBBDD = datosUsuario.getNombre();
                                String apellidosBBDD = datosUsuario.getApellido();
                                String edadBBDD = datosUsuario.getEdad();
                                String emailBBDD = datosUsuario.getEmail();
                                String passwordBBDD = datosUsuario.getPassword();




                                //subjectBBDD = datosUsuario.getAsignaturas();

                                if (snapShot.child("grupo").exists()) {
                                    grupoBBDD = datosUsuario.getGrupo();
                                }



                                //passwordOriginal = datosUsuario.getPassword();


                                //Se introducen los datos obtenidos en los elementos de la vista
                                if (fotoBBDD != null) {
                                    //Picasso.get().load(fotoBBDD).into(profileImage);
                                    Picasso.get().load(fotoBBDD).into(imageProfile);
                                }

                                //userName.setText(nombreBBDD+" "+apellidosBBDD);

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        tipoUsuario = parent.getItemAtPosition(position).toString();

        if (position == 2) {
            txtGrupo.setVisibility(View.GONE);
            studentGroup.setVisibility(View.GONE);
        } else {
            txtGrupo.setVisibility(View.VISIBLE);
            studentGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
