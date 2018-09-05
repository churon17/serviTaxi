package jeancarlosdev.servitaxi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import jeancarlosdev.servitaxi.Common.Common;
import jeancarlosdev.servitaxi.Modelos.Cliente;
import jeancarlosdev.servitaxi.Modelos.FCMResponse;
import jeancarlosdev.servitaxi.Modelos.Favorito;
import jeancarlosdev.servitaxi.Modelos.MensajeBackJson;
import jeancarlosdev.servitaxi.Modelos.Notification;
import jeancarlosdev.servitaxi.Modelos.Sender;
import jeancarlosdev.servitaxi.Modelos.Token;
import jeancarlosdev.servitaxi.Remote.Conexion;
import jeancarlosdev.servitaxi.Remote.IFCMService;
import jeancarlosdev.servitaxi.Remote.VolleyPeticion;
import jeancarlosdev.servitaxi.Remote.VolleyProcesadorResultado;
import jeancarlosdev.servitaxi.Remote.VolleyTiposError;
import jeancarlosdev.servitaxi.Utilidades.CustomInfoWindow;
import jeancarlosdev.servitaxi.Utilidades.TransformarImagen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/***
 * Clase utilizada para implementar y hacer uso el Mapa de GoogleMaps.
 * En esta clase también se implementa Places, para buscar ubicaciones en la Api de Places de GoogleMaps.
 * Esta clase tambien es la contenedera para acceder a distintas actividades como cerrar sesión, cambiar contraseña.
 * Implementa Cuatro interfaces necesarias para manipular en tiempo real la ubicación del usuario.
 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
 * @see com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
 * @see com.google.android.gms.location.LocationListener
 * @see NavigationView.OnNavigationItemSelectedListener
 */
