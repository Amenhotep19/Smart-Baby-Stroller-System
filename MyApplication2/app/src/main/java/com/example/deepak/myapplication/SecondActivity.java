package com.example.deepak.myapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class SecondActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    String Database_Path = "users";
    String Storage_Path = "profilepics/";
    TextView textView;
    private ImageView imageView;
    EditText editText;
    Uri uriProfileImage;
    String downloadUri;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String imageUrl;
    Button UploadButton;


    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mAuth = FirebaseAuth.getInstance();

        editText = (EditText) findViewById(R.id.editTextDisplayName);
        imageView = (ImageView) findViewById(R.id.imageView);
        UploadButton = (Button)findViewById(R.id.buttonSave);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        progressBar = new ProgressBar(SecondActivity.this);





        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showimagechooser();
            }
        });

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.

                startActivity(new Intent(SecondActivity.this, DisplayProfile.class));

            }
        });
    }


    private void showimagechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile Image"), CHOOSE_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        final String TempImageName = editText.getText().toString().trim();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        String userID=mAuth.getCurrentUser().getUid();
        System.out.println(userID);
        // this is how you set your desired name for the image
        final StorageReference ImagesRef = storageRef.child("images/"+mAuth.getCurrentUser().getUid()+".jpg");
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
                                                    Uri downloadUri = task.getResult();

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
                                                    ref.child("imageURL").setValue(downloadUri.toString());
                                                    ref.child("imageName").setValue(TempImageName);


                                                } else {
                                                    Log.i("wentWrong","downloadUri failure");
                                                }
                                            }
                                        });
                                    }
        });

    }


}

                //     StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + userID + "/" + name + ".jpg");
                //StorageReference profileImageRef =
                //      FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
