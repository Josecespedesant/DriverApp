package com.tec.driverapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegistrationActivity extends AppCompatActivity {
    TextView carnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final TextView volverainiciar = (TextView) findViewById(R.id.volverainiciarsesion);
        final Button ubicar = (Button) findViewById(R.id.location);
        carnet = (TextView) findViewById(R.id.carnet);

        ubicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ubintent = new Intent(RegistrationActivity.this, MapActivity.class);
                startActivityForResult(ubintent, 2);
                //startActivityForResult(ubicintent, 1);
            }
        });

        carnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(RegistrationActivity.this, BarcodeScanner.class);
                startActivityForResult(registerIntent, 1);

                //RegistrationActivity.this.startActivity(registerIntent);
            }
        });
        volverainiciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                RegistrationActivity.this.startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String resultado = data.getStringExtra("resultado");
                carnet.setText(resultado);
            }
        }
    }
}
