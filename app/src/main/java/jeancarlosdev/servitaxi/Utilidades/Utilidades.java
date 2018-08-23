package jeancarlosdev.servitaxi.Utilidades;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import jeancarlosdev.servitaxi.Bienvenido;


public class Utilidades {

    private String nombre = "";

    private String email = "";

    private Context contexto;

    public Utilidades(Context contexto) {

        this.contexto = contexto;

    }

    public  void dataFacebook(){

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Intent intencion = new Intent(contexto, Bienvenido.class);
                        try {

                            nombre = object.getString("name");

                            email = object.getString("email");

                            intencion.putExtra("nombre", nombre);

                            intencion.putExtra("email", email);

                            contexto.startActivity(intencion);

                        }catch (Exception e){

                            Log.e("Error", e.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    public void hiloSplashScreen(final Intent intencion){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                contexto.startActivity(intencion);
            }
        }, 2000);

    }
}