public class Bienvenido extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //region Atributos

    /***
     * El Fragment utilizado para nuestro Mapa.
     */
    SupportMapFragment mapFragment;

    /***
     * Para mostrar el mapa con el que vamos a trabajar.
     */
    private GoogleMap mMap;

    /***
     * Para ver si el usuario dio los permisos necesarios de Localización.
     */
    private static final int PERMISSION_REQUEST_CODE = 7777;

    /***
     * Para ver si el usuario dio los permisos necesarios de Localización.
     */
    private static final int PLAY_SERVICE_REQUEST_CODE = 7778;

    /***
     * LocationRequest se utiliza para solicitar una calidad de servicio para las actualizaciones de ubicación desde FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /***
     * Trabaja conjuntamente con LocationRequest, para las actualizaciones de ubicación.
     */
    private GoogleApiClient mGoogleApiClient;

    /***
     * Variable de tipo LOCATION utilizada para mantener constantemente el valor de latitud y longitud del conductor.
     */
    private Location mUltimaUbicacion;

    /***
     * Para cambiar el intervalo de actualización de LocationRequest para la ubicación en tiempo real.
     */
    private static int UPDATE_INTERVAL = 5000;

    /***
     * Para cambiar el intervalo de actualización de LocationRequest para la ubicación en tiempo real.
     */
    private static int FATEST_INTERVAL = 3000;

    /***
     * Para cambiar el intervalo de actualización de LocationRequest para la ubicación en tiempo real.
     */
    private static int DISPLACEMENT = 5000;

    /***
     * Referencia a la base de datos de Firebase.
     */
    DatabaseReference ref;

    /***
     * Para consultas de ubicacion en tiempo real con Firebase.
     */
    GeoFire geoFire;

    /***
     * Para agregar o quitar el marcador cuando deseemos dentro de nuestro mapa.
     */
    private Marker mUserMarker;

    /***
     * Variable utiliza para agregar posteriormente la lista de Favoritos.
     */
    private Favorito[] favoritosLista;

    /**
     * Boton que se utilizará para solicitar Taxi.
     */
    Button btnSolicitarTaxi;

    /**
     * Para verificar si se ha encontrado un conductor.
     */
    boolean conductorEncontrado = false;

    boolean conductorEncontradoFav = false;

    double latFav = 0;

    double lonFav = 0;

    String driver_idGlob = "";

    int radio = 1;

    int distance = 1; // 3km

    private static final int LIMIT = 3;

    /***
     * Para crear un objeto de tipo IFCMService para peticiones en Firebase Cloud Messaging.
     */
    IFCMService mService;

    //Presense System

    /**
     * Referencia de la base de datos de Firebase.
     */
    DatabaseReference driverAvailable;

    //Load Data From Last Intent

    String url, nombre, email;

    /***
     * HashMap utilizado para constantemente enviar y recibir información del servidor.
     */
    HashMap<String, String> mapa;

    /***
     * Variable de Tipo RequestQueue para ejecutar nuestras peticiones con Volley.
     */
    private RequestQueue requestQueue;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.frm_bienvenido);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mService = Common.getFCMService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        ImageView imagen = hView.findViewById(R.id.imageView);

        TextView textoNombre = hView.findViewById(R.id.Nombre);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        TextView textoCorreo = hView.findViewById(R.id.correo);

        if (getIntent().getExtras().getString("url") != null) {

            url = getIntent().getExtras().getString("url");

            Picasso.with(this).load(url).transform(new TransformarImagen()).into(imagen);
        }

        if (getIntent().getExtras().getString("nombre") != null) {

            nombre = getIntent().getExtras().getString("nombre");

            textoNombre.setText(nombre);
        }

        if (getIntent().getExtras().getString("email") != null) {

            email = getIntent().getExtras().getString("email");

            textoCorreo.setText(email);
        }

        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Geo fire.

        ref = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);

        geoFire = new GeoFire(ref);

        btnSolicitarTaxi = findViewById(R.id.btnPickUpRequest);

        btnSolicitarTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!conductorEncontrado) {
                    requestPickUpHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else if (conductorEncontradoFav){
                    sendRequestToDriverFavorito(driver_idGlob, latFav, lonFav);
                } else if (conductorEncontrado){
                    sendRequestToDriver(driver_idGlob);
                }

            }
        });

        listaFavoritos();

        setUpLocation();

        updateFirebaseToken();
    }

    /***
     * Método que se ejecuta cuando se cierra sesión.
     */
    private void cerrarSesion() {

        Paper.init(this);

        Paper.book().destroy();

        LoginManager.getInstance().logOut();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(Bienvenido.this, Login_2.class);

        startActivity(intent);

        finish();
    }

    /***
     * Sobreescritura del método, que nos ayudara a realizar distintas funcionalidad.
     * Cada vez que se abre la aplicación se verifica si anteriormente ya ha existido una sesión abiert, para de esta manera no estar ingresando nuevamente los datos.
     * @param savedInstanceState
     */


    /***
     * Método que nos sirve para actualizar el Token del Conductor en la Tabla de Firebase.
     * No recibe parámetros.
     */
    private void updateFirebaseToken() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference tokens = db.getReference(Common.token_tb1);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());

        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

    }

    /***
     * Método que nos sirve para enviar peticion al Conductor, envia notificación al usuario de la App serviTaxi Conductor.
     * @param driver_id recibe como parametro un String id, que va a ser utilizado para identificar al conductor que se le va a enviar.
     */

    private void sendRequestToDriver(final String driver_id) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tb1);

        tokens.orderByKey().equalTo(driver_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                            Token token = postSnapShot
                                    .getValue(Token.class); //Obtener Token de database con key

                            String json_lat_lng = new Gson()
                                    .toJson(new LatLng(mUltimaUbicacion.getLatitude(),
                                            mUltimaUbicacion.getLongitude()));

                            String riderToken = FirebaseInstanceId
                                    .getInstance()
                                    .getToken();

                            Notification data = new Notification(
                                    riderToken,
                                    json_lat_lng); //Enviar a conductor app

                            Sender content = new Sender(token.getToken(),
                                    data); //Enviar la data al token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call,
                                                               Response<FCMResponse> response) {

                                            if (response.body().success == 1) {

                                                Toast.makeText(Bienvenido.this, "Peticion Enviada", Toast.LENGTH_SHORT).show();
                                                conductorEncontrado = false;
                                                conductorEncontradoFav = false;
                                                driver_idGlob = "";
                                                btnSolicitarTaxi.setText("Solicitar Taxi");
                                            } else {

                                                Toast.makeText(Bienvenido.this, "La Peticion Fallo", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /***
     * Método que nos sirve para enviar peticion favorita al Conductor, envia notificación al usuario de la App serviTaxi Conductor.
     * @param driver_id recibe como parametro un String id, que va a ser utilizado para identificar al conductor que se le va a enviar.
     */
    private void sendRequestToDriverFavorito(final String driver_id, final Double latitud, final Double longitud) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tb1);

        tokens.orderByKey().equalTo(driver_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                            Token token = postSnapShot
                                    .getValue(Token.class); //Obtener Token de database con key

                            String json_lat_lng = new Gson()
                                    .toJson(new LatLng(latitud,
                                            longitud));

                            String riderToken = FirebaseInstanceId
                                    .getInstance()
                                    .getToken();

                            Notification data = new Notification(
                                    riderToken,
                                    json_lat_lng); //Enviar a conductor app

                            Sender content = new Sender(token.getToken(),
                                    data); //Enviar la data al token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call,
                                                               Response<FCMResponse> response) {

                                            if (response.body().success == 1) {

                                                Toast.makeText(Bienvenido.this, "Peticion Enviada", Toast.LENGTH_SHORT).show();
                                                conductorEncontradoFav = false;
                                                conductorEncontrado = false;
                                                driver_idGlob = "";
                                                btnSolicitarTaxi.setText("Solicitar Taxi");
                                            } else {

                                                Toast.makeText(Bienvenido.this, "La Peticion Fallo", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /***
     * Método que nos srive para mostrar la ventana de Agregar Favoritos.
     */
    private void mostrarAgregarFavs() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Agregar a Favoritos?");
        dialog.setMessage("Desea agregar la ubicación actual a Favoritos?");

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_Layout = inflater.
                inflate(R.layout.frm_agregar_favoritos, null);

        dialog.setView(register_Layout);

        dialog.setPositiveButton("Agregar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        agregarFav();

                        //dialogInterface.dismiss();
                    }
                });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    /**
     * Método que nos sirve para agregar Favoritos a nuestro servicio.
     */
    private void agregarFav() {
        String direccionText = "";
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<android.location.Address> direcciones = null;

        try {
            direcciones = geocoder.getFromLocation(mUltimaUbicacion.getLatitude(), mUltimaUbicacion.getLongitude(),1);
        } catch (Exception e) {
            Log.d("Error", "Error en geocoder:"+e.toString());
        }

        if(direcciones != null && direcciones.size() > 0 ){

            android.location.Address direccion = direcciones.get(0);

            direccionText = direccion.getThoroughfare();
        }

        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("external",  String.valueOf(Paper.book().read(Common.external_id)));
        mapa.put("direccion", String.valueOf(direccionText));
        mapa.put("latitud", String.valueOf(mUltimaUbicacion.getLatitude()));
        mapa.put("longitud", String.valueOf(mUltimaUbicacion.getLongitude()));

        VolleyPeticion<MensajeBackJson> agregarFav = Conexion.registrarFavorito(
                getApplicationContext(),
                mapa,
                new com.android.volley.Response.Listener<MensajeBackJson>() {
                    @Override
                    public void onResponse(MensajeBackJson response) {
                        if (response != null && ("FD".equalsIgnoreCase(response.Siglas)
                                || "DNF".equalsIgnoreCase(response.Siglas))) {
                            Toast.makeText(Bienvenido.this, response.Mensaje, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Bienvenido.this, "Se agregó correctamente", Toast.LENGTH_SHORT).show();
                            listaFavoritos();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());

                        return;
                    }
                }
        );

        requestQueue.add(agregarFav);
    }

    /**
     * Método que nos ayuda a buscar Taxis en nuestra zona, en el radio establecido para nuestra ubicación actual.
     * @param uid este parametro creará una nueva solicitud en la BD de firebase.
     */
    private void requestPickUpHere(String uid) {

        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.solicitud_tb1);

        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mUltimaUbicacion.getLatitude()
                , mUltimaUbicacion.getLongitude()));

        if (mUserMarker.isVisible()) {

            mUserMarker.remove();

        }

        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("Usted")
                .snippet("")
                .position(new LatLng(mUltimaUbicacion.getLatitude(),
                        mUltimaUbicacion.getLongitude()))
                .icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mUserMarker.showInfoWindow();

        btnSolicitarTaxi.setText("Encontrando tu Taxi....");

        findDrivers();
    }

    /**
     * Método que nos ayuda a buscar Taxis en nuestra zona, en el radio establecido para nuestra ubicación escogida de Favoritos.
     * @param uid este parametro creará una nueva solicitud en la BD de firebase.
     */
    private void requestPickUpHereFavorito(String uid, Double latitud, Double longitud) {

        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.solicitud_tb1);

        GeoFire mGeoFire = new GeoFire(dbRequest);

        mGeoFire.setLocation(uid, new GeoLocation(latitud
                , longitud));

        if (mUserMarker.isVisible()) {

            mUserMarker.remove();

        }

        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("Favorito")
                .snippet("")
                .position(new LatLng(latitud,
                        longitud))
                .icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mUserMarker.showInfoWindow();

        btnSolicitarTaxi.setText("Encontrando tu Taxi....");

        findDriversFavorito(latitud, longitud);
    }

    /**
     * Método que nos sirve para encontrar los Taxis cercanos a nuestra ubicación.
     */
    private void findDrivers() {

        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);

        GeoFire gfDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(mUltimaUbicacion.getLatitude(), mUltimaUbicacion.getLongitude()), radio);

        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Si lo encuentra

                if (!conductorEncontrado) {
                    conductorEncontrado = true;
                    driver_idGlob = key;
                    btnSolicitarTaxi.setText("Llamar Taxi");
                    Toast.makeText(Bienvenido.this, "Taxi Encontrado!", Toast.LENGTH_SHORT).show();
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
                if (!conductorEncontrado) {
                    radio++;
                    findDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    /**
     * Método que nos sirve para encontrar los Taxis cercanos a nuestra ubicación previamente solicitada en Favoritos.
     */
    private void findDriversFavorito(final Double latitud, final Double longitud) {
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);

        GeoFire gfDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(latitud, longitud), radio);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Si lo encuentra

                if (!conductorEncontradoFav) {
                    conductorEncontradoFav = true;
                    driver_idGlob = key;
                    btnSolicitarTaxi.setText("Llamar Taxi");
                    Toast.makeText(Bienvenido.this, "Taxi Encontrado!", Toast.LENGTH_SHORT).show();
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
                if (!conductorEncontradoFav) {
                    radio++;
                    findDriversFavorito(latitud, longitud);
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

        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayServices()) {

                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();

                    }
                }
        }
    }

    /***
     * Método que nos ayuda a cambiar la Localización.
     * Esté metodo checa si el usuario ha dado los permisos necesarios que necesita para obtener la ubicación del Cliente.
     */
    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_CODE);
        } else {

            if (checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    /***
     * Método que muestra la ubicación actual y la muestra con un nuevo marker en el Mapa.
     * Esté  método a su vez manda a guardar constantemente la ubicación actual por Geofire a Firebase.
     */
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {

            return;
        }
        mUltimaUbicacion = LocationServices.
                FusedLocationApi.
                getLastLocation(mGoogleApiClient);

        if (mUltimaUbicacion != null) {

            driverAvailable = FirebaseDatabase.getInstance().getReference(Common.drivers_tb1);
            driverAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Si no hay ningun cambia en la tabla Conductor, vamos a recargar
                    //todos los conductores disponibles.

                    loadAllAvailableDrivers();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            final double latitude = mUltimaUbicacion.getLatitude();
            final double longitud = mUltimaUbicacion.getLongitude();

            if (mUserMarker != null) {
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

            rotateMarker(mUserMarker, -360, mMap);

            loadAllAvailableDrivers();

        } else {

            Log.d("ERROR", "CAN NOT GET YOUR LOCATION");
        }

    }

    /***
     * Cargar todos los conductores en un radio de 3km
     */
    private void loadAllAvailableDrivers() {

        /*
         * Primero vamos a borrar todos los marcadores, tanto como los conductores
         * como el nuestro
         * */

        mMap.clear();

        /*
         * Ahora agregamos nuevamente nuestra ubicacion.
         * */

         mUserMarker = mMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.person))
                .position(new LatLng(mUltimaUbicacion.getLatitude(),
                        mUltimaUbicacion.getLongitude()))
                .title("Usted"));

        DatabaseReference driverLocation = FirebaseDatabase.
                getInstance().
                getReference(Common.drivers_tb1);

        GeoFire gf = new GeoFire(driverLocation);

        GeoQuery geoQuery = gf.queryAtLocation
                (new GeoLocation(mUltimaUbicacion.getLatitude(),
                                mUltimaUbicacion.getLongitude()),
                        distance);

        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                FirebaseDatabase.
                        getInstance()
                        .getReference(Common.conductor_tb1).child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Cliente cliente = dataSnapshot.getValue(Cliente.class);

                                                                mMap.addMarker(
                                                                        new MarkerOptions().
                                                                                position(new LatLng(location.latitude,
                                                                                        location.longitude))
                                                                                .flat(true)
                                                                                .title(cliente.getNombre())
                                                                                .icon(BitmapDescriptorFactory
                                                                                        .fromResource(R.drawable.rueda

                                                                                        )));
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
                if (distance <= LIMIT) {
                    distance++;
                    loadAllAvailableDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    /**
     * Este método crea una nueva instancia de tipo LocationRequest.
     * Esta instancia nos permitirá manipular la ubicación actual del conductor, cada que momento necesitamos actualizar el intervalo.
     */
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    /**
     * Este método crea una nueva instancia de tipo GoogleApiClient.
     * Esta instancia trabajará conjuntamente con LocationRequest para la manipulación de la clase Actual.
     */
    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();


    }

    /***
     * Método utiliado para verificar si los servicios han sido concedidos por el usuario.
     * @return boolean.
     * True si todo esta correcto y los servicios han sido concedidos sin ningún problema.
     * False si los permisos no han sido concedidos.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                        this,
                        PLAY_SERVICE_REQUEST_CODE).show();
            } else {
                Toast.makeText(this, "this device is not supported",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
            return false;
        }
        return true;
    }

    /***
     * Método que nos mostrara la ventana de cambiar contraseña.
     * A su vez cambia la contraseña tanto en firebase, Servicio o en Firebase Real Time Database.
     */
    private void cambiarContrasena() {

        final String external = Paper.book().read(Common.external_id);

        final AlertDialog.Builder dialogInicio = new AlertDialog.Builder(this);

        dialogInicio.setTitle("Contraseña");

        dialogInicio.setMessage("Cambiar contraseña");

        LayoutInflater inflater = LayoutInflater.from(this);

        View changePass_layout = inflater.
                inflate(R.layout.changepass, null);

        final MaterialEditText etxtEmail = changePass_layout.
                findViewById(R.id.etxtemailConf);

        final MaterialEditText etxtActuallyPass = changePass_layout.
                findViewById(R.id.etxtActuallyPass);

        final MaterialEditText etxtPass = changePass_layout.
                findViewById(R.id.etxtPass);

        final MaterialEditText etxtConfirmPass = changePass_layout.
                findViewById(R.id.etxtConfirmPass);

        dialogInicio.setView(changePass_layout);

        dialogInicio.setPositiveButton("Cambiar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                        if (TextUtils.isEmpty(etxtEmail.getText().toString())) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.ingreseEmail,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(etxtActuallyPass.getText().toString())) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.ingreseActuallyPass,
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }

                        if (etxtPass.getText().toString().length() < 6) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.contrasenaDemasiadoCorta,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (TextUtils.isEmpty(etxtPass.getText().toString())) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.ingresePass,
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }

                        if (etxtPass.getText().toString().length() < 6) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.contrasenaDemasiadoCorta,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (TextUtils.isEmpty(etxtConfirmPass.getText().toString())) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.ingreseConfirmPass,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (etxtConfirmPass.getText().toString().length() < 6) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.contrasenaDemasiadoCorta,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!etxtPass.getText().toString().equals(etxtConfirmPass.getText().toString())) {

                            Toast.makeText(Bienvenido.this,
                                    R.string.noCoinciden,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        final AuthCredential credential = EmailAuthProvider
                                .getCredential(etxtEmail.getText().toString(), etxtActuallyPass.getText().toString());

                        final android.app.AlertDialog dialogoEspera = new SpotsDialog(Bienvenido.this);

                        dialogoEspera.show();

                        mapa = new HashMap<>();

                        mapa.put("clave", etxtConfirmPass.getText().toString());

                        //region RegistrarCleinte
                        VolleyPeticion<MensajeBackJson> registrar = Conexion.cambiarContrasena(
                                getApplicationContext(),
                                mapa,
                                external,
                                new com.android.volley.Response.Listener<MensajeBackJson>() {
                                    @Override
                                    public void onResponse(MensajeBackJson response) {

                                        if (response != null && ("BDF".equalsIgnoreCase(response.Siglas)
                                                || "NI".equalsIgnoreCase(response.Siglas))) {

                                            Toast.makeText(getApplicationContext(), response.Mensaje, Toast.LENGTH_SHORT).show();

                                            dialogoEspera.dismiss();

                                            return;

                                        } else {

                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                user.updatePassword(etxtConfirmPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            FirebaseDatabase db = FirebaseDatabase.getInstance();

                                                                            DatabaseReference clientes = db.getReference(Common.clientes_tb1);

                                                                            clientes.child(FirebaseAuth.getInstance()
                                                                                    .getCurrentUser().getUid()).child("password").setValue(etxtConfirmPass.getText().toString());

                                                                            Toast.makeText(getApplicationContext(),
                                                                                    R.string.correctPass,
                                                                                    Toast.LENGTH_SHORT).show();

                                                                            dialogoEspera.dismiss();

                                                                        } else {

                                                                            Toast.makeText(Bienvenido.this,
                                                                                    R.string.noChange,
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Log.e("Error", "Error auth failed");
                                                            }
                                                        }
                                                    });




                                        }
                                    }
                                },
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                                        Log.e("Error", error.toString());

                                        return;
                                    }
                                }
                        );

                        requestQueue.add(registrar);
                        //endregion;
                    }
                });

        dialogInicio.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogInicio.show();
    }
    /***
     * Método que nos mostrara la ventana de ?Quiénes somos?.
     * Esta ventana muestra la información de quienes desarrollarón esta app.
     */
    private void mostrarVentanaQuien() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_Layout = inflater.
                inflate(R.layout.frm_quienes, null);

        dialog.setView(register_Layout);

        dialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialog.show();
    }
    /***
     * Este método nos muestra la  ventana de los favoritos del Cliente actualmente Logueado.
     */
    private void mostrarVentanaFavs() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Bienvenido.this);
        builderSingle.setTitle("Favoritos: ");

        final ArrayAdapter<String> arrayAdapterNombre = new ArrayAdapter<String>(Bienvenido.this, android.R.layout.select_dialog_singlechoice);
        final ArrayAdapter<Favorito> arrayAdapter = new ArrayAdapter<Favorito>(Bienvenido.this, android.R.layout.select_dialog_singlechoice);

        if (favoritosLista != null){
            for (Favorito fav : favoritosLista) {
                arrayAdapter.add(fav);
                arrayAdapterNombre.add(String.valueOf(fav.getDireccion()));
            }
        }

        builderSingle.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mostrarAgregarFavs();
            }
        });

        builderSingle.setNegativeButton("Atrás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapterNombre, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final Favorito fav = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Bienvenido.this);
                builderInner.setMessage(fav.getDireccion());
                builderInner.setTitle("Solicitar Taxi en ");
                builderInner.setPositiveButton("Solicitar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        if (!conductorEncontradoFav) {
                            latFav = Double.valueOf(fav.getLatitud());
                            lonFav = Double.valueOf(fav.getLongitud());
                            requestPickUpHereFavorito(FirebaseAuth.getInstance().getCurrentUser().getUid(), Double.valueOf(fav.getLatitud()), Double.valueOf(fav.getLongitud()));
                        }
                    }
                });
                builderInner.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }
    /**
     * Este método nos ayuda a listar todos los Favoritos para poder cargarlo en la Ventana de Favoritos.
     */
    public void listaFavoritos(){
        String external = String.valueOf(Paper.book().read(Common.external_id));

        VolleyPeticion<Favorito[]> favoritos = Conexion.listarFavoritos(
                getApplicationContext(),
                external,
                new com.android.volley.Response.Listener<Favorito[]>() {
                    @Override
                    public void onResponse(Favorito[] response) {
                        if (response != null) {
                            favoritosLista = new Favorito[response.length];
                            favoritosLista = response;
                        } else {
                            //Toast.makeText(Bienvenido.this, "No posee ningún lugar favorito", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Bienvenido.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error", error.getMessage());

                        return;
                    }
                }
        );

        requestQueue.add(favoritos);
    }
    /**
     * Esté método nos ayuda para inicializar la busqueda de Actualizaciones de las Localizaciones.
     * Checando previamente si los permisos estan correcctamente concedidos.
     */
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this
        );


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

        if (id == R.id.cerrarSesion) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favoritos) {
            mostrarVentanaFavs();
        } else if (id == R.id.nav_quienes) {
            mostrarVentanaQuien();

        } else if (id == R.id.cerrarSesion) {

            cerrarSesion();
        } else if (id == R.id.cambiarContrasena) {

            cambiarContrasena();

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

                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * i + (1 - t) * startRotation;

                mCurrent.setRotation(-rot > 360 ? rot / 1 : rot);

                if (t < 1.0) {

                    handler.postDelayed(this, 16);

                }

            }
        });
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
}
