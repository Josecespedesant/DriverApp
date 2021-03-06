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
    import android.view.View;
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
    import com.tec.entities.Estudiante;
    import com.tec.graph.DijkstraAlgorithm;
    import com.tec.graph.Edge;
    import com.tec.graph.Graph;
    import com.tec.graph.Vertex;

    import java.io.IOException;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Random;

    /**
     * Mapa desplegado para el conductor
     */
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
        static Boolean vasolo=true;
        Graph g = new Graph(null,null);
        int cont =1;
        static double globalLatitude;
        static double globalLongitude;

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



            g.generateThirtyRandomPlaces();

            List<Vertex> puntos = g.getVertexes();

            for(int i = 0; i < puntos.size()-1;i++){
                LatLng latLng = new LatLng(puntos.get(i).getLat(),puntos.get(i).getLon());
                Marker auxMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(puntos.get(i).getName()));
            }


            final LatLng  posTEC = new LatLng(9.857191, -83.912284);
            final Marker TEC = mMap.addMarker(new MarkerOptions().position(posTEC).title("TEC").icon(BitmapDescriptorFactory.fromResource(R.drawable.tec)));

            mMap.setMyLocationEnabled(false);



            //if(estudiante.isNecesitaViaje() == true) {
                //LatLng locationChofer = new LatLng(estudiante.getPosicionHogar().getLat(), estudiante.getPosicionHogar().getLon()); //cambiar por la ruta no pos HOGAR
                //markerEstudiante = mMap.addMarker(new MarkerOptions().position(locationChofer).title("Ride").icon(BitmapDescriptorFactory.fromResource(R.drawable.student)));
           // }else{
                //Toast.makeText(getApplicationContext(), "No hay ningún estudiante que necesite viaje", Toast.LENGTH_LONG).show();
            //}


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onMapClick(LatLng latLng) {
                    if(ubicacion==null) {
                        ubicacion = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_left)));

                        Double lat = ubicacion.getPosition().latitude;
                        Double lon = ubicacion.getPosition().longitude;

                        Vertex conductr = new Vertex("Node_0", "Node_0", lat, lon);
                        g.getVertexes().add(0, conductr);
                        Edge arista = new Edge("Lane_0", g.getVertexes().get(0), g.getVertexes().get(1), 1);
                        g.getEdges().add(0, arista);

                        if(!g.getVertexes().isEmpty()) {
                            Random rand = new Random();
                            int randomTime = rand.nextInt((10 - 1) + 1) + 1;
                            DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(g);
                            dijkstraAlgorithm.execute(g.getVertexes().get(0));
                            LinkedList<Vertex> path = dijkstraAlgorithm.getPath(g.getVertexes().get(30));

                            LatLng posCond = new LatLng(path.get(0).getLat(),path.get(0).getLon());
                            ubicacion.setPosition(posCond);


                            //ANIMACIÓN DEBERÍA OCURRIR AQUÍ

                            LatLng posDest = new LatLng(path.get(1).getLat(),path.get(1).getLon());
                            animateMarker(ubicacion, posDest, false, randomTime);

                        }




                        //Verificar
                        //LatLng posEstu = new LatLng(estudiante.getPosLatitud(), estudiante.getPosLongitud());
                        //if(estudiante.isNecesitaViaje()){
                            //animateMarker(ubicacion, posEstu, false);
                            //markerEstudiante.remove();
                        //}else{
                        //    animateMarker(ubicacion, posTEC, false);
                        //}




                    }else{
                        //ANIMACIÓN DEBERÍA CONTINUAR OCURRIR AQUÍ
                        if(!g.getVertexes().isEmpty()) {
                            Random rand = new Random();
                            int randomTime = rand.nextInt((10 - 1) + 1) + 1;
                            DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(g);
                            dijkstraAlgorithm.execute(g.getVertexes().get(0));
                            LinkedList<Vertex> path = dijkstraAlgorithm.getPath(g.getVertexes().get(30));

                            LatLng posCond = new LatLng(path.get(cont).getLat(), path.get(cont).getLon());
                            ubicacion.setPosition(posCond);

                            for (int j = cont+1; j < path.size(); j++) {
                                LatLng posDest = new LatLng(path.get(j).getLat(), path.get(j).getLon());
                                animateMarker(ubicacion, posDest, false, randomTime);
                                cont++;
                                return;
                            }
                        }
                    }
                }
            });
        }


        public void animateMarker(final Marker marker, final LatLng toPosition,
                                  final boolean hideMarker, int dist) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);

            int distance = dist * 10000;
            final int speed = 10;
            final long duration = distance/speed;
            ubicacion.setTitle(duration/1000+" Segundos");

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