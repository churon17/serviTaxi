package jeancarlosdev.servitaxi.Common;

import jeancarlosdev.servitaxi.Remote.FCMClient;
import jeancarlosdev.servitaxi.Remote.IFCMService;

/***
 * Clase utilizada para reutilizar variables estáticas en distintas clases.
 * Estas variables son creadas aquí, para una mayor manipulación de las mismas.
 */
public class Common {

    /***
     * Variable estática  utilizada para hacer referencia al nombre de la tabla Clientes en Firebase.
     */
    public static final String clientes_tb1 = "Clientes";
    /***
     * Variable estática  utilizada para hacer referencia al nombre de la tabla Conductores en Firebase.
     */
    public static final String conductor_tb1 = "Conductores";
    /***
     * Variable estática  utilizada para hacer referencia al nombre de la tabla Drivers en Firebase.
     */
    public static final String drivers_tb1 = "Drivers";

    /***
     * Variable estática  utilizada para hacer referencia al nombre de la tabla Solicitud en Firebase.
     */
    public static final String solicitud_tb1 = "Solicitud";

    /***
     * Variable estática  utilizada para hacer referencia al nombre de la tabla Tokens en Firebase.
     */
    public static final String token_tb1 = "Tokens";

    /***
     * Variable estática utilizada para notificaciones en FIREBASE CLOUD MESSAGING
     * Guarda la URL base para consultar FIREBASE CLOUD MESSAGING.
     */
    public static final String fcmUrl = "https:/fcm.googleapis.com/";
    /***
     * Variable estática utilizada para guardar en la BD del telefono el valor del correo electrónico del cliente, previamente Logueado.
     */
    public static String cliente =  "usr";
    /***
     * Variable estática utilizada para guardar en la BD del telefono el valor de la contraseña del cliente, previamente Logueado.
     */
    public static String password = "pwd";
    /***
     * Variable estática utilizada para guardar en la BD del telefono el valor del nombre del cliente, previamente Logueado.
     */
    public static String nombre =  "name";

    /***
     * Variable estática utilizada para guardar en la BD del telefono el valor de la URL, en caso se haya Logueado con Facebook del conductor, previamente Logueado.
     */
    public static String imagen =  "img";

    /***
     * Variable estática utilizada para guardar en la BD del telefono el valor de la external_Id, previamente Logueado.
     * Esta variable external_id será utilizada para manipular métodos de solicitud al servidor con Volley.
     */
    public static String external_id =  "external_id";


    /***
     * Método estático para realizar una instancia de IFCMservice, sigue el modelo SINGLETON.
     */
    public static IFCMService getFCMService(){

        return FCMClient.getClient(fcmUrl).create(IFCMService.class);
    }
}
