package app.boleia.passageiro.model;

/**
 * Created by maike.silva on 29/03/2018.
 */

public class ViagensRequisicao {
    private String marcaModelo,nomeMotorista,origem,destino,tempo,statusViagem,stViagens;

    public String getStViagens() {
        return stViagens;
    }

    public void setStViagens(String stViagens) {
        this.stViagens = stViagens;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getStatusViagem() {
        return statusViagem;
    }

    public void setStatusViagem(String statusViagem) {
        this.statusViagem = statusViagem;
    }
}
