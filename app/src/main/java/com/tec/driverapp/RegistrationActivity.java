package com.tec.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.tec.entities.Conductor;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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


    Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();
    String urlInicioSesion = "http://172.18.210.63:8080/registro-conductor";


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
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    registro(nuevoconductor);
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

    public void registro(Conductor conductor) {
        String json = gson.toJson(conductor);
        final JsonParser jsonParser = new JsonParser();
        final JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        jsonObject.remove("amigos");
        json = jsonObject.toString();

        RequestBody requestBody = new FormBody.Builder()
                .add("json", json)
                .build();

        Request request = new Request.Builder()
                .url(this.urlInicioSesion)
                .addHeader("Content-Type", "text/plain")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    JsonObject json = jsonParser.parse(myResponse).getAsJsonObject();
                    boolean exito = json.getAsJsonPrimitive("exitoso").getAsBoolean();

                    if (exito) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistrationActivity.this, "Bienvenido!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity.this,
                                        DriverMapActivity.class);
                                RegistrationActivity.this.startActivity(intent);
                            }
                        });

                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistrationActivity.this,
                                        "Información inválida o usuario ya existe", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        });
    }
}
