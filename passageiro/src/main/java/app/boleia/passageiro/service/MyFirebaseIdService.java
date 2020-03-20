package app.boleia.passageiro.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.model.Token;


/**
 * Created by maike.silva on 28/12/2017.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String refresheToken = FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refresheToken);
    }

    private void updateTokenToServer(String refresheToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(refresheToken);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        }
    }
}
