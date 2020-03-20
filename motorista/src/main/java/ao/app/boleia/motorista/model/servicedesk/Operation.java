package ao.app.boleia.motorista.model.servicedesk;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Operation {

    @SerializedName("module")
    private String module;

    @SerializedName("details")
    public ArrayList<Tarefas> details = null;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ArrayList<Tarefas> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Tarefas> details) {
        this.details = details;
    }
}
