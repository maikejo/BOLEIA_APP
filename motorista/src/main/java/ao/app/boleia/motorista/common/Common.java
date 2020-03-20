package ao.app.boleia.motorista.common;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.User;
import ao.app.boleia.motorista.remote.FCMClient;
import ao.app.boleia.motorista.remote.IFCMService;
import ao.app.boleia.motorista.remote.IGoogleAPI;
import ao.app.boleia.motorista.remote.RetrofitClient;

/**
 * Created by maike.silva on 26/12/2017.
 */

public class Common {


    //PROD
   /* public static final String online_tbl = "/application/online";
    public static final String user_motorista_tbl = "/application/motorista";
    public static final String utilizador_tbl = "/application/utilizadores";
    public static final String token_tbl = "/application/tokens";
    public static final String automoveis_tbl = "/application/automoveis";
    public static final String notificacao_tbl = "/application/notificacao";
    public static final String viagens_tbl = "/application/viagens";
    public static final String rotas_tbl = "/application/rotas";
    public static final String categoria_tbl = "/application/categorias";
    public static final String dispositivo_tbl = "/application/dispositivos";
    public static final String do_status_automovel_tbl = "/application/do_statusautomovel";
    public static final String logAutomovel_tbl = "/application/logautomovel";
    public static final String recarga_tbl = "/application/recargas";*/



    //HML
    public static final String online_tbl = "/hml/online";
    public static final String user_motorista_tbl = "/hml/motorista";
    public static final String utilizador_tbl = "/hml/utilizadores";
    public static final String token_tbl = "/hml/tokens";
    public static final String automoveis_tbl = "/hml/automoveis";
    public static final String notificacao_tbl = "/hml/notificacao";
    public static final String viagens_tbl = "/hml/viagens";
    public static final String rotas_tbl = "/hml/rotas";
    public static final String categoria_tbl = "/hml/categorias";
    public static final String dispositivo_tbl = "/hml/dispositivos";
    public static final String do_status_automovel_tbl = "/hml/do_statusautomovel";
    public static final String logAutomovel_tbl = "/hml/logautomovel";
    public static final String recarga_tbl = "/hml/recargas";


    public static final DatabaseReference dbReferenceAutomovel =  FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
    public static final DatabaseReference dbReferenceUtilizador =  FirebaseDatabase.getInstance().getReference().child(Common.utilizador_tbl);
    public static final DatabaseReference dbReferenceViagens =  FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
    public static final DatabaseReference dbReferenceDispositivo =  FirebaseDatabase.getInstance().getReference().child(Common.dispositivo_tbl);
    public static final DatabaseReference dbReferenceRotas =  FirebaseDatabase.getInstance().getReference().child(Common.rotas_tbl);
    public static final DatabaseReference dbReferenceLogViagem =  FirebaseDatabase.getInstance().getReference().child(Common.logAutomovel_tbl);
    public static final DatabaseReference dbReferenceRecarga =  FirebaseDatabase.getInstance().getReference().child(Common.recarga_tbl);




    public static final int PICK_IMAGE_REQUEST = 9999;

    public static User currentUser;
    public static Automovel currentAutomovel;

    public static Location mLastLocation = null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String user_field = "usr";
    public static final String pwd_field = "pwd";

    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
