package com.example.deepak.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText UName, UPhone, UEmail;
    private EditText UPassword;
    private TextView ULogin;
    private Button Reg;
    private FirebaseAuth auth;
    DatabaseReference dbuser;
    private ProgressDialog progressDialog;
    // private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIviews();
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        dbuser = FirebaseDatabase.getInstance().getReference("users");

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering Please wait!");
                progressDialog.show();
                addUser ();
            }


        });

        ULogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));

            }
        });
    }

    private void addUser() {

        final String u_email = UEmail.getText().toString().trim();
        final String u_pass = UPassword.getText().toString().trim();
        final String u_name = UName.getText().toString().trim();
        final String u_phone = UPhone.getText().toString().trim();


        if (TextUtils.isEmpty(u_name)) {
            Toast.makeText(getApplicationContext(), "Enter name ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(u_email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(u_phone)) {
            Toast.makeText(getApplicationContext(), "Enter phone", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(u_pass)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (u_pass.length() < 6) {

            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }



        auth.createUserWithEmailAndPassword(u_email, u_pass)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final FirebaseUser users = auth.getCurrentUser();
                        if (users.isEmailVerified()) {
                            Toast.makeText(RegistrationActivity.this,"You are in =)",Toast.LENGTH_LONG).show();
                        }

                        else {
                            users.sendEmailVerification();
                            Toast.makeText(RegistrationActivity.this,"Check your email first...",Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(RegistrationActivity.this, "registeration suceessful:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            progressDialog.dismiss();

                            String id = auth.getCurrentUser().getUid();
                            Users user = new Users(id, u_name, u_email, u_pass, u_phone);
                            dbuser.child(id).setValue(user);

                            Toast.makeText(RegistrationActivity.this, "user added", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();

                        }
                    }
                });

    }


    private void setupUIviews() {
        UName = (EditText) findViewById(R.id.etUName);
        UEmail = (EditText) findViewById(R.id.etUEmail);
        UPassword = (EditText) findViewById(R.id.etUPassword);
        UPhone = (EditText) findViewById(R.id.etUphone);
        ULogin = (TextView) findViewById(R.id.tvULogin);
        Reg = (Button) findViewById(R.id.btnRegister);

    }

}
