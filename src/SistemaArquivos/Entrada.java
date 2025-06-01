package SistemaArquivos;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Entrada implements Serializable {
    protected String nome;
    protected LocalDateTime criacao;
    protected Diretorio pai;

    public Entrada(String nome, LocalDateTime criacao, Diretorio pai) {
        this.nome = nome;
        this.criacao = criacao;
        this.pai = pai;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }
    
    public String buscarCaminho(){
        if (pai == null){
            return "/" + nome;
        }

        String caminhoPai = pai.buscarCaminho();
        if (caminhoPai.equals("/")){
            return caminhoPai + nome;
        } else {
            return caminhoPai + "/" + nome;
        }

    }
    
    public abstract boolean isDiretorio();

    

}
