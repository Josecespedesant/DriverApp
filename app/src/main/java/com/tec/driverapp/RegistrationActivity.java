package com.tec.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.tec.comm.NuevoConductor;
import com.tec.entities.Conductor;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase para registrarse en el sistema
 */
public class RegistrationActivity extends AppCompatActivity {
    static TextView carnet;
    EditText nombre;
    static EditText pass;

    String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";

    String resultcarnet = "";
    static String resultnombre = "";
    static String resultpass = "";
    boolean flag = false;
    RelativeLayout register;

    static Conductor nuevoconductor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final EditText nombre = (EditText) findViewById(R.id.nombrec);
        final EditText pass = (EditText) findViewById(R.id.contrasregistr);
        register = (RelativeLayout) findViewById(R.id.registrarse);
        final TextView inic = (TextView) findViewById(R.id.volveriniciar);

        carnet = (TextView) findViewById(R.id.carnet);

        inic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent volver = new Intent(RegistrationActivity.this, MainActivity.class);
                RegistrationActivity.this.startActivity(volver);
                finish();
            }
        });

        carnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(RegistrationActivity.this, BarcodeScanner.class);
                startActivityForResult(registerIntent, 1);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!carnet.getText().toString().isEmpty()&&!nombre.getText().toString().isEmpty()&&!pass.getText().toString().isEmpty()) {
                    resultnombre = nombre.getText().toString();
                    resultpass = pass.getText().toString();
                    resultcarnet = carnet.getText().toString();
                    //nuevoconductor = getManualRegistrationInfo();
                    MainActivity.carnet.setText(carnet.getText().toString());
                    MainActivity.contraseñalogin.setText(pass.getText().toString());
                    //getManualRegistrationInfo();
                    nuevoconductor = new Conductor(resultnombre, resultpass, resultcarnet, 0, 0);
                    NuevoConductor condARegistrar = new NuevoConductor();
                    try {
                        condARegistrar.registrar(nuevoconductor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Ingresar todos los datos",Toast.LENGTH_SHORT).show();

                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String getokn = bundle.getString("valor");
            carnet.setText(getokn);
        }
    }


    /*
     * Devuelve los datos ingresados por el conductor que se va a registrar en un array, devuelve null si hay algún campo sin rellenar.
     */


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
