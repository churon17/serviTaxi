package jeancarlosdev.servitaxi.Utilidades;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import jeancarlosdev.servitaxi.Bienvenido;


public class Utilidades  extends StringUtils{

    private String nombre = "";

    private String email = "";

    private Context contexto;

    String[] arregloRetornar = new String[2];


    public Utilidades(Context contexto) {

        this.contexto = contexto;

    }

    public static String formatoFecha(Date date){
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strFecha = "";

        try {
            strFecha = formato.format(date);
            return strFecha;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }

    public  String[] dataFacebook(){

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //Intent intencion = new Intent(contexto, Bienvenido.class);
                        try {

                            nombre = object.getString("name");

                            email = object.getString("email");

                            /*Se ejecuta despues*/


                            arregloRetornar[0] = nombre;

                            arregloRetornar[1] = email;

                            //intencion.putExtra("nombre", nombre);

                            //intencion.putExtra("email", email);

                            //contexto.startActivity(intencion);

                        }catch (Exception e){

                            Log.e("Error", e.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        request.setParameters(parameters);
        request.executeAsync();

        return  arregloRetornar;
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
