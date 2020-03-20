package ao.app.boleia.motorista.model;

/**
 * Created by maike.silva on 09/03/2018.
 */

public class Viagens {

    private String cdautomovel,cdutilizador,cdviagem,sqrota,stviagem,uidmotorista,avatarUrl,nomeUtilizador;
    private Long avaliacao,qtdpassageiros,dthrcriado,dthraceita,dthrfim,dthrinicio,dthrcancelado;
    private Boolean visto;

    private Rota rotas;

    public Long getDthrcancelado() {
        return dthrcancelado;
    }

    public void setDthrcancelado(Long dthrcancelado) {
        this.dthrcancelado = dthrcancelado;
    }

    public Rota getRotas() {
        return rotas;
    }

    public void setRotas(Rota rotas) {
        this.rotas = rotas;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getDthrcriado() {
        return dthrcriado;
    }

    public void setDthrcriado(Long dthrcriado) {
        this.dthrcriado = dthrcriado;
    }

    public Long getDthraceita() {
        return dthraceita;
    }

    public void setDthraceita(Long dthraceita) {
        this.dthraceita = dthraceita;
    }

    public Long getDthrfim() {
        return dthrfim;
    }

    public void setDthrfim(Long dthrfim) {
        this.dthrfim = dthrfim;
    }

    public Long getDthrinicio() {
        return dthrinicio;
    }

    public void setDthrinicio(Long dthrinicio) {
        this.dthrinicio = dthrinicio;
    }

    public Long getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Long avaliacao) {
        this.avaliacao = avaliacao;
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

    public String getCdviagem() {
        return cdviagem;
    }

    public void setCdviagem(String cdviagem) {
        this.cdviagem = cdviagem;
    }

    public Long getQtdpassageiros() {
        return qtdpassageiros;
    }

    public void setQtdpassageiros(Long qtdpassageiros) {
        this.qtdpassageiros = qtdpassageiros;
    }

    public String getSqrota() {
        return sqrota;
    }

    public void setSqrota(String sqrota) {
        this.sqrota = sqrota;
    }

    public String getStviagem() {
        return stviagem;
    }

    public void setStviagem(String stviagem) {
        this.stviagem = stviagem;
    }

    public String getUidmotorista() {
        return uidmotorista;
    }

    public void setUidmotorista(String uidmotorista) {
        this.uidmotorista = uidmotorista;
    }

    public Boolean getVisto() {
        return visto;
    }

    public void setVisto(Boolean visto) {
        this.visto = visto;
    }

    public String getNomeUtilizador() {
        return nomeUtilizador;
    }

    public void setNomeUtilizador(String nomeUtilizador) {
        this.nomeUtilizador = nomeUtilizador;
    }
}
