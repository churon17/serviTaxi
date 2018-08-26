package jeancarlosdev.servitaxi.Common;

import jeancarlosdev.servitaxi.Remote.FCMClient;
import jeancarlosdev.servitaxi.Remote.IFCMService;

/***
 * Variables en común del proyecto para un fácil mantenimiento
 */

public class Common {

    public static final String clientes_tb1 = "Clientes";
    public static final String conductor_tb1 = "Conductores";
    public static final String drivers_tb1 = "Drivers";
    public static final String solicitud_tb1 = "Solicitud";
    public static final String token_tb1 = "Tokens";

    public static final String fcmUrl = "https:/fcm.googleapis.com/";
    public static String cliente =  "usr";
    public static String password = "pwd";
    public static String nombre =  "name";
    public static String imagen =  "img";




    public static IFCMService getFCMService(){

        return FCMClient.getClient(fcmUrl).create(IFCMService.class);
    }
}
