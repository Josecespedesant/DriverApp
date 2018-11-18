package com.tec.driverapp;

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

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {
    TextView carnet;
    EditText nombre;
    EditText pass;
    String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";

    String resultcarnet;
    String resultnombre;
    String resultpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final EditText nombre = (EditText) findViewById(R.id.nombrec);
        final EditText pass = (EditText) findViewById(R.id.contrasregistr);

        final RelativeLayout register = (RelativeLayout) findViewById(R.id.registrarse);

        final TextView volverainiciar = (TextView) findViewById(R.id.volverainiciarsesion);
        final Button ubicar = (Button) findViewById(R.id.location);
        carnet = (TextView) findViewById(R.id.carnet);

        ubicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ubintent = new Intent(RegistrationActivity.this, GetLocationActivity.class);
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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultnombre = nombre.getText().toString();
                resultcarnet = carnet.getText().toString();
                resultpass = pass.getText().toString();

                getManualRegistrationInfo();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String getokn = bundle.getString("valor");
            carnet.setText(getokn);
        }
        linkedinHelperApi();

    }



    /*
     * Devuelve los datos ingresados por el conductor que se va a registrar en un array, devuelve null si hay algún campo sin rellenar.
     */
    public String[] getManualRegistrationInfo(){
        //FALTA INCLUIR UBICACION
        String[] resultado = new String[3];
        if(!resultnombre.matches("")&&!resultpass.matches("")&&!resultcarnet.matches("")){
            resultado[0] = resultnombre;
            resultado[1] = resultpass;
            resultado[2] = resultcarnet;
            return resultado;
        }else{
            Toast.makeText(getApplicationContext(), "Por favor, ingrese correctamente sus datos", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void linkedinHelperApi(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(RegistrationActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                finalResult(apiResponse.getResponseDataAsJson());
            }

            @Override
            public void onApiError(LIApiError LIApiError) {

            }
        });
    }

    public void finalResult(JSONObject jsonObject){
        try{
            TextView nombre = (TextView) findViewById(R.id.nombrec);
            nombre.setText("Nombre:   " + jsonObject.get("firstName").toString()+ " "+ jsonObject.get("lastName").toString()); //aqui se agarra el nombre y se pone en la variable nombre XDDDDDDDD
        }catch (Exception e){
            e.printStackTrace();
        }
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
