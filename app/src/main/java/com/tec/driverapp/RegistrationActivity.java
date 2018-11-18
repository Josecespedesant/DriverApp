package com.tec.driverapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegistrationActivity extends AppCompatActivity {
    TextView carnet;
    TextView nombre;

    String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextView nombre = (TextView) findViewById(R.id.nombre);
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

        Bundle bundle = getIntent().getExtras();
        String getokn = bundle.getString("valor");
        carnet.setText(getokn);

        linkedinHelperApi();

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
            TextView nombre = (TextView) findViewById(R.id.nombre);
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
