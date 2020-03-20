package app.boleia.passageiro.model;

/**
 * Created by maike.silva on 08/03/2018.
 */

public class Automovel {

    private String dsmarca;
    private String dsmodelo;
    private String cdstatus;
    private Double latitude;
    private Double longitude;
    private String uidmotorista;
    private String cdautomovel;
    private String dsplaca;
    private String motoristaOnline;
    private String cdutilizador;
    private String cdrgb;
    private int qtdSolicitacoes;

    public int getQtdSolicitacoes() {
        return qtdSolicitacoes;
    }

    public void setQtdSolicitacoes(int qtdSolicitacoes) {
        this.qtdSolicitacoes = qtdSolicitacoes;
    }

    public String getCdrgb() {
        return cdrgb;
    }

    public void setCdrgb(String cdrgb) {
        this.cdrgb = cdrgb;
    }

    public String getCdutilizador() {
        return cdutilizador;
    }

    public void setCdutilizador(String cdutilizador) {
        this.cdutilizador = cdutilizador;
    }

    public String getMotoristaOnline() {
        return motoristaOnline;
    }

    public void setMotoristaOnline(String motoristaOnline) {
        this.motoristaOnline = motoristaOnline;
    }

    public String getDsplaca() {
        return dsplaca;
    }

    public void setDsplaca(String dsplaca) {
        this.dsplaca = dsplaca;
    }

    public String getCdautomovel() {
        return cdautomovel;
    }

    public void setCdautomovel(String cdautomovel) {
        this.cdautomovel = cdautomovel;
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

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }

    public String getDsmodelo() {
        return dsmodelo;
    }

    public void setDsmodelo(String dsmodelo) {
        this.dsmodelo = dsmodelo;
    }


    public String getDsmarca() {
        return dsmarca;
    }

    public void setDsmarca(String dsmarca) {
        this.dsmarca = dsmarca;
    }
}
