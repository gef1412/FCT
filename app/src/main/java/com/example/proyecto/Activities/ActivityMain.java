package com.example.proyecto.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto.R;
import com.example.proyecto.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityMain extends AppCompatActivity {

    //Elementos necesarios del activity
    RelativeLayout gallery1, gallery2;
    Button btnlogin, btnreset, btnsignup;
    TextView textogoogle;
    EditText mMail, mPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog barraCarga;
    private SharedPreferences prefs;
    private Switch rememberMe;


    // private static final String TAG = MainActivity.class.getName();

    //Para hacer uso del Splash Screen
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            textogoogle.setVisibility(View.INVISIBLE);
            gallery1.setVisibility(View.VISIBLE);
            gallery2.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(ActivityMain.this, UserActivity.class));
            finish();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referenciamos los elementos de la vista
        gallery1 = (RelativeLayout) findViewById(R.id.gallery1);
        gallery2 = (RelativeLayout) findViewById(R.id.gallery2);
        textogoogle = (TextView) findViewById(R.id.textogoogle);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btnreset = (Button) findViewById(R.id.btnreset);
        mMail = (EditText) findViewById(R.id.mail);
        mPassword = (EditText) findViewById(R.id.password);
        rememberMe= (Switch) findViewById(R.id.remember_me_switch);
        barraCarga=new ProgressDialog(this);

        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Para determinar el tiempo de Splash Screen
        handler.postDelayed(runnable, 2500);

        //Botón que nos lleva al activity de registro
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        //Al activity de reseteo de contraseña
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this,RecuperarActivity.class);
                startActivity(intent);
            }
        });


        //Botón de login
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se recogen las credenciales para loguear al usuario
                final String email= mMail.getText().toString();
                final String password= mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(ActivityMain.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                                    // Sign in success, update UI with the signed-in user's information
                                    barraCarga.dismiss();
                                    Toast.makeText(ActivityMain.this,"Bienvenido",Toast.LENGTH_SHORT).show();

                                        Intent intent= new Intent(getApplication(), UserActivity.class);
                                        startActivity(intent);
                                        saveOnPreferences(email, password);
                                        finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    barraCarga.dismiss();
                                    Toast.makeText(ActivityMain.this,"Error, compruebe el usuario o contraseña",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        //se auto-rellenan el email y contraseña en caso de haberse guardado
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setCredentialsIfExist();

        //Si el switch está activado guarda los valores introducidos en los campos de email y password
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveOnPreferences(mMail.getText().toString().trim(), mPassword.getText().toString().trim());
            }
        });

    }

    //método que fija el email y contraseña que se hayan guardado
    private void setCredentialsIfExist() {
        String email = Utils.getUserMailPrefs(prefs);
        String password = Utils.getUserPassPrefs(prefs);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mMail.setText(email);
            mPassword.setText(password);
            rememberMe.setChecked(true);
        }
    }

    //método que guarda el email y contraseña introducidos
    private void saveOnPreferences(String email, String password) {
        if (rememberMe.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", email);
            editor.putString("pass", password);
            editor.apply();
        } else {
            Utils.removeSharedPreferences(prefs);
        }
    }

}