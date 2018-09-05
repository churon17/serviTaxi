package jeancarlosdev.servitaxi.Modelos;

/***
 * Clase utilizada para almacenar el Token obtenido en distintos puntos del Programa, para notificaciones, BD, entre otros.
 */
public class Token {

    /***
     * Atributo utilizado para guardar el token.
     * Tipo de dato de tipo String
     */
    private String token;


    /***
     * Constructor de la clase, para inicializar un objeto de la clase Token.
     * Este constructor no recibe parametros.
     */
    public Token() {
    }

    /***
     * Constructor de la clase, para inicializar un objeto de la clase Token.
     * Este constructor recibe como parametro un String
     * @param token este parametro va a ser utilizado para asignarla al atributo de la clase to.
     */
    public Token(String token) {
        this.token = token;
    }


    /**
     * Devuleve el valor de tipo String correspondiente al valor del atributo token.
     * @return Obtiene el atributo token, de la clase Token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Cambia el atributo body_id de la clase Notification, recibe como parametro un dato de tipo String
     * @param token este parametro va a remplazar el atributo de la clase.
     */
    public void setToken(String token) {
        this.token = token;
    }
}
