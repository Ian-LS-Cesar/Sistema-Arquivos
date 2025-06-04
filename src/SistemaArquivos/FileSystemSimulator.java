package SistemaArquivos;

import java.io.*;
import java.time.LocalDateTime;
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
            this.root = new Diretorio("home", LocalDateTime.now(), null);
            this.pastaAtual = root;
            salvar();
            journal.limparJournal();
        }
    }
    // Comandos para Arquivos

    // Criar Arquivo
    public void criarArquivo(String nome, String conteudo) {
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
            journal.adicionarEntrada("criar_arquivo " + caminhoCompleto + " " + conteudo.replaceAll("\\r?\\n", "\\\\n"));
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
                System.err.println("Erro criar_arquivo: Caminho Inválido " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String nomeArquivo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Arquivo arquivo : temp.getArquivos()) {
            if (arquivo.getNome().equals(nomeArquivo)) {
                System.err.println("Erro criar_arquivo: Arquivo com o nome " + nomeArquivo + " já existente");
                return;
            }
        }

        Arquivo novoArquivo = new Arquivo(nomeArquivo, java.time.LocalDateTime.now(), temp, conteudo,
                conteudo.length());
        temp.getArquivos().add(novoArquivo);
    }

    // Renomear Arquivo
    public void renomearArquivo(String nomeOriginal, String novoNome) {
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
        if (entradaJournal) {
            journal.adicionarEntrada("renomear_arquivo " + caminhoCompleto + " " + novoNome);
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
                System.err.println("Erro renomear_arquivo: " + caminhoCompleto);
                return;
            }

            temp = proximaPasta;
        }

        String antigo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Arquivo arquivo : temp.getArquivos()) {
            if (arquivo.getNome().equals(antigo)) {
                arquivo.setNome(novoNome);
                break;
            }
        }

    }

    // Copiar Arquivo
    public void copiarArquivo(String nomeArquivo, Diretorio destino) {
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
        if (entradaJournal) {
            journal.adicionarEntrada("copiar_arquivo " + caminhoOrigem + " " + caminhoDestino);
        }

        String origem;
        if (caminhoOrigem.startsWith("/")) {
            origem = caminhoOrigem.substring(1);
        } else {
            origem = caminhoOrigem;
        }
        String[] parametrosOrigem = origem.split("/");
        Diretorio tempOrigem = root;

        int indiceOrigem = 0;
        if (parametrosOrigem.length > 0 && parametrosOrigem[0].equals(root.getNome())) {
            indiceOrigem = 1;
        }

        for (int i = indiceOrigem; i < parametrosOrigem.length - 1; i++) {
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : tempOrigem.getSubPastas()) {
                if (subPasta.getNome().equals(parametrosOrigem[i])) {
                    proximaPasta = subPasta;
                    break;
                }
            }
            if (proximaPasta == null) {
                System.err.println("Erro copiar_arquivo: Origem inválida" + caminhoOrigem);
                return;
            }
            tempOrigem = proximaPasta;
        }

        Arquivo arquivoOrigem = null;
        String nomeArquivo = parametrosOrigem[parametrosOrigem.length - 1];
        for (Arquivo arquivo : tempOrigem.getArquivos()) {
            if (arquivo.getNome().equals(nomeArquivo)) {
                arquivoOrigem = arquivo;
                break;
            }
        }
        if (arquivoOrigem == null) {
            return;
        }

        String destino;
        if (caminhoDestino.startsWith("/")) {
            destino = caminhoDestino.substring(1);
        } else {
            destino = caminhoDestino;
        }
        String[] parametrosDestino = destino.split("/");
        Diretorio tempDestino = root;

        int indiceDestino = 0;
        if (parametrosDestino.length > 0 && parametrosDestino[0].equals(root.getNome())) {
            indiceDestino = 1;
        }

        for (int i = indiceDestino; i < parametrosDestino.length; i++) {
            Diretorio proximaPasta = null;
            for (Diretorio subPasta : tempDestino.getSubPastas()) {
                if (subPasta.getNome().equals(parametrosDestino[i])) {
                    proximaPasta = subPasta;
                    break;
                }
            }

            if (proximaPasta == null) {
                System.err.println("Erro copiar_arquivo: Destino Inválido" + caminhoDestino);
                return;
            }
            tempDestino = proximaPasta;
        }

        Arquivo copia = new Arquivo(arquivoOrigem.getNome(), LocalDateTime.now(), tempDestino,
                arquivoOrigem.getConteudo(), arquivoOrigem.getConteudo().length());
        tempDestino.getArquivos().add(copia);

    }

    // Apagar Arquivo
    public void apagarArquivo(String nome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nome;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nome;
        }
        apagarArquivoPorCaminho(caminho, true);
        salvar();
    }

    private void apagarArquivoPorCaminho(String caminhoCompleto, boolean entradaJournal) {
        if (entradaJournal) {
            journal.adicionarEntrada("excluir_arquivo " + caminhoCompleto);
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
                System.err.println("Erro excluir_arquivo: Caminho inválido " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String nomeAlvo = parametrosCaminho[parametrosCaminho.length - 1];
        Arquivo remover = null;
        for (Arquivo arquivo : temp.getArquivos()) {
            if (arquivo.getNome().equals(nomeAlvo)) {
                remover = arquivo;
                break;
            }
        }
        if (remover != null) {
            temp.getArquivos().remove(remover);
        }
    }

    // Ler Arquivo
    public String lerConteudoArquivo(String nome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nome;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nome;
        }
        return lerConteudoArquivoPorCaminho(caminho);
    }

    private String lerConteudoArquivoPorCaminho(String caminhoCompleto) {
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
                System.err.println("Erro ler_arquivo: Caminho Inválido " + caminhoCompleto);
                return null;
            }
            temp = proximaPasta;
        }

        String nomeArquivo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Arquivo arquivo : temp.getArquivos()) {
            if (arquivo.getNome().equals(nomeArquivo)) {
                return arquivo.getConteudo();
            }
        }
        System.err.println("Erro ler_arquivo: Arquivo não encontrado " + nomeArquivo);
        return null;
    }

    // Comandos para Pastas
    // Criar Pasta
    public void criarDiretorio(String nome) {
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
                System.err.println("Erro criar_pasta: Caminho Inválido" + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String nomeNovaPasta = parametrosCaminho[parametrosCaminho.length - 1];
        for (Diretorio subPasta : temp.getSubPastas()) {
            if (subPasta.getNome().equals(nomeNovaPasta)) {
                System.err.println("Erro criar_pasta: Pasta com o nome " + nomeNovaPasta + " já existente");
                return;
            }
        }

        Diretorio novaPasta = new Diretorio(nomeNovaPasta, java.time.LocalDateTime.now(), temp);
        temp.getSubPastas().add(novaPasta);
    }

    // Renomear Pasta
    public void renomearDiretorio(String nomeOriginal, String novoNome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nomeOriginal;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nomeOriginal;
        }
        renomearDiretorioPorCaminho(caminho, novoNome, true);
    }

    private void renomearDiretorioPorCaminho(String caminhoCompleto, String novoNome, boolean entradaJournal) {
        if (entradaJournal) {
            journal.adicionarEntrada("renomear_pasta " + caminhoCompleto + " " + novoNome);
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
                System.err.println("Erro renomear_pasta: Caminho Inválido " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }

        String antigo = parametrosCaminho[parametrosCaminho.length - 1];
        for (Diretorio subPasta : temp.getSubPastas()) {
            if (subPasta.getNome().equals(antigo)) {
                subPasta.setNome(novoNome);
                break;
            }
        }
    }

    // Apagar Pasta
    public void apagarDiretorio(String nome) {
        String caminho;
        if (pastaAtual.buscarCaminho().equals("/")) {
            caminho = "/" + nome;
        } else {
            caminho = pastaAtual.buscarCaminho() + "/" + nome;
        }
        apagarDiretorioPorCaminho(caminho, true);
        salvar();

    }

    private void apagarDiretorioPorCaminho(String caminhoCompleto, boolean entradaJournal) {
        if (entradaJournal) {
            journal.adicionarEntrada("excluir_pasta " + caminhoCompleto);
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
                System.err.println("Erro excluir_pasta: Caminho Inválido " + caminhoCompleto);
                return;
            }
            temp = proximaPasta;
        }
        String nomeAlvo = parametrosCaminho[parametrosCaminho.length - 1];
        Diretorio remover = null;
        for (Diretorio subPasta : temp.getSubPastas()) {
            if (subPasta.getNome().equals(nomeAlvo)) {
                remover = subPasta;
                break;
            }
        }
        if (remover != null) {
            temp.getSubPastas().remove(remover);
        }

    }
    // Comandos do Sistema de Arquivos

    // Listar Arquivos e Pastas do Diretório Atual
    public void listarArquivosPasta() {
        System.out.println("\nDiretório: " + pastaAtual.getNome());
        System.out.println("\n  Arquivos:");
        for (Arquivo arquivo : pastaAtual.getArquivos()) {
            System.out.println("    - " + arquivo.getNome() + "     Tamanho: " + arquivo.getTamanho() + "B");
        }

        System.out.println("\n  Pastas:");
        for (Diretorio pasta : pastaAtual.getSubPastas()) {
            System.out.println("    - " + pasta.getNome());
        }

    }

    // Ver Pasta Atual
    public Diretorio getPastaAtual() {
        return pastaAtual;
    }

    //Mudar de Pasta
    public void mudarPasta(String caminho){
        if (caminho.equals("/")){
            pastaAtual = root;
            return;
        }
        
        String[] parametros = caminho.split("/");
        Diretorio temp;
        
        if (caminho.startsWith("/")){
            temp = root;
        } else {
            temp = pastaAtual;
        }

        for (String parametro : parametros){
            if (parametro.isEmpty() || parametro.equals(".")){
                continue;
            } else if (parametro.equals("..")) {
                if (temp.getPai() != null){
                    temp = temp.getPai();
                }
            } else {
                Diretorio proximaPasta = null;
                for (Diretorio subPasta : temp.getSubPastas()) {
                    if (subPasta.getNome().equals(parametro)) {
                        proximaPasta = subPasta;
                        break;
                    }
                }
                if (proximaPasta != null){
                    temp = proximaPasta;
                } else {
                    System.out.println("Diretório não encontrado: " + parametro);
                    return;
                }
            }
        }
        pastaAtual = temp;
    }
    // Executar comandos listados no Journal
    private void retomarEntrada(String entrada) {
        String[] parametros = entrada.split(" ", 3);
        String comando = parametros[0];

        switch (comando) {
            case "criar_arquivo" ->
                criarArquivoPorCaminho(parametros[1], parametros.length > 2 ? parametros[2] : "", false);

            case "renomear_arquivo" ->
                renomearArquivoPorCaminho(parametros[1], parametros[2], false);

            case "copiar_arquivo" ->
                copiarArquivoPorCaminho(parametros[1], parametros[2], false);

            case "excluir_arquivo" ->
                apagarArquivoPorCaminho(parametros[1], false);

            case "criar_pasta" ->
                criarDiretorioPorCaminho(parametros[1], false);

            case "renomear_pasta" ->
                renomearDiretorioPorCaminho(parametros[1], parametros[2], false);

            case "excluir_pasta" ->
                apagarDiretorioPorCaminho(parametros[1], false);
            default ->
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
            System.err.println("Erro ao carregar sistema. Tentando novamente!");
            this.root = new Diretorio("home", LocalDateTime.now(), null);
            this.pastaAtual = root;
        }
    }

    // Salvar Sistema de Arquivos
    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(disco))) {
            oos.writeObject(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
