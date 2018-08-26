package jeancarlosdev.servitaxi.Remote;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import jeancarlosdev.servitaxi.Utilidades.Utilidades;


public class VolleyPeticion<T> extends Request<T> {

    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String CONTENT_TYPE = "application/vnd.api-v1+json";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private String postData = null;
    private Listener<T> listener;
    private byte[] rawData = null;
    private String typeRawData = "";
    private Map<String, String> headers = new HashMap<>();
    private final Gson gson = new Gson();
    private Context context;

    private Class<T> clazz;
    private int mHttpCode = Integer.MIN_VALUE;

    public VolleyPeticion(Context context, int method, String url, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        initialHeader("");
        this.listener = listener;
    }

    public VolleyPeticion(Context context, int method, String url, JSONObject param, Class<T> clazz, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        this.listener = listener;
        this.clazz = clazz;
        initialHeader("");
        setPostData(param);
    }

    public VolleyPeticion(Context context, int method, String url, String param, Class<T> clazz, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.context = context;
        this.clazz = clazz;
        initialHeader("");
        setPostData(param);
    }

    public VolleyPeticion(Context context, int method, String url, Object postObject, Class objectType, Class<T> clazz, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        this.listener = listener;
        this.clazz = clazz;
        initialHeader("");
        setPostData(postObject, objectType);
    }

    public VolleyPeticion(Context context, int method, String url, Object postObject, Class objectType, Class<T> clazz, String authorization, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        this.listener = listener;
        this.clazz = clazz;
        initialHeader(authorization);
        setPostData(postObject, objectType);
    }

    public VolleyPeticion(Context context, int method, String url, JSONObject paramJson, Class<T> clazz, String authorization, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.context = context;
        this.clazz = clazz;
        initialHeader(authorization);
        setPostData(paramJson);
    }

    public VolleyPeticion(Context context, int method, String url, String paramString, Class<T> clazz, String authorization, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        this.listener = listener;
        this.clazz = clazz;
        initialHeader(authorization);
        setPostData(paramString);
    }

    public VolleyPeticion(Context context, int method, String url, Class<T> clazz, String authorization, Listener<T> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = context;
        this.listener = listener;
        this.clazz = clazz;
        initialHeader(authorization);
        setPostData("");
    }

    public void initialHeader(String authorization) {
        setTag(getUrl());
        setRetryPolicy(new DefaultRetryPolicy(30000,
                /*DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/ 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept-Encoding", "UTF-8");

        headers.put("Content-Type", "application/json");

        setAuthorization(authorization);
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public String getMethodName() {
        int method = getMethod();
        if (method == Method.DELETE) return "DELETE";
        if (method == Method.GET) return "GET";
        if (method == Method.POST) return "POST";
        if (method == Method.PUT) return "PUT";
        if (method == Method.PATCH) return "PATCH";
        return "Unknown";
    }

    public VolleyPeticion setResponseClass(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public VolleyPeticion setAuthorization(String authorization) {
        if (Utilidades.isNotEmpty(authorization))
            headers.put("Authorization", authorization);
        return this;
    }

    public VolleyPeticion setPostData(String postData) {
        this.postData = postData;
        return this;
    }

    public VolleyPeticion setPostData(Object postObject, Class inputType) {
        postData = gson.toJson(postObject, inputType);
        return this;
    }

    public VolleyPeticion setPostData(Object postObject, Type inputType) {
        postData = gson.toJson(postObject, inputType);
        return this;
    }

    public VolleyPeticion setRawData(byte[] rawData, String type) {
        this.rawData = rawData;
        this.typeRawData = type;
        headers.put("Content-Type", type);
        return this;
    }

    public VolleyPeticion setPostData(JSONObject jsonObject) {
        if (jsonObject != null) {
            postData = jsonObject.toString();
        }
        return this;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (rawData != null) {
            return rawData;
        } else {
            if (postData != null) {
                try {
                    return postData.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    return postData.getBytes();
                }
            }
        }
        return super.getBody();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        mHttpCode = response == null ? -1 : response.statusCode;
        return VolleyProcesadorResultado.parseNetworkResponse(response, clazz);
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mHttpCode = error == null ? -2 :
                error.networkResponse == null ? -3 : error.networkResponse.statusCode;
        super.deliverError(error);
    }

    @Override
    public String getBodyContentType() {
        if (rawData != null) {
            return typeRawData;
        } else {
            if (getMethod() != Method.PATCH)
                return "application/json";
            else
                return " ";
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public String requestToString() {
        String str = "\n>Method: " + getMethodName();
        str += "\n\n>Url: " + getUrl();
        str += "\n\n>Headers: ";
        try {
            Map<String, String> headers = getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                str += "\n-" + entry.getKey() + ": " + entry.getValue();
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }


        return str;
    }
}