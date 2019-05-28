package com.example.test3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private ImageButton SelectPostImage;
    private ProgressDialog loadingBar;
    private Button CreatePostButton;
    private EditText  PostDescription, PostName;
    private static final int  Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description, PName;
    private StorageReference PostsImageRefrence;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUri, current_user_id;
    private DatabaseReference userRef, postsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        loadingBar= new ProgressDialog(this);
        PostsImageRefrence= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        CreatePostButton = (Button) findViewById(R.id.create_post_button);
        PostDescription = (EditText) findViewById(R.id.post_descrition);
        PostName = (EditText) findViewById(R.id.post_name);

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              OpenGalery();
            }
        });

        CreatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatesPostInfo();
            }
        });

    }

    private void ValidatesPostInfo()
    {
        String PName = PostName.getText().toString();
        String Description = PostDescription.getText().toString();
        if (ImageUri == null)
        {
            Toast.makeText(this,"Please select photo", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Please write description", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(PName))
        {
            Toast.makeText(this,"Please write Post Name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are creating your new Post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToStorage();

        }


    }

    private void StoringImageToStorage()
    {
        Calendar calForData = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mmmm-yyyy");
        saveCurrentDate = currentDate.format(calForData.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForData.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImageRefrence.child("Post Images").child(ImageUri.getLastPathSegment()+ postRandomName+".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
             if (task.isSuccessful())
             {

                 downloadUri=task.getResult().getStorage().getDownloadUrl().toString();

                 Toast.makeText(PostActivity.this,"Sucessfullu image upload to Storage", Toast.LENGTH_SHORT).show();
                 SavingPostInformation();
             }
             else
                 {
                     String message = task.getException().getMessage();
                     Toast.makeText(PostActivity.this,"Error ocured "+message, Toast.LENGTH_SHORT).show();

                 }
            }
        });
    }

    private void SavingPostInformation()
    {
        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();


                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("descrition", Description);
                    postsMap.put("postname", PostName);
                    postsMap.put("postimage", downloadUri);
                    postsMap.put("plofileimage", userProfileImage);
                    postsMap.put("fullnane", userFullName);
                    postsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful())
                                { SendUserToMainActivity();
                                 Toast.makeText(PostActivity.this,"Succesfully", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                                }
                                else
                                    {

                                        Toast.makeText(PostActivity.this,"Erorr ocured ", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
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

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void OpenGalery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data !=null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }
}
