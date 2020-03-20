package ao.app.boleia.motorista.model.servicedesk;

import com.google.gson.annotations.SerializedName;

public class Tarefas {

    @SerializedName("SUBJECT")
    private String SUBJECT;
    @SerializedName("CREATEDTIME")
    private String CREATEDTIME;
    @SerializedName("PRIORITY")
    private String PRIORITY;
    @SerializedName("STATUS")
    private String STATUS;
    @SerializedName("WORKORDERID")
    private String WORKORDERID;
    @SerializedName("TECHNICIAN")
    private String TECHNICIAN;

    public Tarefas(String SUBJECT, String CREATEDTIME, String PRIORITY, String STATUS, String WORKORDERID, String TECHNICIAN) {
        this.SUBJECT = SUBJECT;
        this.CREATEDTIME = CREATEDTIME;
        this.PRIORITY = PRIORITY;
        this.STATUS = STATUS;
        this.WORKORDERID = WORKORDERID;
        this.TECHNICIAN = TECHNICIAN;
    }

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getCREATEDTIME() {
        return CREATEDTIME;
    }

    public void setCREATEDTIME(String CREATEDTIME) {
        this.CREATEDTIME = CREATEDTIME;
    }

    public String getPRIORITY() {
        return PRIORITY;
    }

    public void setPRIORITY(String PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getWORKORDERID() {
        return WORKORDERID;
    }

    public void setWORKORDERID(String WORKORDERID) {
        this.WORKORDERID = WORKORDERID;
    }

    public String getTECHNICIAN() {
        return TECHNICIAN;
    }

    public void setTECHNICIAN(String TECHNICIAN) {
        this.TECHNICIAN = TECHNICIAN;
    }
}
