
import SistemaArquivos.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner entrada = new Scanner(System.in);
        FileSystemSimulator simulador = new FileSystemSimulator("disco.dat");

        while (true) {
            System.out.print(simulador.getPastaAtual().buscarCaminho() + "> ");
            String input = entrada.nextLine();
            String[] texto = input.split(" ", 2);
            String comando = texto[0];
            String parametro;

            if (texto.length > 1) {
                parametro = texto[1];
            } else {
                parametro = "";
            }

            switch (comando) {
                case "criar_arquivo":
                    String[] argumentosArquivo = parametro.split(" ", 2);
                    if (argumentosArquivo.length == 2){
                        simulador.criarArquivo(argumentosArquivo[0], argumentosArquivo[1]);
                    } else {
                        System.out.println("Formato: criar_arquivo nomeArquivo Conteúdo");
                    }
                    break;
                case "renomear_arquivo":
                    String[] argumentosRenomear = parametro.split(" ");
                    if (argumentosRenomear.length == 2){
                        simulador.renomearArquivo(argumentosRenomear[0], argumentosRenomear[1]);
                    } else {
                        System.out.println("Formato: renomear_arquivo nomeArquivo novoNome");
                    }
                    break;
                case "copiar_arquivo":
                    String[] argumentosCopiar = parametro.split(" ");
                    if (argumentosCopiar.length == 2) {
                        String nomeArquivo = argumentosCopiar[0];
                        String nomePastaDestino = argumentosCopiar[1];
                        Diretorio pastaDestino = null;
                        for (Diretorio diretorio : simulador.getPastaAtual().getSubPastas()){
                            if (((Diretorio) diretorio).getNome().equals((nomePastaDestino))){
                                pastaDestino = (Diretorio) diretorio;
                                break;
                            }
                        }
                        if (pastaDestino != null) {
                            simulador.copiarArquivo(nomeArquivo, pastaDestino);
                        } else {
                            System.out.println("Pasta não encontrada.");
                        }
                    } else {
                        System.out.println("Formato: copiar_arquivo nomeArquivo pastaDestino");
                    }
                    break;
                case "excluir_arquivo":
                    simulador.apagarArquivo(parametro);
                    break;
                case "cd":
                    simulador.mudarPasta(parametro);
                    break;
                case "criar_pasta":
                    simulador.criarDiretorio(parametro);
                    break;
                case "renomear_pasta":
                    break;
                case "excluir_pasta":
                    simulador.apagarDiretorio(parametro);
                    break;
                case "listar":
                    simulador.listarArquivosPasta();
                    break;
                case "ajuda":
                    System.out.println(
                        "Comandos disponíveis:\n" +
                        "   - criar_arquivo nomeArquivo Conteúdo        -> Criar arquivo\n" +
                        "   - renomear_arquivo nomeArquivo novoNome     -> Renomear arquivo\n" +
                        "   - copiar_arquivo nomeArquivo pastaDestino   -> Copiar arquivo para pasta\n" +
                        "   - excluir_arquivo nomeArquivo               -> Excluir arquivo\n" +
                        "   - criar_pasta nomePasta                     -> Criar pasta\n" +
                        "   - renomear_pasta nomePasta novoNome         -> Renomear pasta\n" +
                        "   - excluir_pasta nomePasta                   -> Excluir pasta\n" +
                        "   - cd caminho                                -> Mudar de pasta\n" +
                        "   - listar                                    -> Listar arquivos e pastas\n" +
                        "   - ajuda                                     -> Listar comandos\n" +
                        "   - sair                                      -> Fechar simulador\n"
                    );
                    break;
                case "sair":
                    System.out.println("Finalizando...");
                    entrada.close();
                    return;
                default:
                    System.out.println("Comando Inválido. Digite 'ajuda' para listar comandos válidos.");
                    break;
            }
        }
    }
}
