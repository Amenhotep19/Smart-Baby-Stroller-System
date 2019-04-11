package com.example.deepak.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayProfile extends AppCompatActivity {
    private static final String TAG = "DisplayProfile";
    public static final String Firebase_Server_URL = "https://gakusei-go.firebaseio.com/";


    private ImageView profilePic,navImage;
    private TextView profileName, profilePhone, profileEmail;
    private Button profileUpdate,EditInfo;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private String userID;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Toolbar toolbar;
    private TextView Nemail,Nname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        EditInfo=(Button)findViewById(R.id.pbuttonEditInfo);
        profileUpdate=(Button)findViewById(R.id.pbuttonSave);
        profilePic = findViewById(R.id.ivProfilePic);
        profileName = findViewById(R.id.tvProfileName);
        profilePhone = findViewById(R.id.tvProfilePhone);
        profileEmail=findViewById(R.id.tvProfileEmail);
        progressDialog = new ProgressDialog(this);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        System.out.println(userID);
        firebaseStorage = FirebaseStorage.getInstance();
        userID = firebaseAuth.getUid();
        System.out.println(userID);


        toolbar=findViewById(R.id.toolbar);
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();

        // setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        nv = (NavigationView)findViewById(R.id.nv);
        navImage   = (ImageView) nv.getHeaderView(0).findViewById(R.id.pPic);
        Nemail  = (TextView) nv.getHeaderView(0).findViewById(R.id.Nemail);
        Nname  = (TextView) nv.getHeaderView(0).findViewById(R.id.Nname);



        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {

            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {

                    case R.id.Profile:
                        startActivity(new Intent(DisplayProfile.this, DisplayProfile.class));
                        Toast.makeText(DisplayProfile.this, "My Account",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.EditProfile:
                        startActivity(new Intent(DisplayProfile.this, ProfileEditInfo.class));
                        Toast.makeText(DisplayProfile.this, "Edit Profile",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.weather:
                        startActivity(new Intent(DisplayProfile.this, Weather.class));
                        Toast.makeText(DisplayProfile.this, "Weather",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.weather2:
                        startActivity(new Intent(DisplayProfile.this, Weather2.class));
                        Toast.makeText(DisplayProfile.this, "Weather2",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(DisplayProfile.this, MainActivity.class));
                        Toast.makeText(DisplayProfile.this, "Logged Out ",Toast.LENGTH_SHORT).show();
                        finish();
                        return true;

                    default:

                        return true;

                }


            }

        });





        EditInfo.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {


                startActivity(new Intent(DisplayProfile.this, ProfileEditInfo.class));

            }

        });

        profileUpdate.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                startActivity(new Intent(DisplayProfile.this, SecondActivity.class));

            }

        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override

            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // Toast.makeText(getApplicationContext(),"Successfully signed in with: " + user.getEmail());
                    Toast.makeText(getApplicationContext(), "Enter email address!" +user.getEmail(), Toast.LENGTH_SHORT).show();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(), "Successfuly signed out", Toast.LENGTH_SHORT).show();

                }// ...

            }

        };

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("images").child(firebaseAuth.getUid()+ "." + "jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override

            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profilePic);
                Picasso.get().load(uri).fit().centerCrop().into(navImage);

            }

        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mainSnapshot) {

                Users userProfile = mainSnapshot.getValue(Users.class);
                profileName.setText("Name: " + userProfile.getName());
                profilePhone.setText("Phone: " + userProfile.getPhone());
                profileEmail.setText("Email: " + userProfile.getEmail());
                Nemail.setText(userProfile.getEmail());
                Nname.setText(userProfile.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void Logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(DisplayProfile.this, MainActivity.class));
        finish();
    }


    @Override

    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);

    }



    @Override

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);

        }



    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {



        if(t.onOptionsItemSelected(item))

            return true;



        return super.onOptionsItemSelected(item);
    }
}