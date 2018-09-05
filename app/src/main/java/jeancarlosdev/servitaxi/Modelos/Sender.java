package jeancarlosdev.servitaxi.Modelos;

/***
 * Clase utilizada para enviar la notificación, actúa como remitente.
 */
public class Sender {
    /***
     * Atributo utilizado para guardar el destino de la Notificacion, en nuestro caso el Token
     */
    public String to;
    /***
     * Atributo de la clase Sender de tipo Notification utilizado para guardar la notificacion.
     * @see Notification
     */
    public Notification notification;

    /***
     * Constructor de la clase, para inicializar un objeto de la clase Sender.
     * Este constructor no recibe parametros.
     */
    public Sender() {
    }
    /***
     * Constructor de la clase, para inicializar un objeto de la clase Sender.
     * Este constructor recibe como parametro un String y un Objeto de la clase Notification.
     * @param to este parametro va a ser utilizado para asignarla al atributo de la clase to.
     * @param notification este parametro va ser utilizado para asignarla al atributo de la clase notification.
     * @see Notification
     */
    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    /**
     *  Devuelve un valor de tipo String correspondiente al valor del atributo to.
     * @return Obtiene el atributo to, de la clase Notification.
     */
    public String getTo() {
        return to;
    }

    /**
     * Cambia el atributo to de la clase Sender, recibe como parametro un dato de tipo String
     * @param to este parametro va a remplazar el atributo de la clase.
     */
    public void setTo(String to) {
        this.to = to;
    }


    /**
     * Devuelve un valor de tipo Notification correspondiente al atributo notification.
     * @return Obtiene el objeto o atributo notificcation, de la clase Sender.
     * @see Notification
     */
    public Notification getNotification() {
        return notification;
    }

    /**
     * Cambia el atributo notificacion de la clase Sender, recibe como parametro un dato de tipo Notification
     * @param notification este parametro va a remplazar el atributo de la clase.
     * @see Notification
     */
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
