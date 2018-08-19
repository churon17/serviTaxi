package jeancarlosdev.servitaxi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.github.glomadrian.materialanimatedswitch.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class Bienvenido_3 extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{

    //region Atributos
    private GoogleMap mMap;

    private static final int PERMISSION_REQUEST_CODE = 7777;

    private static final int PLAY_SERVICE_REQUEST_CODE = 7778;

    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    private Location mUltimaUbicacion;

    private static int UPDATE_INTERVAL = 5000;

    private static int FATEST_INTERVAL = 3000;

    private static int DISPLACEMENT = 5000;

    private Marker mCurrent;

    private MaterialAnimatedSwitch location_switch;

    private SupportMapFragment mapFragment;

    private String nombre;

    private String email;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.frm_bienvenido_3);

        nombre = getIntent().getStringExtra("nombre");

        email = getIntent().getStringExtra("email");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btn_user = findViewById(R.id.btn_find_user);


        btn_user.setText(
                AccessToken.getCurrentAccessToken() == null? "No Logueado": nombre);

        location_switch = findViewById(R.id.switch_location);

        location_switch.setOnCheckedChangeListener(
                new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isOnline) {

                        if(isOnline){

                            startLocationUpdates();
                            displayLocation();
                            Snackbar.make(mapFragment.getView(), "En linea", Snackbar.
                                    LENGTH_SHORT).show();

                        }else{

                            stopLocationUpdates();

                            mCurrent.remove();
                            Snackbar.make(mapFragment.getView(),
                                    "Fuera de Linea", Snackbar.
                                            LENGTH_SHORT).show();

                        }

                    }
                });
        setUpLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(checkPlayServices()){

                        buildGoogleApiClient();
                        createLocationRequest();
                        if(location_switch.isChecked()){
                            displayLocation();
                        }
                    }
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();



    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mUltimaUbicacion = location;
        displayLocation();
    }

    //region Metodos Propios
    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_CODE);
        }else {

            if(checkPlayServices()){

                buildGoogleApiClient();
                createLocationRequest();
                if(location_switch.isChecked()){
                    displayLocation();
                }
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode!= ConnectionResult.SUCCESS){

            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                        this,
                        PLAY_SERVICE_REQUEST_CODE).show();
            }else{
                Toast.makeText(this, "this device is not supported",
                        Toast.LENGTH_SHORT).show();

                finish();

            }

            return false;

        }

        return  true;
    }

    private void stopLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED)
        {

            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                this);

    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED)
        {

            return;
        }

        mUltimaUbicacion = LocationServices.
                FusedLocationApi.
                getLastLocation(mGoogleApiClient);

        if(mUltimaUbicacion != null){
            if(location_switch.isChecked()){
                final double latitude = mUltimaUbicacion.getLatitude();
                final double longitud = mUltimaUbicacion.getLongitude();


                if(mCurrent != null)
                {
                    mCurrent.remove(); //Remove marker exist.
                }

                mCurrent = mMap.addMarker(new MarkerOptions().
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.person))
                        .position(new LatLng(latitude, longitud))
                        .title("Usted"));

                //Mover la camara del cellPhone a esa posicion.

                mMap.animateCamera(CameraUpdateFactory.
                        newLatLngZoom(
                                new LatLng(latitude,                                                                     longitud),
                                17.0f));

                //Dibujar la animation de rotar el carrito.

                rotateMarker(mCurrent, -360,  mMap);
            }


        }else{

            Log.d("ERROR", "CAN NOT GET YOUR LOCATION");
        }

    }

    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();

        final float startRotation = mCurrent.getRotation();

        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;

                float t = interpolator.getInterpolation((float)elapsed/duration);

                float rot = t*i+(1-t)*startRotation;

                mCurrent.setRotation( -rot>360? rot/1 : rot);

                if(t<1.0){

                    handler.postDelayed(this,16 );

                }

            }
        });


    }

    private void startLocationUpdates() {

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED)
        {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest,this
        );



    }


    //endregion

}

