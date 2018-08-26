package jeancarlosdev.servitaxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;


import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.LockOnGetVariable;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import jeancarlosdev.servitaxi.Common.Common;
import jeancarlosdev.servitaxi.Modelos.Cliente;
import jeancarlosdev.servitaxi.Modelos.ClienteBackJson;
import jeancarlosdev.servitaxi.Modelos.MensajeBackJson;
import jeancarlosdev.servitaxi.Remote.Conexion;
import jeancarlosdev.servitaxi.Remote.VolleyPeticion;
import jeancarlosdev.servitaxi.Remote.VolleyProcesadorResultado;
import jeancarlosdev.servitaxi.Remote.VolleyTiposError;
import jeancarlosdev.servitaxi.Utilidades.Utilidades;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login_2 extends AppCompatActivity {


    //region Atributos
    private Button btnSignIn, btnRegister;
    private RelativeLayout layoutPrincipal;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    DatabaseReference clientes;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String email;
    private String name;
    Utilidades util = new Utilidades(this);

    private RequestQueue requestQueue;

    boolean guardo = false;

    boolean inicioSesion = false;

    HashMap<String, String> mapa;
    //endregion

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.
                Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf").
                        setFontAttrId(R.attr.fontPath).build());

        setContentView(R.layout.frm_login_2);

        callbackManager = CallbackManager.Factory.create();

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        clientes = db.getReference(Common.clientes_tb1);

        loginButton = findViewById(R.id.login_button);

        loginButton.setReadPermissions("email");

        btnSignIn = findViewById(R.id.btnIniciar);

        btnRegister = findViewById(R.id.btnRegistrar);

        layoutPrincipal = findViewById(R.id.layoutPrincipal);

        eventosOnClickBtns();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

    }

    //region Metodos Propios
    public void eventosOnClickBtns() {

        //Inicio de Sesion Facebook
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final String[] arreglo = util.dataFacebook();

                mapa = new HashMap<>();

                mapa.put("correo", arreglo[1]);

                mapa.put("clave", "12345");

                VolleyPeticion<ClienteBackJson> inicio = Conexion.iniciarSesion(
                        getApplicationContext(),
                        mapa,
                        new Response.Listener<ClienteBackJson>() {
                            @Override
                            public void onResponse(ClienteBackJson response) {

                                if (response != null
                                        && response.siglas.equalsIgnoreCase("ND")) {

                                    Snackbar.make(layoutPrincipal,
                                            "Error " + response.mensaje,
                                            Snackbar.LENGTH_SHORT
                                    ).show();

                                    btnSignIn.setEnabled(true);

                                    mapa = new HashMap<>();

                                    mapa.put("correo", arreglo[1]);

                                    mapa.put("clave", "123456789");

                                    final String apellidos[] = (arreglo[0]).split(" ");

                                    String apellido = "";

                                    if (apellidos.length > 1) {

                                        apellido = apellidos[2];

                                        if (apellidos.length > 2) {

                                            apellido = apellido + apellidos[3];
                                        }
                                    }

                                    final String apellidoFinal = apellido;

                                    mapa.put("nombre", arreglo[0]);

                                    mapa.put("apellido", apellido);

                                    //region RegistrarCliente

                                    VolleyPeticion<MensajeBackJson> registrar = Conexion.registrarCliente(
                                            getApplicationContext(),
                                            mapa,
                                            new Response.Listener<MensajeBackJson>() {
                                                @Override
                                                public void onResponse(MensajeBackJson response) {

                                                    if (response != null && response.siglas.equalsIgnoreCase("FD")) {

                                                        Toast.makeText(layoutPrincipal.getContext(), response.mensaje, Toast.LENGTH_SHORT).show();

                                                        btnSignIn.setEnabled(true);

                                                        Log.e("ERROR", response.mensaje);

                                                        return;

                                                    } else {

                                                        auth.createUserWithEmailAndPassword(
                                                                arreglo[1],
                                                                "123456789"
                                                        )
                                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                    @Override
                                                                    public void onSuccess(AuthResult authResult) {

                                                                        Cliente cliente = new Cliente();

                                                                        cliente.setEmail(arreglo[1]);
                                                                        cliente.setPassword("123456789");
                                                                        cliente.setNombre(arreglo[0]);
                                                                        cliente.setApellidos(apellidoFinal);

                                                                        //Usamos al email como llave primaria.
                                                                        clientes.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                .setValue(cliente)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {

                                                                                        Snackbar.make(layoutPrincipal,
                                                                                                "Cliente Registrado correctamente",
                                                                                                Snackbar.LENGTH_SHORT).show();

                                                                                        startActivity(new Intent(getApplicationContext(), Bienvenido.class));
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                        Snackbar.make(layoutPrincipal,
                                                                                                "Cliente no registrado " + e.getMessage(),
                                                                                                Snackbar.LENGTH_SHORT).show();

                                                                                        Log.e("Cliente no registrado", e.getMessage());

                                                                                        return;
                                                                                    }
                                                                                });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Snackbar.make(layoutPrincipal,
                                                                        "Cliente no registrado" + e.getMessage(),
                                                                        Snackbar.LENGTH_SHORT).show();

                                                                Log.e("Cliente no registrado", e.getMessage());

                                                                return;
                                                            }

                                                        });

                                                        Toast.makeText(getApplicationContext(),
                                                                "Se ha guardado correctamente",
                                                                Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                    VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                                                    Log.e("Error", error.toString());

                                                    return;
                                                }

                                            }
                                    );

                                    requestQueue.add(registrar);
                                    //endregion


                                } else {

                                    Snackbar.make(layoutPrincipal,
                                            "Bienvenido, " + response.nombre,
                                            Snackbar.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(), Bienvenido.class));

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                                Log.e("Error", error.toString());

                                mapa = new HashMap<>();

                                mapa.put("correo", arreglo[1]);

                                mapa.put("clave", "12345");

                                final String apellidos[] = (arreglo[0]).split(" ");

                                String apellido = "";

                                if (apellidos.length > 2) {

                                    apellido = apellidos[2];

                                    if (apellidos.length > 3) {

                                        apellido = apellido + apellidos[3];
                                    }
                                }

                                final String apellidoFinal = apellido;

                                mapa.put("nombre", arreglo[0]);

                                mapa.put("apellido", apellidoFinal);

                                //region RegistrarCliente

                                VolleyPeticion<MensajeBackJson> registrar = Conexion.registrarCliente(
                                        getApplicationContext(),
                                        mapa,
                                        new Response.Listener<MensajeBackJson>() {
                                            @Override
                                            public void onResponse(MensajeBackJson response) {

                                                if (response != null && response.siglas.equalsIgnoreCase("FD")) {

                                                    Toast.makeText(layoutPrincipal.getContext(), response.mensaje, Toast.LENGTH_SHORT).show();

                                                    btnSignIn.setEnabled(true);

                                                    Log.e("ERROR", response.mensaje);

                                                    return;

                                                } else {

                                                    auth.createUserWithEmailAndPassword(
                                                            arreglo[1],
                                                            "123456789"
                                                    )
                                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                @Override
                                                                public void onSuccess(AuthResult authResult) {

                                                                    Cliente cliente = new Cliente();

                                                                    cliente.setEmail(arreglo[1]);
                                                                    cliente.setPassword("123456789");
                                                                    cliente.setNombre(arreglo[0]);
                                                                    cliente.setApellidos(apellidoFinal);

                                                                    //Usamos al email como llave primaria.
                                                                    clientes.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .setValue(cliente)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    Snackbar.make(layoutPrincipal,
                                                                                            "Cliente Registrado correctamente",
                                                                                            Snackbar.LENGTH_SHORT).show();

                                                                                    startActivity(new Intent(getApplicationContext(), Bienvenido.class));
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                    Snackbar.make(layoutPrincipal,
                                                                                            "Cliente no registrado " + e.getMessage(),
                                                                                            Snackbar.LENGTH_SHORT).show();

                                                                                    Log.e("Cliente no registrado", e.getMessage());

                                                                                    return;
                                                                                }
                                                                            });
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Snackbar.make(layoutPrincipal,
                                                                    "Cliente no registrado" + e.getMessage(),
                                                                    Snackbar.LENGTH_SHORT).show();

                                                            Log.e("Cliente no registrado", e.getMessage());

                                                            return;
                                                        }

                                                    });

                                                    Toast.makeText(getApplicationContext(),
                                                            "Se ha guardado correctamente",
                                                            Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                                                Log.e("Error", error.toString());

                                                return;
                                            }

                                        }
                                );

                                requestQueue.add(registrar);
                                //endregion


                                return;
                            }
                        }
                );

                requestQueue.add(inicio);


            }

            @Override
            public void onCancel() {

                mostrarMensaje(R.string.cancel_login);
            }

            @Override
            public void onError(FacebookException error) {

                mostrarMensaje(R.string.error_Login);

                Log.e("ERROR DE FACEBOOK", error.toString());
            }
        });


        //Mostrar Ventana Registrar
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarVentanaRegistro();
            }
        });

        //Mostrar Ventana para inicio de Sesion.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarVentanaLogin();
            }
        });
    }


    private void mostrarMensaje(int identificador_mensaje) {

        Toast.makeText(Login_2.this,
                identificador_mensaje,
                Toast.LENGTH_SHORT).show();
    }

    private void mostrarVentanaLogin() {
        AlertDialog.Builder dialogInicio = new AlertDialog.Builder(this);

        dialogInicio.setTitle("Inicio de Sesion");

        dialogInicio.setMessage("Inicio de sesion Cliente Servitaxi");

        LayoutInflater inflater = LayoutInflater.from(this);

        View inicioSesion_layout = inflater.
                inflate(R.layout.frm_inicio_sesion, null);

        final MaterialEditText etxtEmailInicio = inicioSesion_layout.
                findViewById(R.id.etxtEmailInicio);

        final MaterialEditText etxtContrasenaInicio = inicioSesion_layout.
                findViewById(R.id.etxtContrasenaInicio);


        dialogInicio.setView(inicioSesion_layout);

        dialogInicio.setPositiveButton("INICIAR SESION",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                        btnSignIn.setEnabled(false);

                        /*Desactivo el boton de inicio de Sesion
                         * para que el usuario no este manipulando*/

                        if (TextUtils.isEmpty(etxtEmailInicio.getText().toString())) {

                            Snackbar.make(layoutPrincipal,
                                    "Por favor ingrese una direccion de correo " +
                                            "electronico",
                                    Snackbar.LENGTH_SHORT).show();

                            return;
                        }

                        if (TextUtils.isEmpty(etxtContrasenaInicio.getText().toString())) {

                            Snackbar.make(layoutPrincipal,
                                    "Por favor ingrese contrasena"
                                    , Snackbar.LENGTH_SHORT).show();

                            return;
                        }

                        if (etxtContrasenaInicio.getText().toString().length() < 6) {

                            Snackbar.make(layoutPrincipal,
                                    "Contrasena demasiado corta ",
                                    Snackbar.LENGTH_SHORT).show();

                            return;
                        }
                        //Autentificar con el servicio;
                        final android.app.AlertDialog dialogoEspera = new SpotsDialog(Login_2.this);

                        dialogoEspera.show();

                        auth.signInWithEmailAndPassword(etxtEmailInicio.getText().toString(),
                                etxtContrasenaInicio.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        dialogoEspera.dismiss();
                                        startActivity(new Intent(Login_2.this
                                                , Bienvenido.class));

                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogoEspera.dismiss();
                                Snackbar.make(layoutPrincipal,
                                        "Error" + e.getMessage(),
                                        Snackbar.LENGTH_SHORT).show();

                                btnSignIn.setEnabled(true);
                            }
                        });
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

    private void mostrarVentanaRegistro() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Registrar Conductor");
        dialog.setMessage("Registro de Conductor ServiTaxi");

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_Layout = inflater.
                inflate(R.layout.frm_registrar, null);

        final MaterialEditText etxtEmail = register_Layout.findViewById(
                R.id.etxtEmail);

        final MaterialEditText etxtContrasena = register_Layout.findViewById(
                R.id.etxtContrasena);

        final MaterialEditText etxtApellido = register_Layout.findViewById(
                R.id.etxtApellido);

        final MaterialEditText etxtNombre = register_Layout.findViewById(
                R.id.etxtNombre);

        dialog.setView(register_Layout);

        dialog.setPositiveButton("REGISTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(etxtEmail.getText().toString())) {

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese una direccion de correo " +
                                    "electronico",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                if (TextUtils.isEmpty(etxtContrasena.getText().toString())) {

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese contrasena"
                            , Snackbar.LENGTH_SHORT).show();

                    return;
                }


                if (etxtContrasena.getText().toString().length() < 4) {

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese una contrasena con mas de " +
                                    "6 caracteres ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                if (TextUtils.isEmpty(etxtNombre.getText().toString())) {

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese su Nombre ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                if (TextUtils.isEmpty(etxtApellido.getText().toString())) {

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese su telefono de celular ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }
                mapa = new HashMap<>();

                mapa.put("correo", etxtEmail.getText().toString());

                mapa.put("clave", etxtContrasena.getText().toString());

                mapa.put("nombre", etxtNombre.getText().toString());

                mapa.put("apellido", etxtApellido.getText().toString());

                //region RegistrarCliente

                VolleyPeticion<MensajeBackJson> registrar = Conexion.registrarCliente(
                        getApplicationContext(),
                        mapa,
                        new Response.Listener<MensajeBackJson>() {
                            @Override
                            public void onResponse(MensajeBackJson response) {

                                if (response != null && response.siglas.equalsIgnoreCase("FD")) {

                                    Toast.makeText(layoutPrincipal.getContext(), response.mensaje, Toast.LENGTH_SHORT).show();

                                    btnSignIn.setEnabled(true);

                                    Log.e("ERROR", response.mensaje);

                                    return;

                                } else {

                                    auth.createUserWithEmailAndPassword(
                                            etxtEmail.getText().toString(),
                                            etxtContrasena.getText().toString()
                                    )
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {

                                                    Cliente cliente = new Cliente();

                                                    cliente.setEmail(etxtEmail.getText().toString());
                                                    cliente.setPassword(etxtContrasena.getText().toString());
                                                    cliente.setNombre(etxtNombre.getText().toString());
                                                    cliente.setApellidos(etxtApellido.getText().toString());

                                                    //Usamos al email como llave primaria.
                                                    clientes.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .setValue(cliente)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    Snackbar.make(layoutPrincipal,
                                                                            "Cliente Registrado correctamente",
                                                                            Snackbar.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Snackbar.make(layoutPrincipal,
                                                                            "Cliente no registrado " + e.getMessage(),
                                                                            Snackbar.LENGTH_SHORT).show();

                                                                    return;
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(layoutPrincipal,
                                                    "Cliente no registrado" + e.getMessage(),
                                                    Snackbar.LENGTH_SHORT).show();

                                            return;
                                        }

                                    });

                                    Toast.makeText(getApplicationContext(),
                                            "Se ha guardado correctamente",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                                Log.e("Error", error.toString());

                                return;
                            }

                        }
                );

                requestQueue.add(registrar);
                //endregion

            }
        });

        dialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    //endregion
    private boolean registrarCliente(
            @NonNull HashMap<String, String> map) {

        VolleyPeticion<MensajeBackJson> registrar = Conexion.registrarCliente(
                getApplicationContext(),
                map,
                new Response.Listener<MensajeBackJson>() {
                    @Override
                    public void onResponse(MensajeBackJson response) {

                        if (response != null && response.siglas.equalsIgnoreCase("FD")) {

                            Toast.makeText(layoutPrincipal.getContext(), response.mensaje, Toast.LENGTH_SHORT).show();

                            btnSignIn.setEnabled(true);

                            Log.e("ERROR", response.mensaje);

                            return;

                        } else {

                            Toast.makeText(layoutPrincipal.getContext(), response.mensaje, Toast.LENGTH_SHORT).show();

                            guardo = true;

                            Log.e("BOOLEAAAAAAAAAAAAN", guardo + "");
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                        Log.e("Error", error.toString());

                        Toast.makeText(layoutPrincipal.getContext(), "Fallo en la operacion, estamos en Error Listener", Toast.LENGTH_SHORT).show();

                        return;
                    }

                }
        );
        requestQueue.add(registrar);

        Log.e("BOOLEAAAAAAAAAAAAN", guardo + "");

        return guardo;
    }

    private boolean iniciarSesionCliente(
            @NonNull HashMap<String, String> map) {

        VolleyPeticion<ClienteBackJson> inicio = Conexion.iniciarSesion(
                getApplicationContext(),
                map,
                new Response.Listener<ClienteBackJson>() {
                    @Override
                    public void onResponse(ClienteBackJson response) {

                        if (response != null
                                && response.siglas.equalsIgnoreCase("ND")) {

                            Snackbar.make(layoutPrincipal,
                                    "Error " + response.mensaje,
                                    Snackbar.LENGTH_SHORT
                            ).show();

                            btnSignIn.setEnabled(true);

                            return;

                        } else {

                            Snackbar.make(layoutPrincipal,
                                    "Bienvenido, " + response.nombre,
                                    Snackbar.LENGTH_SHORT).show();

                            inicioSesion = true;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyTiposError errors = VolleyProcesadorResultado.parseErrorResponse(error);

                        Log.e("Error", error.toString());

                        Snackbar.make(layoutPrincipal,
                                errors.errorMessage,
                                Snackbar.LENGTH_SHORT).show();

                        return;
                    }
                }
        );

        requestQueue.add(inicio);

        return inicioSesion;
    }
}
