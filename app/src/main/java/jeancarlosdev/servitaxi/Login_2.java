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
import com.facebook.AccessToken;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
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
    private String name;
    Utilidades util = new Utilidades(this);

    private RequestQueue requestQueue;

    boolean guardo = false;

    boolean inicioSesion = false;

    HashMap<String, String> mapa;

    String nombre;

    String user;

    String imagen;


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

        Paper.init(this);

        callbackManager = CallbackManager.Factory.create();

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        clientes = db.getReference(Common.clientes_tb1);

        loginButton = findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        btnSignIn = findViewById(R.id.btnIniciar);

        btnRegister = findViewById(R.id.btnRegistrar);

        layoutPrincipal = findViewById(R.id.layoutPrincipal);

        user = Paper.book().read(Common.cliente);
        String pass = Paper.book().read(Common.password);
         nombre = Paper.book().read(Common.nombre);
         imagen = Paper.book().read(Common.imagen);

        if (user != null && pass != null) {
            if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
                autoLogin(user, pass);
            }
        }

        eventosOnClickBtns();
    }

    private void autoLogin(final String user, String pass) {

        final android.app.AlertDialog dialogoEspera = new SpotsDialog(Login_2.this);

        dialogoEspera.show();

        //IniciarSesion.

        auth.signInWithEmailAndPassword(user, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        dialogoEspera.dismiss();

                        FirebaseDatabase.getInstance().getReference(Common.conductor_tb1)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        Intent intencion = new Intent(getApplicationContext(),
                                                Bienvenido.class);

                                        nombre = Paper.book().read(Common.nombre);

                                        imagen = Paper.book().read(Common.imagen);


                                        intencion.putExtra("nombre",nombre );

                                        intencion.putExtra("email",user);

                                        intencion.putExtra("url" , imagen);

                                        startActivity(intencion);

                                        dialogoEspera.dismiss();

                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {

                                    final String ruta = ("https://graph.facebook.com/"+object.getString("id")+"/picture?width=150&height=150");

                                    final String nombre = object.getString("name");

                                    final String email = object.getString("email");

                                    mapa = new HashMap<>();

                                    mapa.put("correo", email);

                                    mapa.put("clave", "123456789");

                                    VolleyPeticion<ClienteBackJson> inicio = Conexion.iniciarSesion(
                                            getApplicationContext(),
                                            mapa,
                                            new Response.Listener<ClienteBackJson>() {
                                                @Override
                                                public void onResponse(final ClienteBackJson response) {

                                                    if (response != null && (response.siglas.equalsIgnoreCase("ND"))) {

                                                        mapa = new HashMap<>();

                                                        mapa.put("correo", email);

                                                        mapa.put("clave", "123456789");

                                                        final String apellidos[] = (nombre.split(" "));

                                                        String apellido = "";

                                                        if (apellidos.length > 2) {

                                                            apellido = apellidos[2];

                                                            if (apellidos.length > 3) {

                                                                apellido = apellido + apellidos[3];
                                                            }
                                                        }

                                                        final String apellidoFinal = apellido;

                                                        mapa.put("nombre", nombre);

                                                        mapa.put("apellido", apellido);

                                                        //region RegistrarCleinte
                                                        VolleyPeticion<MensajeBackJson> registrar = Conexion.registrarCliente(
                                                                getApplicationContext(),
                                                                mapa,
                                                                new Response.Listener<MensajeBackJson>() {
                                                                    @Override
                                                                    public void onResponse(MensajeBackJson response) {

                                                                        if (response != null && ("FD".equalsIgnoreCase(response.Siglas)
                                                                                || "DNF".equalsIgnoreCase(response.Siglas))) {

                                                                            Toast.makeText(layoutPrincipal.getContext(), response.Mensaje, Toast.LENGTH_SHORT).show();

                                                                            return;

                                                                        } else {

                                                                            auth.createUserWithEmailAndPassword(
                                                                                    mapa.get("correo"),
                                                                                    mapa.get("clave")
                                                                            )
                                                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                                        @Override
                                                                                        public void onSuccess(AuthResult authResult) {

                                                                                            Cliente cliente = new Cliente();

                                                                                            cliente.setEmail(mapa.get("correo"));
                                                                                            cliente.setPassword(mapa.get("clave"));
                                                                                            cliente.setNombre(mapa.get("nombre"));
                                                                                            cliente.setApellidos(mapa.get("apellido"));

                                                                                            //Usamos al email como llave primaria.
                                                                                            clientes.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                    .setValue(cliente)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {

                                                                                                            Paper.book().write(Common.cliente, mapa.get("correo"));
                                                                                                            Paper.book().write(Common.password, mapa.get("clave"));
                                                                                                            Paper.book().write(Common.nombre, mapa.get("nombre"));
                                                                                                            Paper.book().write(Common.imagen, ruta);

                                                                                                            Intent intencion = new Intent(getApplicationContext(),
                                                                                                                    Bienvenido.class);

                                                                                                            intencion.putExtra("nombre",nombre );

                                                                                                            intencion.putExtra("email",email );

                                                                                                            intencion.putExtra("url",ruta);

                                                                                                            startActivity(intencion);
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
                                                        //endregion;


                                                    } else {

                                                        auth.signInWithEmailAndPassword(email,
                                                                "123456789")
                                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                    @Override
                                                                    public void onSuccess(AuthResult authResult) {

                                                                        Paper.book().write(Common.cliente, email);

                                                                        Paper.book().write(Common.password, "123456789");

                                                                        Paper.book().write(Common.nombre, response.nombre);

                                                                        Paper.book().write(Common.imagen, ruta);

                                                                        Intent intencion = new Intent(getApplicationContext(),
                                                                                Bienvenido.class);

                                                                        intencion.putExtra("nombre",nombre );

                                                                        intencion.putExtra("email",email );

                                                                        intencion.putExtra("url",ruta);

                                                                        startActivity(intencion);

                                                                        finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                Snackbar.make(layoutPrincipal,
                                                                        "Error" + e.getMessage(),
                                                                        Snackbar.LENGTH_SHORT).show();

                                                            }
                                                        });


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

                                    requestQueue.add(inicio);


                                } catch (Exception e) {

                                    Log.e("Error", e.toString());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                request.setParameters(parameters);
                request.executeAsync();
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

                        mapa = new HashMap<>();

                        mapa.put("correo", etxtEmailInicio.getText().toString());

                        mapa.put("clave", etxtContrasenaInicio.getText().toString());

                        VolleyPeticion<ClienteBackJson> inicio = Conexion.iniciarSesion(
                                getApplicationContext(),
                                mapa,
                                new Response.Listener<ClienteBackJson>() {
                                    @Override
                                    public void onResponse(final ClienteBackJson response) {

                                        if (response != null
                                                && response.siglas.equalsIgnoreCase("ND")) {

                                            Snackbar.make(layoutPrincipal,
                                                    "Error " + response.mensaje,
                                                    Snackbar.LENGTH_SHORT
                                            ).show();

                                            btnSignIn.setEnabled(true);

                                            return;

                                        } else {

                                            auth.signInWithEmailAndPassword(etxtEmailInicio.getText().toString(),
                                                    etxtContrasenaInicio.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {

                                                            Paper.book().write(Common.cliente, etxtEmailInicio.getText().toString());
                                                            Paper.book().write(Common.password, etxtContrasenaInicio.getText().toString());
                                                            Paper.book().write(Common.nombre, response.nombre);

                                                            dialogoEspera.dismiss();

                                                            Intent intencion = new Intent(getApplicationContext(),
                                                                    Bienvenido.class);

                                                            intencion.putExtra("nombre", response.nombre + " " +response.apellido
                                                            );

                                                            intencion.putExtra("email", etxtEmailInicio.getText().toString());

                                                            startActivity(intencion);

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

                                if (response != null && ("FD".equalsIgnoreCase(response.Siglas) || "DNF".equalsIgnoreCase(response.Siglas))) {

                                    Toast.makeText(layoutPrincipal.getContext(), response.Mensaje, Toast.LENGTH_SHORT).show();

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

}
