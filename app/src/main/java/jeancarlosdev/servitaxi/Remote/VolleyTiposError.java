package jeancarlosdev.servitaxi.Remote;

import android.os.Parcelable;
import android.os.Parcel;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import jeancarlosdev.servitaxi.Utilidades.Utilidades;

public class VolleyTiposError implements Parcelable {
    public String errorCode = ERR_UNKNOWN;
    public String errorTitle = "";
    public String errorMessage = "";

    public String messageTitle = "";
    public String messageBody = "";
    public String messageMore = "";

    public int httpCode = -1;
    public long networkTimeMs = -1;

    //Server-defined API error codes
    static public final String ERR_ACCOUNT_DUPLICATE = "ERR_ACCOUNT_DUPLICATE";
    static public final String ERR_ACCOUNT_NOT_EXISTS = "ERR_ACCOUNT_NOT_EXISTS";
    static public final String ERR_ACCOUNT_IMAGE_NOT_AVAILABLE = "ERR_ACCOUNT_IMAGE_NOT_AVAILABLE";
    static public final String WRN_ACCOUNT_NOT_VALIDATED = "WRN_ACCOUNT_NOT_VALIDATED";
    static public final String ERR_ACCOUNT_DUPLICATE_FB_ACCOUNT_EXISTS = "ERR_ACCOUNT_DUPLICATE_FB_ACCOUNT_EXISTS";
    static public final String ERR_ACCOUNT_DUPLICATE_EMAIL_ACCOUNT_EXISTS = "ERR_ACCOUNT_DUPLICATE_EMAIL_ACCOUNT_EXISTS";
    static public final String ERR_ACCOUNT_CREDENTIALS_NOT_CORRECT = "ERR_ACCOUNT_CREDENTIALS_NOT_CORRECT";
    static public final String ERR_ACCOUNT_PASSWORD_REQUEST_EXPIRED = "ERR_ACCOUNT_PASSWORD_REQUEST_EXPIRED ";
    static public final String ERR_ACCOUNT_PASSWORD_REQUEST_NOT_EXISTS = "ERR_ACCOUNT_PASSWORD_REQUEST_NOT_EXISTS";

    static public final String ERR_BAD_REQUEST_DATA = "ERR_BAD_REQUEST_DATA";
    static public final String ERR_UNKNOWN = "ERR_UNKNOWN";
    static public final String ERR_PAYMENT_INSUFFICIENT_FUND = "ERR_PAYMENT_INSUFFICIENT_FUND";
    static public final String ERR_ORDER_EXPIRED_DRAW = "ERR_ORDER_EXPIRED_DRAW";
    static public final String ERR_LOCATION_REQUIRED = "ERR_LOCATION_REQUIRED";
    static public final String ERR_MAXIMUM_WALLET_AMOUNT_EXCEEDED = "ERR_MAXIMUM_WALLET_AMOUNT_EXCEEDED";
    static public final String ERR_GIFT_MAXIMUM_MESSAGE_LENGTH_EXCEEDED = "ERR_GIFT_MAXIMUM_MESSAGE_LENGTH_EXCEEDED ";
    static public final String ERR_GIFT_CLAIMED = "ERR_GIFT_CLAIMED";
    static public final String ERR_CLUSTER_INVALID = "ERR_CLUSTER_INVALID";
    static public final String ERR_DRAW_INVALID = "ERR_DRAW_INVALID";
    static public final String ERR_GAME_INVALID = "ERR_GAME_INVALID";

    //Client-defined error codes
    static public final String ERROR_INTERNAL = "ERROR_INTERNAL";
    static public final String ERR_REQUEST_TIMEOUT = "ERR_REQUEST_TIMEOUT";
    static public final String ERR_NETWORK_CONNECTIVITY = "ERR_NETWORK_CONNECTIVITY";
    static public final String ERR_INVALID_RESPONSE = "ERR_INVALID_RESPONSE";

    /**
     * Error de conexion
     * @return VolleyTiposError
     */
    public static VolleyTiposError createVolleyErrorNoNetwork() {
        VolleyTiposError dataError = new VolleyTiposError();
        dataError.httpCode = 200;
        dataError.errorCode = ERR_NETWORK_CONNECTIVITY;

        return dataError;
    }

