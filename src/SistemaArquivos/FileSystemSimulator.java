package SistemaArquivos;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class FileSystemSimulator {
    private Diretorio root;
    private File disco;
    private Diretorio pastaAtual;
    private Journal journal;

    public FileSystemSimulator(String nomeDisco) {
        this.disco = new File(nomeDisco);
        String nomeJournal;
        if (disco.getParent() == null) {
            nomeJournal = "journal.log";
        } else {
            nomeJournal = disco.getParent() + File.separator + "journal.log";
        }
        this.journal = new Journal(nomeJournal);

        if (disco.exists()) {
            carregarEntradas();
            List<String> pendentes = journal.carregarEntradas();
            if (!pendentes.isEmpty()) {
                System.out.println("Aplicando operações do Journal");
            }

            for (String entrada : pendentes) {
                retomarEntrada(entrada);
            }
            salvar();
            journal.limparJournal();
        } else {
            this.root = new Diretorio("home", LocalDateTime.now(), null, new java.util.LinkedList<>(),
                    new LinkedList<>());
            this.pastaAtual = root;
            salvar();
            journal.limparJournal();
        }
    }
    // Comandos para Arquivos

    // Criar Arquivo
    private void criarArquivo(String nome, String conteudo) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nome;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nome;
        }
        criarArquivoPorCaminho(caminho, conteudo, true);
    }

    private void criarArquivoPorCaminho(String caminhoCompleto, String conteudo, boolean entradaJournal) {
        if (entradaJournal) {
            journal.adicionarEntrada(
                    "criar_arquivo " + caminhoCompleto + " " + conteudo.replaceAll("\\r?\\n", "\\\\n"));
        }

        String caminho;
        if (caminhoCompleto.startsWith("/")) {
            caminho = caminhoCompleto.substring(1);
        } else {
            caminho = caminhoCompleto;
        }

        String[] parametrosCaminho = caminho.split("/");
        Diretorio temp = root;

        int indicePastaAtual = 0;
        if (parametrosCaminho.length > 0 && parametrosCaminho[0].equals(root.getNome())) {
            indicePastaAtual = 1;
        }

        for (int i = indicePastaAtual; i < parametrosCaminho.length - 1; i++) {
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : temp.getSubPastas()) {
                if (subPasta.getNome().equals(parametrosCaminho[i])) {
                    proximaPasta = subPasta;
                    break;
                }
            }

            if (proximaPasta == null) {
                System.out.println("Caminho inválido em : " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String nomeArquivo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Arquivo arquivo : temp.getArquivos()) {
            if (arquivo.getNome().equals(nomeArquivo)) {
                System.out.println("Arquivo com o nome " + nomeArquivo + " já existente");
                return;
            }
        }

        Arquivo novoArquivo = new Arquivo(nomeArquivo, java.time.LocalDateTime.now(), temp, conteudo,
                conteudo.length());
        temp.getArquivos().add(novoArquivo);
    }

    // Renomear Arquivo
    private void renomearArquivo(String nomeOriginal, String novoNome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nomeOriginal;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nomeOriginal;
        }
        renomearArquivoPorCaminho(caminho, novoNome, true);
        salvar();
    }

    private void renomearArquivoPorCaminho(String caminhoCompleto, String novoNome, boolean entradaJournal) {
        if (entradaJournal){
            journal.adicionarEntrada("renomear_arquivo" + caminhoCompleto + " " + novoNome);
        }

        String caminho;
        if (caminhoCompleto.startsWith("/")){
            caminho = caminhoCompleto.substring(1);
        } else {
            caminho = caminhoCompleto;
        }

        String[] parametrosCaminho = caminho.split("/");
        Diretorio temp = root;

        int indicePastaAtual = 0;
        if (parametrosCaminho.length > 0 && parametrosCaminho[0].equals(root.getNome())){
            indicePastaAtual = 1;
        }

        for (int i = indicePastaAtual; i < parametrosCaminho.length - 1; i++){
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : temp.getSubPastas()){
                if (subPasta.getNome().equals(parametrosCaminho[i])){
                    proximaPasta = subPasta;
                    break;
                }
            }

            if (proximaPasta == null){
                System.out.println("Erro na renomeação do arquivo: " + caminhoCompleto);
                return;
            }

            temp = proximaPasta;
        }

        String antigo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Arquivo arquivo : temp.getArquivos()){
            if (arquivo.getNome().equals(antigo)){
                arquivo.setNome(novoNome);
                break;
            }
        }

    }

    // Copiar Arquivo
    private void copiarArquivo(String nomeArquivo, Diretorio destino) {
        String origem;
        if (pastaAtual.buscarCaminho().equals("/")) {
            origem = "/" + nomeArquivo;
        } else {
            origem = pastaAtual.buscarCaminho() + "/" + nomeArquivo;
        }
        String destinoCaminho = destino.buscarCaminho();
        copiarArquivoPorCaminho(origem, destinoCaminho, true);
        salvar();
    }

    private void copiarArquivoPorCaminho(String caminhoOrigem, String caminhoDestino, boolean entradaJournal) {
        if (entradaJournal){
            journal.adicionarEntrada("copiar_arquivo " + caminhoOrigem + " " + caminhoDestino);
        }

        String origem;
        if (caminhoOrigem.startsWith("/")){
            origem = caminhoOrigem.substring(1);
        } else {
            origem = caminhoOrigem;
        }
        String[] parametrosOrigem = origem.split("/");
        Diretorio tempOrigem = root;

        int indiceOrigem = 0;
        if (parametrosOrigem.length > 0 && parametrosOrigem[0].equals(root.getNome())){
            indiceOrigem = 1;
        }

        for (int i = indiceOrigem; i < parametrosOrigem.length - 1; i++){
            Diretorio proximaPasta = null;
            for (Diretorio subPasta: tempOrigem.getSubPastas()) {
                if (subPasta.getNome().equals(parametrosOrigem[i])){
                    proximaPasta = subPasta;
                    break;
                }
            }
            if (proximaPasta == null){
                System.out.println("Erro ao copiar arquivo: Origem inválida" + caminhoOrigem);
                return;
            }
            tempOrigem = proximaPasta;
        }

        Arquivo arquivoOrigem = null;
        String nomeArquivo = parametrosOrigem[parametrosOrigem.length - 1];
        for (Arquivo arquivo : tempOrigem.getArquivos()){
            if (arquivo.getNome().equals(nomeArquivo)){
                arquivoOrigem = arquivo;
                break;
            }
        }
        if (arquivoOrigem == null){
            return;
        }

        String destino;
        if (caminhoDestino.startsWith("/")){
            destino = caminhoDestino.substring(1);
        } else {
            destino = caminhoDestino;
        }
        String[] parametrosDestino = destino.split("/");
        Diretorio tempDestino = root;

        int indiceDestino = 0;
        if (parametrosDestino.length > 0 && parametrosDestino[0].equals(root.getNome())){
            indiceDestino = 1;
        }

        for (int i = indiceDestino; i < parametrosDestino.length; i++){
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : tempDestino.getSubPastas()){
                if (subPasta.getNome().equals(parametrosDestino[i])){
                proximaPasta = subPasta;
                break;
                }
            }

            if (proximaPasta == null){
                System.out.println("Erro ao copiar arquivo: Destino Inválido" + caminhoDestino);
                return;
            }
            tempDestino = proximaPasta;
        }

        Arquivo copia = new Arquivo(arquivoOrigem.getNome(), LocalDateTime.now(), tempDestino, arquivoOrigem.getConteudo(), arquivoOrigem.getConteudo().length());
        tempDestino.getArquivos().add(copia);

    }

    // Apagar Arquivo
    private void apagarArquivo() {

    }

    private void apagarArquivoPorCaminho() {

    }

    // Comandos para Pastas

    // Criar Pasta
    private void criarDiretorio(String nome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nome;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nome;
        }
        criarDiretorioPorCaminho(caminho, true);
        salvar();
    }

    private void criarDiretorioPorCaminho(String caminhoCompleto, boolean entradaJournal) {
        if (entradaJournal) {
            journal.adicionarEntrada("criar_pasta " + caminhoCompleto);
        }
        String caminho;
        if (caminhoCompleto.startsWith("/")) {
            caminho = caminhoCompleto.substring(1);
        } else {
            caminho = caminhoCompleto;
        }

        String[] parametrosCaminho = caminho.split("/");
        Diretorio temp = root;

        int indicePastaAtual = 0;
        if (parametrosCaminho.length > 0 && parametrosCaminho[0].equals(root.getNome())) {
            indicePastaAtual = 1;
        }

        for (int i = indicePastaAtual; i < parametrosCaminho.length - 1; i++) {
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : temp.getSubPastas()) {
                if (subPasta.getNome().equals(parametrosCaminho[i])) {
                    proximaPasta = subPasta;
                    break;
                }
            }

            if (proximaPasta == null) {
                System.out.println("Caminho inválido em : " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String nomeNovaPasta = parametrosCaminho[parametrosCaminho.length - 1];
        for (Diretorio subPasta : temp.getSubPastas()) {
            if (subPasta.getNome().equals(nomeNovaPasta)) {
                System.out.println("Pasta com o nome " + nomeNovaPasta + " já existente");
                return;
            }
        }

        Diretorio novaPasta = new Diretorio(nomeNovaPasta, java.time.LocalDateTime.now(), temp,
                new java.util.LinkedList<>(), new java.util.LinkedList<>());
        temp.getSubPastas().add(novaPasta);
    }

    // Renomear Pasta
    private void renomearDiretorio() {

    }

    private void renomearDiretorioPorCaminho() {

    }

    // Apagar Pasta
    private void apagarDiretorio() {

    }

    private void apagarDiretorioPorCaminho() {

    }
    // Comandos do Sistema de Arquivos

    // Listar Arquivos e Pastas do Diretório Atual
    private void listarArquivosPasta() {
        System.out.println("Arquivos em" + pastaAtual.getNome() + ":");
        for (Arquivo arquivo : pastaAtual.getArquivos()) {
            System.out.println("    - " + arquivo.getNome());
        }

        System.out.println("Subpastas em" + pastaAtual.getNome() + ":");
        for (Diretorio pasta : pastaAtual.getSubPastas()) {
            System.out.println("    - " + pasta.getNome());
        }

    }

    // Ver Pasta Atual
    public Diretorio getPastaAtual() {
        return pastaAtual;
    }

    // Executar comandos listados no Journal
    private void retomarEntrada(String entrada) {
        String[] parametros = entrada.split(" ", 3);
        String comando = parametros[0];

        switch (comando) {
            case "criar_arquivo":
                criarArquivoPorCaminho(parametros[1], parametros.length > 2 ? parametros[2] : "", false);
                break;
            case "renomear_arquivo":
                renomearArquivoPorCaminho(parametros[1], parametros[2], false);
                break;
            case "copiar_arquivo":
                copiarArquivoPorCaminho(parametros[1], parametros[2], false);
                break;
            /* 
                case "apagar_arquivo":
                apagarArquivoPorCaminho(parametros[1], false);
                break;
            */
            case "criar_pasta":
                criarDiretorioPorCaminho(parametros[1], false);
                break;
            /* 
            case "renomear_pasta":
                renomearDiretorioPorCaminho(parametros[1], false);
                break;
            
            case "apagar_pasta":
                apagarDiretorioPorCaminho(parametros[1], false);
                break;
            */
            default:
                System.err.println("Comando desconhecido: " + entrada);
        }
    }

    // Carregar Entradas do Journal
    private void carregarEntradas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(disco))) {
            this.root = (Diretorio) ois.readObject();
            this.pastaAtual = root;
            System.out.println("Sistema de Arquivos iniciado!");
        } catch (Exception e) {
            e.printStackTrace();
            ;
            System.out.println("Erro ao carregar sistema. Tetando novamente!");
            this.root = new Diretorio("home", LocalDateTime.now(), null, new java.util.LinkedList<>(),
                    new java.util.LinkedList<>());
            this.pastaAtual = root;
        }
    }

    // Salvar Sistema de Arquivos
    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(disco))) {
            oos.writeObject(root);
            System.out.println("Sistema de Arquivos salvo!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
