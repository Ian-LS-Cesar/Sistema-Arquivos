package SistemaArquivos;

import java.time.LocalDateTime;

public class Arquivo extends Entrada{
    private String conteudo;
    private long tamanho;
    
    public Arquivo(String nome, LocalDateTime criacao, Diretorio pai, String conteudo, int tamanho) {
        super(nome, criacao, pai);
        if (conteudo == null){
            this.conteudo = "";
        } else {
            this.conteudo = conteudo;
        }
        
        this.tamanho = tamanho;
    }

    public String getConteudo(){
        return conteudo;
    }

    public void setConteudo(String conteudo){
        this.conteudo = conteudo;
    }

    public long getTamanho(){
        return tamanho;
    }

    @Override
    public boolean isDiretorio() {
        return false;
    }

    
}
