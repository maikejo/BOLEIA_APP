package app.boleia.passageiro.model;

/**
 * Created by maike.silva on 09/03/2018.
 */

public class Rota {

    private String dsrota,sqrota,dsdestino,dsorigem;
    private Double latitude_a,latitude_b,longitude_a,longitude_b,kmtotal;

    public String getDsdestino() {
        return dsdestino;
    }

    public void setDsdestino(String dsdestino) {
        this.dsdestino = dsdestino;
    }

    public String getDsorigem() {
        return dsorigem;
    }

    public void setDsorigem(String dsorigem) {
        this.dsorigem = dsorigem;
    }

    public String getDsrota() {
        return dsrota;
    }

    public void setDsrota(String dsrota) {
        this.dsrota = dsrota;
    }

    public Double getKmtotal() {
        return kmtotal;
    }

    public void setKmtotal(Double kmtotal) {
        this.kmtotal = kmtotal;
    }

    public Double getLatitude_a() {
        return latitude_a;
    }

    public void setLatitude_a(Double latitude_a) {
        this.latitude_a = latitude_a;
    }

    public Double getLatitude_b() {
        return latitude_b;
    }

    public void setLatitude_b(Double latitude_b) {
        this.latitude_b = latitude_b;
    }

    public Double getLongitude_a() {
        return longitude_a;
    }

    public void setLongitude_a(Double longitude_a) {
        this.longitude_a = longitude_a;
    }

    public Double getLongitude_b() {
        return longitude_b;
    }

    public void setLongitude_b(Double longitude_b) {
        this.longitude_b = longitude_b;
    }

    public String getSqrota() {
        return sqrota;
    }

    public void setSqrota(String sqrota) {
        this.sqrota = sqrota;
    }
}
