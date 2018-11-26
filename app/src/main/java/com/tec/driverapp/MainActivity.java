package com.tec.driverapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.tec.entities.Conductor;
import com.tec.entities.Estudiante;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Actividad principal donde se accede al registro y/o al inicio de sesión
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
    static TextView carnet;
    static EditText contraseñalogin;
    static String nombre = null;
    static Conductor conductor;
    Gson gson = new Gson();
    OkHttpClient client = new OkHttpClient();
    String urlInicioSesion = "http://172.18.210.63:8080/ingreso-conductor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carnet = (TextView) findViewById(R.id.carnet);
        contraseñalogin = (EditText) findViewById(R.id.passwordlogin);
        final TextView registrate = (TextView) findViewById(R.id.registrate);
        final ImageView linkedin = (ImageView) findViewById(R.id.linkedimg);
        final RelativeLayout iniciarsesion = (RelativeLayout) findViewById(R.id.iniciarsesion);


        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!carnet.getText().toString().isEmpty()&&!contraseñalogin.getText().toString().isEmpty()){
                    Conductor conductorlog = new Conductor(null, contraseñalogin.getText().toString(),carnet.getText().toString(),0,0);
                    inicioSesion(conductorlog);
                }else {
                    Toast.makeText(getApplicationContext(), "Ingresar todos los datos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rintent = new Intent(MainActivity.this, RegistrationActivity.class);
                MainActivity.this.startActivity(rintent);
            }
        });

        carnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barcode = new Intent(MainActivity.this, BarcodeScanner.class);
                startActivityForResult(barcode, 1);
            }
        });
    }

    public void login(View view) {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                linkedinHelperApi();
                Intent intent = new Intent(MainActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onAuthError(LIAuthError error) {
            }
        }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String resultado = data.getStringExtra("resultado");
                carnet.setText(resultado);
                Toast.makeText(getApplicationContext(), "Su carnet es: " + resultado, Toast.LENGTH_SHORT).show();
                Dialog dialo = alertDialog();
                dialo.show();
            }
        }
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    public void linkedinHelperApi() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                finalResult(apiResponse.getResponseDataAsJson());
            }

            @Override
            public void onApiError(LIApiError LIApiError) {

            }
        });
    }


    public Dialog alertDialog() {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
        mbuilder.setMessage("¿Continuar solo?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent mapintent = new Intent(MainActivity.this, DriverMapActivity.class);
                        MainActivity.this.startActivity(mapintent);
                        DriverMapActivity.vasolo = true;
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent mapintent = new Intent(MainActivity.this, DriverMapActivity.class);
                        MainActivity.this.startActivity(mapintent);
                        DriverMapActivity.vasolo = false;
                    }
                });
        return mbuilder.create();

    }


    public void finalResult(JSONObject jsonObject) {
        try {
            Toast.makeText(getApplicationContext(), "Bienvenido " + jsonObject.get("firstName").toString() + " " + jsonObject.get("lastName").toString(), Toast.LENGTH_SHORT).show();
            nombre = jsonObject.get("firstName").toString() + " " + jsonObject.get("lastName");
            conductor = new Conductor(nombre, null, carnet.getText().toString(), 0, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inicioSesion(Conductor conductor) {
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
                                Toast.makeText(MainActivity.this, "Bienvenido!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, DriverMapActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        });

                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Usuario o contrasena incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        });
    }

}
