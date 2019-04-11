package com.example.deepak.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NavigationRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    //  private TextView Info;
    private Button Login,btnReset,btnPhoneAuth;
    private ProgressDialog progressDialog;
    private TextView userRegisteration;
    private int counter = 5;
    private TextView Info;
    private FirebaseAuth auth;
    private TextView forgotPassword;

    @Override




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();
        forgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        Email = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
         Info = (TextView) findViewById(R.id.tvInfo);
        Login = (Button) findViewById(R.id.btnLogin);
        userRegisteration=(TextView)findViewById(R.id.tvRegister);
        btnPhoneAuth=(Button) findViewById(R.id.btn_phone_auth);
        progressDialog = new ProgressDialog(this);
        Info.setText("No of attempts remaining: 5");
        FirebaseUser user = auth.getCurrentUser();



        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, DisplayProfile.class));
            finish();
        }

            userRegisteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();

                startActivity(new Intent(MainActivity.this, ResetPasswordActivity.class));
                progressDialog.dismiss();
            }

        });

        btnPhoneAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,Naviagtion.class));
            }
        });



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait!");
                progressDialog.show();
              final String email=Email.getText().toString();
                final String pass=Password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(!task.isSuccessful()){
                                    counter--;
                                    Info.setText("No of attempts remaining: " + counter);
                                    progressDialog.dismiss();
                                    if(counter == 0){
                                        Login.setEnabled(false);
                                    }

                                    if (pass.length() < 6) {
                                        Password.setError(getString(R.string.minimum_password));
                                    } else {

                                        Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    progressDialog.dismiss();
                                    checkEmailVerification();
                                    startActivity(new Intent(MainActivity.this, DisplayProfile.class));

                                }
                            }
                        });
            }
        });
    }

    private void checkEmailVerification() {

        FirebaseUser firebaseUser = auth.getInstance().getCurrentUser();

        Boolean emailflag = firebaseUser.isEmailVerified();



        startActivity(new Intent(MainActivity.this, SecondActivity.class));



        if(emailflag){

            finish();

            startActivity(new Intent(MainActivity.this, SecondActivity.class));

        }else{

            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();

            auth.signOut();

        }


    }

}