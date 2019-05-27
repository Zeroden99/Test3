package com.example.test3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity
{
    private EditText UserName, FullName, Years, PhoneNumber;
    private DatabaseReference userRef;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;
    String  currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");
        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_full_name);
        Years = (EditText) findViewById(R.id.setup_years);
        PhoneNumber = (EditText) findViewById(R.id.setup_phone_number);
        Button saveInformationbutton = (Button) findViewById(R.id.setup_information_button);
        ImageView profileImage = (ImageView) findViewById(R.id.setup_profile_image);

        saveInformationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountInformation();
            }
        });

    }


    private void SaveAccountInformation()
    {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String years = Years.getText().toString();
        String phonenumber = PhoneNumber.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Write your username", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Write your fullname", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(years))
        {
            Toast.makeText(this, "Write your years old", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phonenumber))
        {
            Toast.makeText(this, "Write your phone number", Toast.LENGTH_SHORT).show();
        }
        else

            {
                loadingBar.setTitle("Saving information");
                loadingBar.setMessage("Please wait, while we are creating your new Account");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                HashMap userMap = new HashMap();
                userMap.put("Username", username);
                userMap.put("Fullname", fullname);
                userMap.put("Years", years);
                userMap.put("PhoneNumber", phonenumber);
                userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                     if (task.isSuccessful( ))
                     {
                         SendUserToMainActivity();
                         Toast.makeText(SetupActivity.this, "Your Account is created Sucesfully", Toast.LENGTH_LONG).show();
                         loadingBar.dismiss();
                     }
                     else
                     {
                        String message=task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Erorr Ocured: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                     }

                    }
                });

            }

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
