package jeancarlosdev.servitaxi;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jeancarlosdev.servitaxi.Utilidades.Utilidades;

public class Splash_1 extends AppCompatActivity {

    private Utilidades util = new Utilidades(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_splash_1);

        inicializarSDKFacebook();

        Log.e("Contexto", this.getLocalClassName());

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "jeancarlosdev.servitaxi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        startActivity(new Intent(getApplicationContext(), Login_2.class));
    /*
        if(AccessToken.getCurrentAccessToken() != null){

            util.dataFacebook();

        }else{

            util.hiloSplashScreen(new Intent(this, Login_2.class));
        }
        */
    }


    /***
     * Este metodo sirve para Inicializar el SDK de facebook para el Login.
     */

    public void inicializarSDKFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


}
