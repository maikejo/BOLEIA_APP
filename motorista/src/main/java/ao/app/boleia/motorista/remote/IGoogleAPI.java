package ao.app.boleia.motorista.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by maike.silva on 26/12/2017.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);
}
