package com.example.proyecto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BorrarUsuarioActivity extends AppCompatActivity {

    static String email_actual="";
    static String password_actual="";
    static String emailDeleted="";
    static String passwordDeleted="";
    FirebaseAuth mAuth;
    FirebaseAuth mAuth2;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();



        email_actual=getIntent().getStringExtra("email");
        password_actual=getIntent().getStringExtra("password");

        emailDeleted=getIntent().getStringExtra("email_delete");
        passwordDeleted=getIntent().getStringExtra("password_delete");

        loginOtherUser(emailDeleted,passwordDeleted);

        user=mAuth.getCurrentUser();




        //final String idDeleted= user.getUid();

        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");





        AuthCredential credential = EmailAuthProvider
                .getCredential(emailDeleted, passwordDeleted); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials

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
                                    Intent intent= new Intent(BorrarUsuarioActivity.this, ActivityMain.class);
                                    startActivity(intent);

                                }
                            });
                            //Toast.makeText(BorrarUsuarioActivity.this,"Usuario eliminado",Toast.LENGTH_SHORT).show();
                            //loginOtherUser(email_actual,password_actual);
                            /*Intent intent= new Intent(BorrarUsuarioActivity.this, ActivityMain.class)
                                    .putExtra("emailLogin",email_actual)
                                    .putExtra("passwordLogin",password_actual);*/

                            //startActivity(intent);
                        }else{
                            //Toast.makeText(BorrarUsuarioActivity.this,"No se pudo eliminar",Toast.LENGTH_SHORT).show();

                            Intent intent= new Intent(BorrarUsuarioActivity.this, ActivityMain.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        /*mAuth2=FirebaseAuth.getInstance();
        mAuth2.signInWithEmailAndPassword(email_actual, password_actual)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                            // Sign in success, update UI with the signed-in user's information

                            Intent intent= new Intent(BorrarUsuarioActivity.this, UserActivity.class)
                                    .putExtra("uid",mAuth2.getUid());
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.

                        }
                    }
                });*/

    }

    private void loginOtherUser(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                            // Sign in success, update UI with the signed-in user's information


                        } else {
                            // If sign in fails, display a message to the user.

                        }
                    }
                });
    }

}
