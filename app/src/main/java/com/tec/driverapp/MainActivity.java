package com.tec.driverapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText nombreusuariologin = (EditText) findViewById(R.id.nombreusuariologin);
        final EditText contraseñalogin = (EditText) findViewById(R.id.passwordlogin);
        final TextView registrate = (TextView) findViewById(R.id.registratetext);
        final ImageView linkedin = (ImageView) findViewById(R.id.linkedimg);
        final RelativeLayout iniciarsesion = (RelativeLayout) findViewById(R.id.iniciarsesion);


        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapdisplay = new Intent(MainActivity.this, DriverMapActivity.class);
                MainActivity.this.startActivity(mapdisplay);
            }
        });

        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                MainActivity.this.startActivity(registerIntent);
            }


        });
    }

}
