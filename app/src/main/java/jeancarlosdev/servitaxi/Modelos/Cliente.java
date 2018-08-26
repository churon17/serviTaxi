package jeancarlosdev.servitaxi.Modelos;

/***
 * Modelo del cliente con sus atributos
 */

public class Cliente {

    private String nombres;

    private String email;

    private String password;

    private String apellidos;

    public Cliente() {
    }

    public Cliente(String nombre,
                   String email,
                   String password,
                   String apellidos) {
        this.nombres = nombre;
        this.email = email;
        this.password = password;
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombres;
    }

    public void setNombre(String nombre) {
        this.nombres = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
