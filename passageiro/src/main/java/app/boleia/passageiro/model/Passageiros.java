package app.boleia.passageiro.model;

/**
 * Created by maike on 17/11/2017.
 */

public class Passageiros {

    private String uid,cdutilizador,apelido,email,nome,cdstatus,cdtipo,nutelefone,sgsexo,avatarUrl,cdautomovel,cdcategoria;
    private Double latitude,longitude,idade;
    private boolean plantao,logado;

    public String getCdcategoria() {
        return cdcategoria;
    }

    public void setCdcategoria(String cdcategoria) {
        this.cdcategoria = cdcategoria;
    }

    public boolean isLogado() {
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    public boolean isPlantao() {
        return plantao;
    }

    public void setPlantao(boolean plantao) {
        this.plantao = plantao;
    }

    public String getCdautomovel() {
        return cdautomovel;
    }

    public void setCdautomovel(String cdautomovel) {
        this.cdautomovel = cdautomovel;
    }

    public String getCdutilizador() {
        return cdutilizador;
    }

    public void setCdutilizador(String cdutilizador) {
        this.cdutilizador = cdutilizador;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Double getIdade() {
        return idade;
    }

    public void setIdade(Double idade) {
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }

    public String getCdtipo() {
        return cdtipo;
    }

    public void setCdtipo(String cdtipo) {
        this.cdtipo = cdtipo;
    }

    public String getNutelefone() {
        return nutelefone;
    }

    public void setNutelefone(String nutelefone) {
        this.nutelefone = nutelefone;
    }

    public String getSgsexo() {
        return sgsexo;
    }

    public void setSgsexo(String sgsexo) {
        this.sgsexo = sgsexo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
