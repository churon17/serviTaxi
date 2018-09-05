package jeancarlosdev.servitaxi.Modelos;

/***
 * Clase utilizada para la respuesta de FCM(Firebase Cloud Messaging).
 */
public class Result {
    /***
     * Atributo de la clase Result utilizado para guardar un String(mensaje del resultado).
     */
    public String message_id;


    /***
     * Constructor de la clase, para inicializar un objeto de la clase Result.
     * Este constructor no recibe parametros.
     */
    public Result() {
    }

    /***
     * Constructor de la clase, para inicializar un objeto de la clase Result.
     * Este constructor recibe como parametro un String.
     * @param message_id este parametro va a ser utilizado para asignarla al atributo de la clase message_id.
     */
    public Result(String message_id) {
        this.message_id = message_id;
    }

    /**
     * Devuelve un valor de tipo String message_id.
     * @return Obtiene el atributo message_id, de la clase Result.
     */
    public String getMessage_id() {
        return message_id;
    }

    /**
     * Cambia el atributo message_id de la clase Result, recibe como parametro un dato de tipo String
     * @param message_id este parametro va a remplazar el atributo de la clase.
     */
    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
