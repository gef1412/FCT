package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;



public class SignUpActivity extends AppCompatActivity{ //implements AdapterView.OnItemSelectedListener

    //Creamos los elementos que vamos a necesitar para recoger los datos de registro
    private CircleImageView imageProfile;
    private EditText inputName, inputLastname, inputAge, inputEmail, inputPassword;
    private Button signUp_btn;
    private ProgressDialog barraCarga;
    private TextView addImageprofile, txtGrupo;

    //private Spinner userType;
    //private Spinner studentGroup;

    //static String tipoUsuario="";
    //static String grupoEstudiante="";
    //private String subjects;
    //static List<String> subjectList = new ArrayList<String>();;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    Uri imageUri;
    String myUrl="";
    StorageTask uploadTask;

    //Para guardar info en el storage de Firebase
    StorageReference storageProfilePictureRef;

    DatabaseReference gruposRef;

    //para crear la cuenta de usuario de Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Referenciamos los elementos de la vista
        /*userType= (Spinner) findViewById(R.id.tipo_usuario);
        studentGroup= (Spinner) findViewById(R.id.grupo_alumno);*/

        //txtGrupo=(TextView) findViewById(R.id.lbl_grupo);
        inputName= (EditText) findViewById(R.id.sign_nombre);
        inputLastname= (EditText) findViewById(R.id.sign_apellido);
        inputAge= (EditText) findViewById(R.id.sign_Edad);
        inputEmail= (EditText) findViewById(R.id.sign_email);
        inputPassword= (EditText) findViewById(R.id.sign_input_password);
        signUp_btn= (Button) findViewById(R.id.sign_btn);
        barraCarga=new ProgressDialog(this);




        /*recyclerView = findViewById(R.id.sign_recycler_subjects);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/

        /*final DatabaseReference AsignaturasRef = FirebaseDatabase.getInstance().getReference().child("Asignaturas");

        FirebaseRecyclerOptions<Asignaturas> opciones = new FirebaseRecyclerOptions.Builder<Asignaturas>()
                .setQuery(AsignaturasRef,Asignaturas.class)
                .build();

        final FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder> adapterSubject = new FirebaseRecyclerAdapter<Asignaturas, SubjectViewHolder>(opciones) {
            @Override
            protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final Asignaturas model) {

                //INFLAMOS LOS ELEMENTOS DE LA LISTA
                holder.txtName.setText(model.getNombre());
                holder.txtCourse.setText(model.getCurso());
                holder.checkBoxSubject.setVisibility(View.VISIBLE);
                holder.imgSubject.setVisibility(View.GONE);


                List<CheckBox> items = new ArrayList<CheckBox>();

                items.add(holder.checkBoxSubject);

                for(final CheckBox item : items){
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int pos = holder.getAdapterPosition();

                            if(item.isChecked()){
                                subjects = model.getNombre();
                                subjectList.add(subjects);
                            }else{
                                subjectList.remove(subjects);
                            }

                        }
                    });
                }



                *//*holder.checkBoxSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();

                        if(holder.checkBoxSubject.isChecked()){
                            subjects = model.getNombre();
                            subjectList.add(subjects);
                        }else{
                            subjectList.remove(subjects);
                        }

                    }
                });*//*



                if(model.getFoto()==null){
                    Picasso.get().load(R.drawable.msn_logo).resize(80,80).into(holder.imgSubject);
                }else{
                    Picasso.get().load(model.getFoto()).resize(80,80).into(holder.imgSubject);
                }


            }

            //ESTE MÉTODO INDICA LA VISTA UTILIZADA PARA MOSTRAR EL PRODUCTO EN LA LISTA
            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_item_layout, parent, false);
                SubjectViewHolder holder= new SubjectViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapterSubject);
        adapterSubject.startListening();*/








        mAuth = FirebaseAuth.getInstance();
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Fotos de perfil");
        gruposRef=FirebaseDatabase.getInstance().getReference();
        //loadGrupos();

        imageProfile = (CircleImageView) findViewById(R.id.sign_profile_image);
        addImageprofile=(TextView) findViewById(R.id.sign_image_profile_btn);

        /*ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.tipos_usuario,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
        userType.setOnItemSelectedListener(this);*/


