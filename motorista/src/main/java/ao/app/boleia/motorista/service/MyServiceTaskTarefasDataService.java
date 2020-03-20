package ao.app.boleia.motorista.service;

import ao.app.boleia.motorista.model.servicedesk.Data;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface MyServiceTaskTarefasDataService {
    @Headers("Content-Type: application/json")
    @GET("/sdpapi/request?INPUT_DATA={%22operation%22:%20{%22details%22:%20{%22from%22:%20%220%22,%22limit%22:%20%2250%22,%22filterby%22:%20%22All_Requests%22}}}%20&OPERATION_NAME=GET_REQUESTS&TECHNICIAN_KEY=B87CD848-0E50-4749-B44A-FAAA95744262&format=json")
    Call<Data> getTarefasData();
}
