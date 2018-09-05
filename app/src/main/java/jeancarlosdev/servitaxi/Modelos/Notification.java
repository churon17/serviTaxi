package jeancarlosdev.servitaxi.Modelos;

/***
 * Clase utilizada para darle cuerpo a la notificación que recibira el cliente.
 */
public class Notification {

    /***
     * Atributo utilizado para guardar el título de la Notificacion.
     */
    public String title;

    /***
     * Atributo utilizado para guardar el cuerpo o descripción de la Notificacion.
     */
    public String body;

    /***
     * Constructor de la clase, para inicializar un objeto de la clase Notification.
     * Este constructor no recibe parametros.
     */
    public Notification() {
    }

    /***
     * Constructor de la clase, para inicializar un objeto de la clase Notification.
     * Este constructor recibe como parametro dos String.
     * @param title este parametro va a ser utilizado para asignarla al atributo de la clase message_id.
     * @param body este parametro va ser utilizado para asignarla al atributo de la clase body.
     */
    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    /**
     * Devuelve un valor de tipo String correspondiente al atributo title.
     * @return Obtiene el atributo title, de la clase Notification.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Cambia el atributo title de la clase Notification, recibe como parametro un dato de tipo String
     * @param title este parametro va a remplazar el atributo de la clase.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve un valor de tipo String correspondiente al atributo body.
     * @return Obtiene el atributo body, de la clase Notification.
     */
    public String getBody() {
        return body;
    }

    /**
     * Cambia el atributo body_id de la clase Notification, recibe como parametro un dato de tipo String
     * @param body este parametro va a remplazar el atributo de la clase.
     */
    public void setBody(String body) {
        this.body = body;
    }
}
