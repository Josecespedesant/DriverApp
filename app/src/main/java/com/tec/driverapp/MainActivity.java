        package com.tec.driverapp;

        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.Signature;
        import android.support.annotation.Nullable;
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

        import com.linkedin.platform.APIHelper;
        import com.linkedin.platform.LISessionManager;
        import com.linkedin.platform.errors.LIApiError;
        import com.linkedin.platform.errors.LIAuthError;
        import com.linkedin.platform.listeners.ApiListener;
        import com.linkedin.platform.listeners.ApiResponse;
        import com.linkedin.platform.listeners.AuthListener;
        import com.linkedin.platform.utils.Scope;

        import org.json.JSONObject;

        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;


        public class MainActivity extends AppCompatActivity {
            private static final String TAG = "MainActivity";
            String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
            static TextView carnet;
            static EditText contraseñalogin;
            static String nombre=null;

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

                       // if(carnet.getText().toString().equals(RegistrationActivity.carnet.getText().toString()) && contraseñalogin.getText().toString().equals(RegistrationActivity.resultpass)){
                      //      Toast.makeText(getApplicationContext(),"Bienvenido: " + nombre,Toast.LENGTH_SHORT).show();

                        Intent mapintent = new Intent(MainActivity.this, DriverMapActivity.class);
                        MainActivity.this.startActivity(mapintent);
                     //   }else{
                            Toast.makeText(getApplicationContext(),"Carnet o contraseña invalida",Toast.LENGTH_SHORT).show();
                    //    }

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
                        MainActivity.this.startActivity(barcode);
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
                if(requestCode == 1){
                    if(resultCode == RESULT_OK){
                        String resultado = data.getStringExtra("resultado");
                        carnet.setText(resultado);
                        Toast.makeText(getApplicationContext(),"Su carnet es: "+resultado,Toast.LENGTH_SHORT).show();

                        Intent mapintent = new Intent(MainActivity.this, DriverMapActivity.class);
                        MainActivity.this.startActivity(mapintent);
                    }
                }
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            }

            public void linkedinHelperApi(){
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

            public void finalResult(JSONObject jsonObject){
                try{
                    Toast.makeText(getApplicationContext(),"Bienvenido "+ jsonObject.get("firstName").toString()+ " "+ jsonObject.get("lastName").toString(),Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
