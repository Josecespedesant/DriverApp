    package com.tec.driverapp;

    import android.Manifest;
    import android.content.pm.PackageManager;
    import android.graphics.Point;
    import android.location.Location;
    import android.os.Build;
    import android.os.Handler;
    import android.os.SystemClock;
    import android.support.annotation.NonNull;
    import android.support.annotation.Nullable;
    import android.support.annotation.RequiresApi;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.app.FragmentActivity;
    import android.os.Bundle;
    import android.view.animation.Interpolator;
    import android.view.animation.LinearInterpolator;
    import android.widget.Toast;
    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.location.LocationRequest;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.Projection;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.tec.comm.NuevoConductor;
    import com.tec.entities.Estudiante;

    import java.io.IOException;


    public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
        private GoogleMap mMap;
        GoogleApiClient googleApiClient;
        Location lastLocation;
        LocationRequest locationRequest;
        SupportMapFragment mapFragment;
        static Marker carmarker;
        Marker ubicacion;
        Estudiante estudiante;
        Marker markerEstudiante;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_driver_map);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DriverMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }else{
                mapFragment.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);

            final LatLng  lalg = new LatLng(9.857191, -83.912284);
            Marker TEC = mMap.addMarker(new MarkerOptions().position(lalg).title("TEC").icon(BitmapDescriptorFactory.fromResource(R.drawable.tec)));

            mMap.setMyLocationEnabled(false);
           // LatLng locationChofer = new LatLng(estudiante.getPosicionHogar().getLat(), estudiante.getPosicionHogar().getLon()); //cambiar por la ruta no pos HOGAR
           // markerEstudiante = mMap.addMarker(new MarkerOptions().position(locationChofer).title("Ride").icon(BitmapDescriptorFactory.fromResource(R.drawable.student)));


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onMapClick(LatLng latLng) {
                    if(ubicacion==null) {
                        ubicacion = mMap.addMarker(new MarkerOptions().position(latLng).title("carro").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_left)));

                        Double lat = ubicacion.getPosition().latitude;
                        Double lon = ubicacion.getPosition().longitude;

                        if(RegistrationActivity.nuevoconductor != null){
                            RegistrationActivity.nuevoconductor.setPosicionHogar(new Posicion(lat, lon));

                            NuevoConductor test = new NuevoConductor();
                            try {
                                test.registrar(RegistrationActivity.nuevoconductor);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(MainActivity.conductor!= null){
                            MainActivity.conductor.setPosicionHogar(new Posicion(lat, lon));

                            NuevoConductor test = new NuevoConductor();
                            try {
                                test.registrar(RegistrationActivity.nuevoconductor);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //Verificar
                        animateMarker(ubicacion, lalg, false);
                    }
                }
            });
        }

        public void animateMarker(final Marker marker, final LatLng toPosition,
                                  final boolean hideMarker) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 20000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 30);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                }
            });
        }

        protected synchronized void buildGoogleApiClient(){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            lastLocation = location;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            if(carmarker!=null){
                carmarker.remove();
            }

         //   carmarker = mMap.addMarker(new MarkerOptions().position(latLng).title("carro").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_left)));

        }


        @Override
        public void onConnected(@Nullable Bundle bundle) {
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DriverMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        final int LOCATION_REQUEST_CODE = 1;
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                case LOCATION_REQUEST_CODE:{
                    if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        mapFragment.getMapAsync(this);
                    }else{
                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        }
    }