        //Al pulsar el botón se guardan los datos de usuario y se crea la cuenta
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCuenta();
            }
        });

        //Para añadir imagen en la BBDD, lleva a otro activity donde seleccionas la imagen
        addImageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity(imageUri).setAspectRatio(1,1)
                        .start(SignUpActivity.this);

            }
        });
    }

    //Nos permite sacar una imagen para el perfil a partir de otra imagen, acotando el área a mostrar
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
            Toast.makeText(this,"Error al obtener foto, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
        }

    }


    /*private void loadGrupos(){
        final List<Grupos> grupos=new ArrayList<>();
        grupos.add(new Grupos(null,null,"Ninguno"));

        gruposRef.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        String ID=ds.getKey();
                        String numero= ds.child("numero").getValue().toString();
                        String nombre= ds.child("nombre").getValue().toString();
                        grupos.add(new Grupos(ID,numero,nombre));
                    }

                    ArrayAdapter<Grupos> arrayAdapter=new ArrayAdapter<>(SignUpActivity.this,android.R.layout.simple_dropdown_item_1line,grupos);
                    studentGroup.setAdapter(arrayAdapter);
                    studentGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            //Obtenemos el nombre del grupo seleccionado
                            grupoEstudiante=parent.getItemAtPosition(position).toString();

                            //CON ESTO OBTENDRÍAMOS EL ID. SERÍA CONVENIENTE EN CASO DE MODIFICAR LOS
                            //DATOS DEL GRUPO
                            //grupoEstudiante=grupos.get(position).getID();
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
    }*/

    //En este método nos aseguramos de que los campos estén rellenos
    private void crearCuenta(){
        String name=inputName.getText().toString().trim();
        String lastname=inputLastname.getText().toString().trim();
        String age= inputAge.getText().toString().trim();
        String email=inputEmail.getText().toString().trim();
        String password=inputPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(lastname)||TextUtils.isEmpty(age)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(this,"Asegúrese de rellenar los campos obligatorios",Toast.LENGTH_SHORT).show();

        } else{
            //Ahora se carga una ventana emergente de proceso y se procede a guardar los datos
            barraCarga.setTitle("Crear cuenta");
            barraCarga.setMessage("Comprobando que todo está en orden, espere por favor");
            barraCarga.setCanceledOnTouchOutside(false);
            barraCarga.show();
            guardaDatosUsuario(name,lastname,age,email,password);
        }
    }


    //método empleado en guardar los datos
    private void guardaDatosUsuario(final String nombre, final String apellido, final String edad,
                                    final String email, final String password){

        //Creamos la referencia o ruta donde guardamos los datos
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        //Bajo el nodo de "Usuarios", guardamos los datos recogidos en subnodos
        RootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Nos aseguramos de que la contraseña tenga 6 caracteres
                    if(password.length()<6){
                        Toast.makeText(SignUpActivity.this,"Debe introducir un password de al menos 6 caracteres",Toast.LENGTH_SHORT).show();
                        barraCarga.dismiss();
                    }else{

                        //Método de Firebase que crea la cuenta con el email y contraseña recogidos
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
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
                                            Toast.makeText(SignUpActivity.this,"Cuenta creada con éxito",Toast.LENGTH_SHORT).show();
                                            //Volvemos a la página principal

                                            mAuth.signOut();
                                            Intent intent= new Intent(SignUpActivity.this, ActivityMain.class);
                                            //subjectList.clear();
                                            startActivity(intent);
                                        } else {
                                            // En caso de fallo, se muestra un mensaje emergente de error

                                            barraCarga.dismiss();
                                            Toast.makeText(SignUpActivity.this,"Error, inténtelo de nuevo",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    private void actualizaImagen(final Uri imageUri, final String ID, final DatabaseReference rootRef) {

        final FirebaseUser user= mAuth.getCurrentUser();
        if(imageUri!=null){
            //Ruta donde se guarda la foto de usuario en Firebase Storage
            final StorageReference fileref=storageProfilePictureRef.child(ID+".jpg");
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
                        rootRef.child("Usuarios").child(ID).updateChildren(userMap);

                    }else{
                        Toast.makeText(SignUpActivity.this,"Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //Método empleado para guardar datos en Firebase Realtime Databasee
    private void guardaenFirebase(String nombre, String apellido, String edad, String email, String password, DatabaseReference rootRef) {
        final String ID = mAuth.getUid();

        //Sirve para mapear los datos a guardar, a la izquierda el nombre del nodo,
        //a la derecha el valor quese guarda en dicho nodo
        HashMap<String,Object> userdataMap=new HashMap<>();
        userdataMap.put("ID",ID);
        userdataMap.put("nombre",nombre);
        userdataMap.put("apellido",apellido);
        userdataMap.put("edad",edad);
        userdataMap.put("email",email);
        userdataMap.put("password",password);
        userdataMap.put("type","Alumno");
        //userdataMap.put("asignaturas", subjectList);


        /*if(tipoUsuario.equals("Alumno")){
            userdataMap.put("grupo",grupoEstudiante);
        }*/

        //Se guardan los datos de usuario dentro de la tabla/nodo "Usuarios", bajo el nodo de la ID
        rootRef.child("Usuarios").child(ID).updateChildren(userdataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){ //En caso exitoso
                            Toast.makeText(SignUpActivity.this,"Datos en BBDD insertados",Toast.LENGTH_SHORT).show();

                        }else{ //En caso de error
                            Toast.makeText(SignUpActivity.this,"Error en la inserción en la BBDD",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Para crear la ruta donde se guardará la imágen de perfil
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        //Método para guardar la foto de perfil, se le pasa la referencia Uri de la foto, y el
        // elemento que servirá para crear la ruta de ubicación de la foto en Storage

        //Este try controla la excepción en caso de que no haya foto seleccionada y el Uri sea null
        try{
            actualizaImagen(imageUri, ID, RootRef);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        //Se cierra la sesión de autenticación, de lo contrario se podría quedar abierta con el usuario
        //recién creado

    }


    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tipoUsuario=parent.getItemAtPosition(position).toString();
        if(position==1){
            txtGrupo.setVisibility(View.GONE);
            studentGroup.setVisibility(View.GONE);
        }else{
            txtGrupo.setVisibility(View.VISIBLE);
            studentGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/
}
