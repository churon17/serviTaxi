package jeancarlosdev.servitaxi.Modelos;

/***
 * Modelo Favoritos con sus atributos
 */
public class Favorito {

    public String direccion;

    public String latitud;

    public String longitud;

    public Favorito() {
    }

    public Favorito(String direccion, String latitud, String longitud) {
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
