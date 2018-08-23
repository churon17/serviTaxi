package jeancarlosdev.servitaxi.Modelos;

public class Cliente {

    private String nombre;

    private String email;

    private String password;

    private String telefono;

    public Cliente() {
    }

    public Cliente(String nombre,
                   String email,
                   String password,
                   String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
