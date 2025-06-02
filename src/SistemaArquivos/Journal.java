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
            System.err.println("Erro ao criar Journal: " + e.getMessage());
        }
    }

    // Exemplo de método correto para Journal
    public void adicionarEntrada(String entrada) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoJournal, true))) {
            writer.write(entrada);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> carregarEntradas(){
        List<String> entradas = new ArrayList<>();
        try {
            entradas = Files.readAllLines(arquivoJournal.toPath());
        } catch (IOException e) {
            System.err.println("Erro ao ler entrada do Journal: " + e.getMessage());
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
