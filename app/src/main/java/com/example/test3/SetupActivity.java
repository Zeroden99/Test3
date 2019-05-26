package com.example.test3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SetupActivity extends AppCompatActivity
{
    private EditText UserName, FullName, Years, PhoneNumber;
    private Button SaveInformationbutton;
    private ImageView ProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_full_name);
        Years = (EditText) findViewById(R.id.setup_years);
        PhoneNumber = (EditText) findViewById(R.id.setup_phone_number);
        SaveInformationbutton = (Button) findViewById(R.id.setup_information_button);
        ProfileImage = (ImageView) findViewById(R.id.setup_profile_image);

    }
}
