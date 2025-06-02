package SistemaArquivos;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Journal {
    private final File arquivoJournal;

    public Journal(String nomeJournal){
        this.arquivoJournal = new File(nomeJournal);
        try{
            if (!arquivoJournal.exists()){
                File pai = arquivoJournal.getParentFile();
                if (pai != null){
                    pai.mkdirs();
                }
                arquivoJournal.createNewFile();
            }
        } catch (IOException e){
            System.out.println("Erro ao criar Journal: " + e.getMessage());
        }
    }

    public synchronized void adicionarEntrada(String entrada){
        try (FileWriter fw = new FileWriter(arquivoJournal, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter saida = new PrintWriter(bw)) {
            saida.println();
            saida.flush();
        } catch (IOException e) {
            System.out.println("Erro na adição de entrada do Journal: " + e.getMessage());
        }
    }

    public synchronized List<String> carregarEntradas(){
        List<String> entradas = new ArrayList<>();
        try {
            entradas = Files.readAllLines(arquivoJournal.toPath());
        } catch (IOException e) {
            System.out.println("Erro ao ler entrada do Journal: " + e.getMessage());
        }
        return entradas;
    }

    public synchronized void limparJournal(){
        try(FileWriter fw = new FileWriter(arquivoJournal, false)){
            fw.write("");
            fw.flush();
        } catch (IOException e) {
            System.out.println("Erro ao limpar Journal: " + e.getMessage());
        }
    }
}
