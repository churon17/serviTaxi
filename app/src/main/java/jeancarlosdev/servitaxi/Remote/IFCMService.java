package jeancarlosdev.servitaxi.Remote;

import jeancarlosdev.servitaxi.Modelos.FCMResponse;
import jeancarlosdev.servitaxi.Modelos.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
/***
 * Interfaz creada para ser implementada en distintas clases que requieres implementar los métodos de la misma.
 * Toda clase que implemente está interfaz se verá obligado en reescribir todos los métodos de la misma.
 */
public interface IFCMService{
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAxCkr0cI:APA91bGKkDa-Xh5h1aZtup-DsmqmYw1baBYJoAmYBmUNeGvlV9VPTf5UKMfNXVo39LuKkc2uY6w2XN3pzvGRDQzG4RxQvVIQojMDXSVvrFW1Rb4r6HzrwXta_HAxF0zT8-jlYLV90e1A8GYDQ-1_EEx1XKmjp1CQHA"
    })
    /***
     * Método sin implementación, por ser una interfaz, que recibe un dato de Tipo Sender, para enviar el Mensaje.
     * @see Sender
     */
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
