package jeancarlosdev.servitaxi.Modelos;

import java.util.List;
/***
 * Clase utilizada para la respuesta de FIREBASE CLOUD MESSAGING
 */
public class FCMResponse {

    /***
     * Variable utilizada para extraer el id de la respuesta de FCM.
     */
    public long multicast_id;
    /***
     * Variable utilizada para extraer un entero de la respuesta de FCM.
     * Este entero nos indicara si es que la petición ha sido exitosa.
     */
    public int success;

    /***
     * Variable utilizada para extraer un entero de la respuesta de FCM.
     * Este entero nos indicara si es que la petición ha fallado .
     */
    public int failure;


    public int canonical_ids;

    /***
     * Variable utilizada para extraer la respuesta de FCM.
     * Este variable de tipo List guardara todos los resultados obtenidos en la petición.
     */
    public List<Result> results;

    /***
     * Constructor de la clase.
     * Para instanciar un objeto de tipo FCMResponse, sin necesidad de enviar parametros.
     */
    public FCMResponse() {

    }

    /***
     * Constructor de la clase.
     * Para instanciar un objeto de tipo FCMResponse, es necesario enviar todos los parametros.
     * @param multicast_id
     * @param success
     * @param failure
     * @param canonical_ids
     * @param results
     * Se inicializara un objeto de la clase FCMResponse con todos sus atributos inicializados.
     */
    public FCMResponse(long multicast_id, int success, int failure, int canonical_ids, List<Result> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }
    /**
     * Devuelve el valor del atributo multicast_id
     * @return Obtiene el atributo multicast_id, de la clase FCMResponse.
     */
    public long getMulticast_id() {
        return multicast_id;
    }

    /**
     * Cambia el atributo multicast_id de la clase FCMResponse, recibe como parametro un dato de tipo Long
     * @param multicast_id este parametro va a remplazar el atributo de la clase.
     */
    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    /**
     *  Devuelve el valor del atributo success.
     * @return Obtiene el atributo success, de la clase FCMResponse.
     */
    public int getSuccess() {
        return success;
    }

    /**
     * Cambia el atributo success de la clase FCMResponse, recibe como parametro un dato de tipo int
     * @param success este parametro va a remplazar el atributo de la clase.
     */
    public void setSuccess(int success) {
        this.success = success;
    }
    /**
     * Devuelve el valor del atributo failure.
     * @return Obtiene el atributo failure, de la clase FCMResponse.
     */
    public int getFailure() {
        return failure;
    }

    /**
     * Cambia el atributo failure de la clase FCMResponse, recibe como parametro un dato de tipo int
     * @param failure este parametro va a remplazar el atributo de la clase.
     */
    public void setFailure(int failure) {
        this.failure = failure;
    }

    /**
     * Devuelve el valor del atributo canonical_ids.
     * @return Obtiene el atributo canonical_ids, de la clase FCMResponse.
     */
    public int getCanonical_ids() {
        return canonical_ids;
    }

    /**
     * Cambia el atributo canonical_ids de la clase FCMResponse, recibe como parametro un dato de tipo int
     * @param canonical_ids este parametro va a remplazar el atributo de la clase.
     */
    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    /**
     * Devuelve una lista de tipo Result.
     * @return Obtiene el atributo results, de la clase FCMResponse.
     * @see Result
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * Cambia el atributo results de la clase FCMResponse, recibe como parametro un dato de tipo List<Result>
     * @param results este parametro va a remplazar el atributo de la clase.
     * @see Result
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }
}
