package jeancarlosdev.servitaxi.Modelos;
/**
 * Clase utilizada para enviar y extraer datos en Firebase en la tabla Cliente.
 */
public class Cliente {

    /**
     * Atributo para obtener y enviar los nombres.
     */
    private String nombres;

    /**
     * Atributo para obtener y enviar el email.
     */
    private String email;

    /**
     * Atributo para obtener y enviar la contraseña.
     */
    private String password;

    /**
     * Atributo para obtener y enviar los apellidos.
     */
    private String apellidos;

    /**
     * Constructor de la clase, para inicializar sin necesidad de recibir atributos.
     * De esta manera poder manipular los atributos en ejecución con los Getter y Setter.
     */
    public Cliente() {
    }


    /**
     * Constructor de la clase, para inicializar obligado a recibir atributos.
     * De esta manera se inicializa inmediatamente la clase Cliente con todos sus atributos.
     */
    public Cliente(String nombre,
                   String email,
                   String password,
                   String apellidos) {
        this.nombres = nombre;
        this.email = email;
        this.password = password;
        this.apellidos = apellidos;
    }

    /**
     * Devuelve el valor del atributo nombres de la clase Cliente.
     * @return Obtiene el atributo nombres, de la clase Cliente.
     */
    public String getNombre() {
        return nombres;
    }

    /**
     * Cambia el atributo nombres de la clase Cliente, recibe como parametro un String
     * @param nombre este parametro va a remplazar el atributo de la clase.
     */
    public void setNombre(String nombre) {
        this.nombres = nombre;
    }

    /**
     * Devuelve el valor del atributo email de la clase Cliente.
     * @return Obtiene el atributo email, de la clase Cliente.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Cambia el atributo email de la clase Cliente, recibe como parametro un String
     * @param email este parametro va a remplazar el atributo de la clase.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el valor del atributo password de la clase Cliente.
     * @return Obtiene el atributo password, de la clase Cliente.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Cambia el atributo password de la clase Cliente, recibe como parametro un String
     * @param password este parametro va a remplazar el atributo de la clase.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Devuelve el valor del atributo apellidos de la clase Cliente.
     * @return Obtiene el atributo apellidos, de la clase Cliente.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Cambia el atributo apellidos de la clase Cliente, recibe como parametro un String
     * @param apellidos este parametro va a remplazar el atributo de la clase.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
