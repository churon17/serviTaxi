package jeancarlosdev.servitaxi;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login;

import org.json.JSONObject;

import jeancarlosdev.servitaxi.Utilidades.Utilidades;

public class Splash_1 extends AppCompatActivity {

    private String nombre;

    private String email;

    private Utilidades util = new Utilidades(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_splash_1);

        inicializarSDKFacebook();

        Log.e("Contexto", this.getLocalClassName());

        if(AccessToken.getCurrentAccessToken() != null){

            util.dataFacebook();

        }else{

            util.hiloSplashScreen(new Intent(this, Login_2.class));
        }
    }



    public void inicializarSDKFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


}
