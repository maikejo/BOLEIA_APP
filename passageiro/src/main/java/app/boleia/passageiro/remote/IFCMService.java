package app.boleia.passageiro.remote;

import app.boleia.passageiro.model.FCMResponse;
import app.boleia.passageiro.model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by maike.silva on 28/12/2017.
 */

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAEeqUfLY:APA91bFqfNuqW1QAGvulN9YQB07kKhUauvzwSwM8iQJM2EIeq6IdPI_TdXr6-lLYLD6I9csKf_go7nEd8G4UlW7SP6mFUBNEPirQGQART8w_F52wk1z5yu7XKPp14-qzNEqwqB8jd-FG"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
