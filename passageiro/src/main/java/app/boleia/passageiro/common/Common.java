package app.boleia.passageiro.common;

import app.boleia.passageiro.model.Passageiros;
import app.boleia.passageiro.remote.FCMClient;
import app.boleia.passageiro.remote.IFCMService;
import app.boleia.passageiro.remote.IGoogleAPI;
import app.boleia.passageiro.remote.RetrofitClient;

/**
 * Created by maike.silva on 27/12/2017.
 */

public class Common {

    //PRODUCAO
  /*  public static final String online_tbl = "/application/online";
    public static final String utilizador_tbl = "/application/utilizadores";
    public static final String viagens_tbl = "/application/viagens";
    public static final String token_tbl = "/application/tokens";
    public static final String rotas = "/application/rotas";
    public static final String automoveis_tbl = "/application/automoveis";
    public static final String deploy = "/application/deploy";
    public static final String do_status_automovel_tbl = "/application/do_statusautomovel";*/

    //HML
    public static final String online_tbl = "/hml/online";
    public static final String utilizador_tbl = "/hml/utilizadores";
    public static final String viagens_tbl = "/hml/viagens";
    public static final String token_tbl = "/hml/tokens";
    public static final String rotas = "/hml/rotas";
    public static final String automoveis_tbl = "/hml/automoveis";
    public static final String deploy = "/hml/deploy";
    public static final String do_status_automovel_tbl = "/hml/do_statusautomovel";

    public static final int PICK_IMAGE_REQUEST = 9999;

    public static Passageiros currentUser;

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
