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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;


import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import dmax.dialog.SpotsDialog;
import jeancarlosdev.servitaxi.Common.Common;
import jeancarlosdev.servitaxi.Modelos.Cliente;
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
    public void eventosOnClickBtns(){

        //Inicio de Sesion Facebook
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                 util.dataFacebook();
            }

            @Override
            public void onCancel() {

                mostrarMensaje(R.string.cancel_login);
            }

            @Override
            public void onError(FacebookException error) {

                mostrarMensaje( R.string.error_Login);

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

        dialogInicio.setMessage("Inicio de sesion conductor ServiTaxi");

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

                        if(TextUtils.isEmpty(etxtEmailInicio.getText().toString())){

                            Snackbar.make(layoutPrincipal,
                                    "Por favor ingrese una direccion de correo " +
                                            "electronico",
                                    Snackbar.LENGTH_SHORT).show();

                            return;
                        }

                        if(TextUtils.isEmpty(etxtContrasenaInicio.getText().toString())){

                            Snackbar.make(layoutPrincipal,
                                    "Por favor ingrese contrasena"
                                    , Snackbar.LENGTH_SHORT).show();

                            return;
                        }

                        if(etxtContrasenaInicio.getText().toString().length() < 6){

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
                                        "Error" +e.getMessage(),
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
        AlertDialog.Builder dialog=  new AlertDialog.Builder(this);
        dialog.setTitle("Registrar Conductor");
        dialog.setMessage("Registro de Conductor ServiTaxi");

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_Layout = inflater.
                inflate(R.layout.frm_registrar, null);

        final MaterialEditText etxtEmail = register_Layout.findViewById(
                R.id.etxtEmail);

        final MaterialEditText etxtContrasena = register_Layout.findViewById(
                R.id.etxtContrasena);

        final MaterialEditText etxtTelefono = register_Layout.findViewById(
                R.id.etxtTelefono);

        final MaterialEditText etxtNombre = register_Layout.findViewById(
                R.id.etxtNombre);

        dialog.setView(register_Layout);

        dialog.setPositiveButton("REGISTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(TextUtils.isEmpty(etxtEmail.getText().toString())){

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese una direccion de correo " +
                                    "electronico",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                if(TextUtils.isEmpty(etxtContrasena.getText().toString())){

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese contrasena"
                            , Snackbar.LENGTH_SHORT).show();

                    return;
                }


                if(etxtContrasena.getText().toString().length() < 6){

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese una contrasena con mas de " +
                                    "6 caracteres ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                if(TextUtils.isEmpty(etxtNombre.getText().toString())){

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese su Nombre ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }


                if(TextUtils.isEmpty(etxtTelefono.getText().toString())){

                    Snackbar.make(layoutPrincipal,
                            "Por favor ingrese su telefono de celular ",
                            Snackbar.LENGTH_SHORT).show();

                    return;
                }

                auth.createUserWithEmailAndPassword(
                        etxtEmail.getText().toString(),
                        etxtContrasena.getText().toString()
                )
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //Guardar Usuario en la base de datos.

                                Cliente cliente = new Cliente();

                                cliente.setEmail(etxtEmail.getText().toString());
                                cliente.setPassword(etxtContrasena.getText().toString());
                                cliente.setNombre(etxtNombre.getText().toString());
                                cliente.setTelefono(etxtTelefono.getText().toString());

                                //Usamos al email como llave primaria.
                                clientes.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(cliente)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Snackbar.make(layoutPrincipal,
                                                        "Cliente Registrado correctamente",
                                                        Snackbar.LENGTH_SHORT ).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Snackbar.make(layoutPrincipal,
                                                        "Cliente no registrado " + e.getMessage(),
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(layoutPrincipal,
                                "Cliente no registrado" + e.getMessage(),
                                Snackbar.LENGTH_SHORT ).show();
                    }
                });

                Toast.makeText(getApplicationContext(),
                        "Se ha guardado correctamente",
                        Toast.LENGTH_SHORT).show();
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
