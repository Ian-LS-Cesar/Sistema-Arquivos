package SistemaArquivos;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Diretorio extends Entrada{


    private List<Arquivo> arquivos = new LinkedList<>();
    private List<Diretorio> subPastas = new LinkedList<>();

    public Diretorio(String nome, LocalDateTime criacao, Diretorio pai, List<Arquivo> arquivos, List<Diretorio> subPastas) {
        super(nome, criacao, pai);
        this.arquivos = new LinkedList<>();
        this.subPastas = new LinkedList<>();
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }


    public List<Diretorio> getSubPastas() {
        return subPastas;
    }

    @Override
    public Diretorio getPai() {
        return pai;
    }
    
    @Override
    public void setPai(Diretorio pai){
        this.pai = pai;
    }

    @Override
    public boolean isDiretorio() {
        return true;
    }


    

    

    
    
}
