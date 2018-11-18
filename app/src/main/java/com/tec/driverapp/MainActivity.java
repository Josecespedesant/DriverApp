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
            static TextView carnet;
            static TextView registrate;

            String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                try {

                    @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                            "com.tec.driverapp",//give your package name here
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());

                        Log.d(TAG, "Hash ASLKJDNKJHNLASDOHASHIPDASDPOHIJASDPHO : " + Base64.encodeToString(md.digest(), Base64.NO_WRAP));//Key hash is printing in Log
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d(TAG, e.getMessage(), e);
                } catch (NoSuchAlgorithmException e) {
                    Log.d(TAG, e.getMessage(), e);
                }

                carnet = (TextView) findViewById(R.id.carnet);
                final EditText contraseñalogin = (EditText) findViewById(R.id.passwordlogin);
                registrate = (TextView) findViewById(R.id.registrate);
                final ImageView linkedin = (ImageView) findViewById(R.id.linkedimg);
                final RelativeLayout iniciarsesion = (RelativeLayout) findViewById(R.id.iniciarsesion);

                iniciarsesion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mapintent = new Intent(MainActivity.this, DriverMapActivity.class);
                        MainActivity.this.startActivity(mapintent);
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
                    public void onClick(View view) {
                        Intent registration = new Intent(MainActivity.this, RegistrationActivity.class);
                        startActivity(registration);
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
                      // intent.putExtra("valor", LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
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
                        final TextView iniciosesion = (TextView) findViewById(R.id.iniciosesion);
                        iniciosesion.setText(resultado);
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
                    //extView nombre = (TextView) findViewById(R.id.nombre);
                    carnet.setText("Nombre:   " + jsonObject.get("firstName").toString()+ " "+ jsonObject.get("lastName").toString()); //aqui se agarra el nombre y se pone en la variable nombre XDDDDDDDD
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

           /* @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == 1){
                    if(resultCode == RESULT_OK){
                        String resultado = data.getStringExtra("resultado");
                        carnet.setText(resultado);
                    }
                }
            }*/
        }
