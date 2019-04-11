package com.example.deepak.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Naviagtion extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Toolbar toolbar;
    private Uri photoUrl;
    private ImageView mPic;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naviagtion);
        toolbar=findViewById(R.id.toolbar);
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        mPic    = (ImageView) nv.getHeaderView(0).findViewById(R.id.pPic);
        getCurrentinfo();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();






    }

    private void getCurrentinfo() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

                StorageReference storageReference = firebaseStorage.getReference();
                storageReference.child("images").child(firebaseAuth.getUid()+ "." + "jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override

                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(mPic);


                    }
            });
        }


    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {



        if(t.onOptionsItemSelected(item))

            return true;



        return super.onOptionsItemSelected(item);
    }
}
