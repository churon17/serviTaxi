package jeancarlosdev.servitaxi.Modelos;

/**
 * Clase utilizada para enviar y extraer datos del servicio en la tabla Favoritos.
 */
public class Favorito {
    /**
     * Atributo para obtener y enviar la dirección.
     */
    public String direccion;

    /**
     * Atributo para obtener y enviar la latitud.
     */
    public String latitud;

    /**
     * Atributo para obtener y enviar la longitud.
     */
    public String longitud;

    /**
     * Constructor de la clase, para inicializar sin necesidad de recibir atributos.
     * De esta manera poder manipular los atributos de la clase, en ejecución.
     */
    public Favorito() {
    }

    /**
     * Constructor de la clase, para inicializar obligado a recibir atributos.
     * De esta manera se inicializa inmediatamente la clase Conductor con todos sus atributos.
     */
    public Favorito(String direccion, String latitud, String longitud) {
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     *Devuelve el valor del atributo direccion de la clase Favorito.
     * @return Obtiene el atributo direccion, de la clase Favorito.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Cambia el atributo direccion de la clase Favorito, recibe como parametro un String
     * @param direccion este parametro va a remplazar el atributo de la clase.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     *Devuelve el valor del atributo latitud de la clase Favorito.
     * @return Obtiene el atributo latitud, de la clase Favorito.
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * Cambia el atributo latitud de la clase Favorito, recibe como parametro un String
     * @param latitud este parametro va a remplazar el atributo de la clase.
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }


    /**
     *Devuelve el valor del atributo longitud de la clase Favorito.
     * @return Obtiene el atributo longitud, de la clase Favorito.
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Cambia el atributo longitud de la clase Favorito, recibe como parametro un String
     * @param longitud este parametro va a remplazar el atributo de la clase.
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