    /**
     * Error en la aplicacion
     * @param msgTitle Titulo del error
     * @param msgContent Mensaje de error
     * @return
     */
    public static VolleyTiposError createVolleyError(String msgTitle, String msgContent) {
        VolleyTiposError dataError = new VolleyTiposError();
        dataError.httpCode = -1;
        dataError.errorCode = ERR_UNKNOWN;
        dataError.errorTitle = msgTitle;
        dataError.errorMessage = msgContent;

        return dataError;
    }

    public VolleyTiposError() {

    }

    public VolleyTiposError(VolleyError error) {
        this(error, null);
    }

    public VolleyTiposError(VolleyError error, Request request) {
        VolleyTiposError dataError = VolleyProcesadorResultado.parseErrorResponse(error);
        errorCode = dataError.errorCode;
        errorTitle = dataError.errorTitle;
        errorMessage = dataError.errorMessage;

        messageTitle = dataError.messageTitle;
        if (Utilidades.isNotEmpty(messageTitle))
            messageTitle = errorTitle;
        messageBody = dataError.messageBody;
        httpCode = dataError.httpCode;
        networkTimeMs = dataError.networkTimeMs;

        if (request != null) {
            messageBody = "RESULT ===================== ";
            messageBody += "\n- Error code: " + errorCode;
            messageBody += "\n- Http code: " + httpCode;
            messageBody += "\n- Error msg: " + errorMessage;
            //messageBody += "\n- Time: " + networkTimeMs + "ms";

            messageBody += "\n\nREQUEST ===================== ";

            if (request instanceof VolleyPeticion) {
                messageBody += ((VolleyPeticion) request).requestToString();
            } //else if (request instanceof GsonRequest) {
            //messageBody += new Gson().toJson(request);//((Gson) request).requestToString();
            //}
        }
    }

    protected VolleyTiposError(Parcel in) {
        errorCode = in.readString();
        errorTitle = in.readString();
        errorMessage = in.readString();
        messageTitle = in.readString();
        messageBody = in.readString();
        messageMore = in.readString();
        httpCode = in.readInt();
        networkTimeMs = in.readLong();
    }

    public void setMessageMore(String messageMore) {
        this.messageMore = messageMore;
    }

    public static final Creator<VolleyTiposError> CREATOR = new Creator<VolleyTiposError>() {
        @Override
        public VolleyTiposError createFromParcel(Parcel in) {
            return new VolleyTiposError(in);
        }

        @Override
        public VolleyTiposError[] newArray(int size) {
            return new VolleyTiposError[size];
        }
    };

    @Override
    public String toString() {
        return "(" + errorCode + "," + httpCode + "," + /*networkTimeMs +"ms: " +*/ errorMessage + ")";
    }

    public boolean isGeneralError() {
        return ERR_UNKNOWN.equals(errorCode) ||
                ERROR_INTERNAL.equals(errorCode) ||
                ERR_REQUEST_TIMEOUT.equals(errorCode);
    }

    public boolean isErrorResetPasswordInvalid() {
        return ERR_ACCOUNT_PASSWORD_REQUEST_EXPIRED.equals(errorCode) ||
                ERR_BAD_REQUEST_DATA.equals(errorCode) ||
                ERR_ACCOUNT_NOT_EXISTS.equals(errorCode) ||
                ERR_ACCOUNT_PASSWORD_REQUEST_NOT_EXISTS.equals(errorCode);
    }

    public boolean isErrorRegisterAccountInvalid() {
        return ERR_ACCOUNT_DUPLICATE_EMAIL_ACCOUNT_EXISTS.equals(errorCode) ||
                ERR_ACCOUNT_DUPLICATE_FB_ACCOUNT_EXISTS.equals(errorCode) ||
                ERR_ACCOUNT_DUPLICATE.equals(errorCode);
    }

    public boolean isErrorNetworkConnection() {
        return ERR_NETWORK_CONNECTIVITY.equals(errorCode);
    }

    public boolean isErrorInvalidResponse() {
        return ERR_INVALID_RESPONSE.equals(errorCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorCode);
        dest.writeString(errorTitle);
        dest.writeString(errorMessage);
        dest.writeString(messageTitle);
        dest.writeString(messageBody);
        dest.writeString(messageMore);
        dest.writeInt(httpCode);
        dest.writeLong(networkTimeMs);
    }
}