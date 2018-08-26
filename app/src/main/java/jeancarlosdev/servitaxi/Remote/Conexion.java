package jeancarlosdev.servitaxi.Remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;

import jeancarlosdev.servitaxi.Modelos.ClienteBackJson;
import jeancarlosdev.servitaxi.Modelos.MensajeBackJson;


public class Conexion {

    private final static String API_URL = "https://servitaxi.000webhostapp.com/ServicioWEB/index.php/";

    public static VolleyPeticion<ClienteBackJson> iniciarSesion(@NonNull final Context contexto,
                                                                @NonNull final HashMap mapa,
                                                                @NonNull Response.Listener<ClienteBackJson> response_Listener,
                                                                @NonNull Response.ErrorListener errorListener){

        final String url = API_URL + "cliente/iniciarSesion";

        VolleyPeticion request = new VolleyPeticion(contexto,
                Request.Method.POST, //Tipo de metodo.
                url,
                mapa,
                HashMap.class,
                String.class,
                response_Listener,
                errorListener);

        request.setResponseClass(ClienteBackJson.class);

        return request;
    }

    public static VolleyPeticion<MensajeBackJson> registrarCliente(@NonNull final Context contexto,
                                                                   @NonNull final HashMap mapa,
                                                                   @NonNull Response.Listener<MensajeBackJson> response_Listener,
                                                                   @NonNull Response.ErrorListener errorListener){

        final String url = API_URL + "cliente/guardar";

        VolleyPeticion request = new VolleyPeticion(contexto,
                Request.Method.POST,
                url,
                mapa,
                HashMap.class,
                String.class,
                response_Listener,
                errorListener);

        request.setResponseClass(MensajeBackJson.class);

        return request;
    }




    /*
    public static VolleyPeticion<Noticia[]> listaNoticiasAdmin(
            @NonNull final Context context,
            @NonNull final String token,
            @NonNull final String id,
            @NonNull Response.Listener<Noticia[]> responseListener,
            @NonNull Response.ErrorListener errorListener
    ){
        final String url = API_URL + "administracion/noticias/list/" +id;
        VolleyPeticion request = new VolleyPeticion(
                context,
                Request.Method.GET,
                url,
                responseListener,
                errorListener
        );
        request.setResponseClass(Noticia[].class);
        try{
            request.getHeaders().put("Api-Token", token);
        }catch (Exception e){
            Log.e("Error de listar", e.getMessage());
        }
        return request;
    }
*/
}
