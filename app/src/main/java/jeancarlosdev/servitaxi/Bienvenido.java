package jeancarlosdev.servitaxi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jeancarlosdev.servitaxi.Common.Common;
import jeancarlosdev.servitaxi.Modelos.Cliente;
import jeancarlosdev.servitaxi.Utilidades.CustomInfoWindow;

public class Bienvenido extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    //region Atributos

    SupportMapFragment mapFragment;

    private GoogleMap mMap;

    private static final int PERMISSION_REQUEST_CODE = 7777;

    private static final int PLAY_SERVICE_REQUEST_CODE = 7778;

    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    private Location mUltimaUbicacion;

    private static int UPDATE_INTERVAL = 5000;

    private static int FATEST_INTERVAL = 3000;

    private static int DISPLACEMENT = 5000;

    DatabaseReference ref;

    GeoFire geoFire;

    private Marker mUserMarker;

    ImageView imgExpandable;

    BottomSheetRiderFragment mBottomSheet;

    Button btnPickUpRequest;

    boolean conductorEncontrado = false;

    String driver_id = "";

    int radio = 1;

    int distance = 1; // 3km

    private static final int LIMIT = 3;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_bienvenido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Maps
        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Geo fire.

        ref = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);

        geoFire = new GeoFire(ref);

        imgExpandable = (ImageView)findViewById(R.id.imgExpandable);

        mBottomSheet = BottomSheetRiderFragment.newInstance("Rider Bottom sheet");

        imgExpandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheet.show(getSupportFragmentManager()
                        ,mBottomSheet.getTag());

            }
        });

        btnPickUpRequest = findViewById(R.id.btnPickUpRequest);

        btnPickUpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPickUpHere(FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        });

        setUpLocation();
    }

    private void requestPickUpHere(String uid) {

        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.solicitud_tb1);

        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mUltimaUbicacion.getLatitude()
                , mUltimaUbicacion.getLongitude()));

        if(mUserMarker.isVisible()){

            mUserMarker.remove();

        }

        mUserMarker =  mMap.addMarker(new MarkerOptions()
                .title("PickUp Here")
                .snippet("")
                .position(new LatLng(mUltimaUbicacion.getLatitude(),
                        mUltimaUbicacion.getLongitude()))
                .icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mUserMarker.showInfoWindow();

        btnPickUpRequest.setText("Getting Your Driver....");
        
        findDrivers();

    }

    private void findDrivers() {
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Common.solicitud_tb1);

        GeoFire gfDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(mUltimaUbicacion.getLatitude(), mUltimaUbicacion.getLongitude()), radio);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Si lo encuentra

                if (!conductorEncontrado) {
                    conductorEncontrado = true;
                    driver_id = key;
                    btnPickUpRequest.setText("Llamar Taxi");
                    Toast.makeText(Bienvenido.this, "" + key, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Si no encuentra un conductor, aumentar radio
                if(!conductorEncontrado){
                    radio++;
                    findDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){

            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(checkPlayServices()){

                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();

                    }
                }
        }
    }

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

            if(checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
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

                final double latitude = mUltimaUbicacion.getLatitude();
                final double longitud = mUltimaUbicacion.getLongitude();

                if(mUserMarker != null)
                {
                    mUserMarker.remove(); //Remove marker exist.
                }

                mUserMarker = mMap.addMarker(new MarkerOptions().
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.person))
                        .position(new LatLng(latitude, longitud))
                        .title("Usted"));

                //Mover la camara del cellPhone a esa posicion.

                mMap.animateCamera(CameraUpdateFactory.
                        newLatLngZoom(
                                new LatLng(latitude, longitud),
                                17.0f));

                //Dibujar la animation de rotar el carrito.

                rotateMarker(mUserMarker, -360,  mMap);

                loadAllAvailableDrivers();

        }else{

            Log.d("ERROR", "CAN NOT GET YOUR LOCATION");
        }

    }

    /***
     * Cargar todos los conductores en un radio de 3km
     */
    private void loadAllAvailableDrivers() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);
        GeoFire gf = new GeoFire(driverLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mUltimaUbicacion.getLatitude(), mUltimaUbicacion.getLongitude()), distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.conductor_tb1).child(key).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Cliente cliente = dataSnapshot.getValue(Cliente.class);

                                mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                                                .flat(true)
                                                .title(cliente.getNombre())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT){
                    distance++;
                    loadAllAvailableDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bienvenido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
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
}
