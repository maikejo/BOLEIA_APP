package ao.app.boleia.motorista.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maike.silva on 08/03/2018.
 */

public class Automovel {

    private String cdautomovel,cdrgb,cdstatus,dsmarca,dsmodelo,dsplaca,uidmotorista,cdutilizador;
    private Double latitude,longitude,nukm;
    private List<Categoria> categorias;

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public String getCdutilizador() {
        return cdutilizador;
    }

    public void setCdutilizador(String cdutilizador) {
        this.cdutilizador = cdutilizador;
    }

    public String getCdautomovel() {
        return cdautomovel;
    }

    public void setCdautomovel(String cdautomovel) {
        this.cdautomovel = cdautomovel;
    }

    public String getCdrgb() {
        return cdrgb;
    }

    public void setCdrgb(String cdrgb) {
        this.cdrgb = cdrgb;
    }

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }

    public String getDsmarca() {
        return dsmarca;
    }

    public void setDsmarca(String dsmarca) {
        this.dsmarca = dsmarca;
    }

    public String getDsmodelo() {
        return dsmodelo;
    }

    public void setDsmodelo(String dsmodelo) {
        this.dsmodelo = dsmodelo;
    }

    public String getDsplaca() {
        return dsplaca;
    }

    public void setDsplaca(String dsplaca) {
        this.dsplaca = dsplaca;
    }

    public String getUidmotorista() {
        return uidmotorista;
    }

    public void setUidmotorista(String uidmotorista) {
        this.uidmotorista = uidmotorista;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getNukm() {
        return nukm;
    }

    public void setNukm(Double nukm) {
        this.nukm = nukm;
    }
}
