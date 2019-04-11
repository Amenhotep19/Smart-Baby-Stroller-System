package com.example.deepak.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileEditInfo extends AppCompatActivity {
    private EditText newUserName, newUserEmail, newUserPhone;
    private Button save;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView updateProfilePic,navImage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;
    ProgressBar progressBar;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Toolbar toolbar;
    private TextView Nemail,Nname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_info);
        updateProfilePic = findViewById(R.id.ivProfileUpdate);
        newUserName = findViewById(R.id.etNameUpdate);
        newUserEmail = findViewById(R.id.etEmailUpdate);


     //
       //

        newUserPhone = findViewById(R.id.etPhoneUpdate);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        save = findViewById(R.id.btnSave);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userID = firebaseAuth.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

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
                        startActivity(new Intent(ProfileEditInfo.this, DisplayProfile.class));
                        Toast.makeText(ProfileEditInfo.this, "My Account",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.EditProfile:
                        startActivity(new Intent(ProfileEditInfo.this, ProfileEditInfo.class));
                        Toast.makeText(ProfileEditInfo.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(ProfileEditInfo.this, MainActivity.class));
                        Toast.makeText(ProfileEditInfo.this, "My Cart",Toast.LENGTH_SHORT).show();
                        finish();
                        return true;

                    default:

                        return true;

                }


            }

        });
















        final StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("images").child(firebaseAuth.getUid() + "." + "jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(updateProfilePic);
                Picasso.get().load(uri).fit().centerCrop().into(navImage);

            }
        });

        // final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users userProfile = dataSnapshot.getValue(Users.class);

                newUserName.setText(userProfile.getName());
                newUserPhone.setText(userProfile.getPhone());
                newUserEmail.setText(userProfile.getEmail());
               Nemail.setText(userProfile.getEmail());
                    Nname.setText(userProfile.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileEditInfo.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }

        });



        save.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View view)
            {
                progressDialog.setMessage("Saving Data");
                progressDialog.show();

                String id = firebaseAuth.getCurrentUser().getUid();
                String name = newUserName.getText().toString();
                String phone = newUserPhone.getText().toString();
                String email = newUserEmail.getText().toString();

                databaseReference.child(id).child("name").setValue(name);
                databaseReference.child(id).child("email").setValue(email);
                databaseReference.child(id).child("phone").setValue(phone);

                progressDialog.dismiss();

                Toast.makeText(ProfileEditInfo.this, "user updated", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfileEditInfo.this, DisplayProfile.class));
                finish();


            }
        });

        updateProfilePic.setOnClickListener(new View.OnClickListener()
        {

            @Override

            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

            }

        });


    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProfilePic.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {

        progressDialog.setMessage("Uploading Image");
        progressDialog.show();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        String userID=firebaseAuth.getCurrentUser().getUid();
        System.out.println(userID);
        // this is how you set your desired name for the image
        final StorageReference ImagesRef = storageRef.child("images/"+firebaseAuth.getCurrentUser().getUid()+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ImagesRef.putBytes(data);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("whatTheFuck:",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }

                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Uri downloadUri = task.getResult();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
                            ref.child("imageURL").setValue(downloadUri.toString());



                        } else {
                            Log.i("wentWrong","downloadUri failure");
                        }
                    }
                });
            }
        });

    }




    @Override

    public boolean onOptionsItemSelected(MenuItem item) {



        if(t.onOptionsItemSelected(item))

            return true;



        return super.onOptionsItemSelected(item);
    }
}
