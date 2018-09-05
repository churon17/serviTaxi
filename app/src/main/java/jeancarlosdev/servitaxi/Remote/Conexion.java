package jeancarlosdev.servitaxi.Remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;

import jeancarlosdev.servitaxi.Modelos.AllCliente;
import jeancarlosdev.servitaxi.Modelos.ClienteBackJson;
import jeancarlosdev.servitaxi.Modelos.Favorito;
import jeancarlosdev.servitaxi.Modelos.MensajeBackJson;
/***
 * Clase utilizada para realizar las peticiones al servidor.
 */
public class Conexion {

    /***
     * Atributo utilizado para guardar la URL base donde se encuentra el Backend de nuestra Aplicación.
     */
    private final static String API_URL = "https://servitaxi.000webhostapp.com/ServicioWEB/index.php/";

    /***
     * Método que nos permitirá iniciar sesión de parte del cliente de la Aplicación ServiTaxi.
     * @param contexto recibe el contexto actual donde va a ser llamado este método.
     * @param mapa recibe un dato de Tipo HashMap<String, String> que contendrá los datos necesarios para enviar al servidor.
     * @param response_Listener recibe un dato Response.Listener<ClienteBackJson> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return un tipo de dato VolleyPeticion<ClienteBackJson> donde se almacenará la respuesta del servidor.
     * @see ClienteBackJson
     * @see HashMap
     */
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

    /***
     * Método que nos permitira registrar un nuevo Cliente en nuestro Servicio.
     * Es necesario que el Cliente tenga una cuenta, para poder manipular las distintas opciones de serviTaxi.
     * @param contexto  recibe el contexto actual donde va a ser llamado este método.
     * @param mapa  recibe un dato de Tipo HashMap<String, String> que contendrá los datos necesarios para enviar al servidor.
     * @param response_Listener recibe un dato Response.Listener<MensajeBackJson> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return un tipo de dato VolleyPeticion<MensajeBackJson> donde se almacenará la respuesta del servidor.
     * @see MensajeBackJson
     * @see HashMap
     */
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

    /***
     * Método que nos devolvera un arreglo de Favoritos, este arreglo de Favoritos nos servirá al momento de mostrar Favoritos al Cliente.
     * @param context  recibe el contexto actual donde va a ser llamado este método.
     * @param responseListener recibe un dato Response.Listener<Favoritos[]> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return Favoritos[], un arreglo de Favortos para ser Listadas.
     * @see Favorito
     * @see HashMap
     */

    public static VolleyPeticion<Favorito[]> listarFavoritos(
            @NonNull final Context context,
            @NonNull final String id,
            @NonNull Response.Listener<Favorito[]> responseListener,
            @NonNull Response.ErrorListener errorListener
    ){
        final String url = API_URL + "cliente/favoritos/listarFavoritos/" + id;
        VolleyPeticion request = new VolleyPeticion(
                context,
                Request.Method.GET,
                url,
                responseListener,
                errorListener
        );
        request.setResponseClass(Favorito[].class);
        return request;
    }

    /***
     * Método que nos permitirá registrar un Favorito para el cliente.
     * @param contexto recibe el contexto actual donde va a ser llamado este método.
     * @param mapa recibe un dato de Tipo HashMap<String, String> que contendrá los datos necesarios para enviar al servidor.
     * @param response_Listener recibe un dato Response.Listener<MensajeBackJson> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return un tipo de dato VolleyPeticion<MensajeBackJson> donde se almacenará la respuesta del servidor.
     * @see MensajeBackJson
     * @see HashMap
     */
    public static VolleyPeticion<MensajeBackJson> registrarFavorito(@NonNull final Context contexto,
                                                                   @NonNull final HashMap mapa,
                                                                   @NonNull Response.Listener<MensajeBackJson> response_Listener,
                                                                   @NonNull Response.ErrorListener errorListener){

        final String url = API_URL + "cliente/favoritos/guardar";

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


    /***
     * Método que nos permitirá cambiar la contraseña del cliente en el servicio.
     * @param contexto recibe el contexto actual donde va a ser llamado este método.
     * @param mapa recibe un dato de Tipo HashMap<String, String> que contendrá los datos necesarios para enviar al servidor.
     * @param response_Listener recibe un dato Response.Listener<MensajeBackJson> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return un tipo de dato VolleyPeticion<MensajeBackJson> donde se almacenará la respuesta del servidor.
     * @see MensajeBackJson
     * @see HashMap
     */
    public static VolleyPeticion<MensajeBackJson> cambiarContrasena(@NonNull final Context contexto,
                                                                    @NonNull final HashMap mapa,
                                                                    @NonNull final String external,
                                                                    @NonNull Response.Listener<MensajeBackJson> response_Listener,
                                                                    @NonNull Response.ErrorListener errorListener){

        final String url = API_URL + "cliente/editar/" +external;

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

    /***
     * Método que nos permitirá retornar un Cliente completo, con todos sus Atributos.
     * Estos atributos almacenados en la clase AllCliente, nos ayudará para poder manipular el cambio de contraseña.
     * @param contexto recibe el contexto actual donde va a ser llamado este método.
     * @param mapa recibe un dato de Tipo HashMap<String, String> que contendrá los datos necesarios para enviar al servidor.
     * @param response_Listener recibe un dato Response.Listener<AllCliente> para recibir la respuesta del servidor.
     * @param errorListener recibe un dato Response.ErrorListener, para detectar un error en caso que la petición falle.
     * @return un tipo de dato VolleyPeticion<AllCliente> donde se almacenará la respuesta del servidor.
     * @see   AllCliente
     * @see HashMap
     */
    public static VolleyPeticion<AllCliente> retornarCliente(@NonNull final Context contexto,
                                                               @NonNull final HashMap mapa,
                                                               @NonNull Response.Listener<AllCliente> response_Listener,
                                                               @NonNull Response.ErrorListener errorListener){

        final String url = API_URL + "cliente/devolverCliente";

        VolleyPeticion request = new VolleyPeticion(contexto,
                Request.Method.POST,
                url,
                mapa,
                HashMap.class,
                String.class,
                response_Listener,
                errorListener);

        request.setResponseClass(AllCliente.class);

        return request;
    }


}
