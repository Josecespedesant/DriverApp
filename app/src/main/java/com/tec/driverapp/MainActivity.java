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

        import com.linkedin.platform.LISessionManager;
        import com.linkedin.platform.errors.LIAuthError;
        import com.linkedin.platform.listeners.AuthListener;
        import com.linkedin.platform.utils.Scope;

        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;


        public class MainActivity extends AppCompatActivity {
            private static final String TAG = "MainActivity";

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

                final TextView carnet = (TextView) findViewById(R.id.carnet);
                final EditText contraseñalogin = (EditText) findViewById(R.id.passwordlogin);
                final TextView registrate = (TextView) findViewById(R.id.registratetext);
                final ImageView linkedin = (ImageView) findViewById(R.id.linkedimg);
                final RelativeLayout iniciarsesion = (RelativeLayout) findViewById(R.id.iniciarsesion);



                linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login(v);
                    }
                });

                registrate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                        MainActivity.this.startActivity(registerIntent);
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
                        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                        intent.putExtra("valor", LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
                        startActivity(intent);
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
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            }
        }
