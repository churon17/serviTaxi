package jeancarlosdev.servitaxi.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jeancarlosdev.servitaxi.Common.Common;
import jeancarlosdev.servitaxi.Modelos.Token;
/***
 * Clase utilizada para manipular distintos métodos para alterar la BD en firebase, por medio de los Tokens.
 */
public class MyFirebaseIdService extends FirebaseInstanceIdService{

    /***
     * Sobreescribimos el método onTokenRefresh, que nos permitirá obtener el token actual.
     * A su vez invoca al método updateTokenToserver.
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refreshedToken);

    }

    /***
     * Esté método nos permitira refrescar la tabla Token en Firebase, con los token actualizados.
     * @param refreshedToken recibe como parametro un String, que será utilizado para crear un nuevo Token.
     */
    private void updateTokenToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tb1 );

        Token token = new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        }
    }
}
