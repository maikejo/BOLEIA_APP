package ao.app.boleia.motorista.model;

import java.util.Date;

public class Recarga {

    private String operadora,nu_recarga,imei,status;
    private Long dthr_recarga;

    public String getOperadora() {
        return operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getNu_recarga() {
        return nu_recarga;
    }

    public void setNu_recarga(String nu_recarga) {
        this.nu_recarga = nu_recarga;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDthr_recarga() {
        return dthr_recarga;
    }

    public void setDthr_recarga(Long dthr_recarga) {
        this.dthr_recarga = dthr_recarga;
    }
}
