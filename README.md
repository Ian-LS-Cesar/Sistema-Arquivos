# Parte 1: Introdução ao Sistema de Arquivos com Journaling 

Sistema de Arquivos  

O sistema de arquivos é uma estrutura que gerencia os dados guardados e os organiza no dispositivo de armazenamento(HD ou SSD). Dessa forma, ele define como todos os dados serão acessados, armazenados e utilizados, fazendo assim que a manipulação seja eficiente e segura ao acessar arquivos e diretórios.  

Importância do Sistema de Arquivos  

A importância do Sistema de Arquivos é garantir a integridade, segurança e o próprio desempenho no armazenamento e na hora de armazenar os dados. Assim, ele permite que os usuários ou os aplicativos acessem, editem e modifiquem os arquivos de modo eficiente e ele também dá mecanismos de recuperação em caso de falhas. 

Conceito de Journaling  

Journaling, ou registro transacional, é uma técnica usada no sistema de arquivos para garantir a integridade dos dados em situações de falha ou interrupções inesperadas. O registro transacional, como o próprio nome diz, serve para registrar todas as operações realizadas no sistema de arquivos em um arquivo de log, que é conhecido como journal, antes de serem realmente aplicadas, permitindo assim que o sistema possa voltar(restaurar) ao estado funcional em caso de falha usando as próprias informações do Journal. 

Propósito e Funcionamento do Journaling  

O propósito do Journaling é que o sistema não seja corrompido caso tenha alguma falha inesperada, travamento ou qualquer outro problema. O journaling serve para prevenir problemas de integridade, registrando todas as alterações importantes antes que elas sejam eficientemente aplicadas no disco, além de reduzir o tempo necessário para verificar e arrumar o sistema de arquivo depois de um erro. Ele registra no jornal as modificações, confirma que o registro foi salvo, depois a ação é executada no sistema de arquivos, depois o jornal é atualizado, marcando como finalizado. 

Tipos de Journaling 

Write-Ahead Logging (WAL): Em português Registro de Gravação Antecipada é uma família de técnicas que fornece durabilidade e atomicidade, registrando primeiramente todas as alterações registradas antes de serem aplicadas no sistema de arquivos. 

Log-Structured File System (LSFS): Em português Sistema de Arquivos Estruturado em Log serve para lidar com limitações dos sistemas de arquivos, colocando todos os dados e metadados escritos sequencialmente dentro do log, servindo principalmente para melhorar o desempenho de gravação e deixar tudo organizado. 

Metadata Journaling: Em português Diário dos Metadados, ele deixa bem explicativo sobre o que se trata, ele guarda apenas metadados(informações como nome, localização e permissões dos arquivos), ele é rápido mas não tem muita proteção. 

Full Data Journaling: Em português Diário de Todos os Dados, também é bastante explicativo, ele pega tanto dados comuns dos arquivos quanto metadados e registra no jornal, tendo maior integridade mas com custo de desempenho elevado. 

# Parte 2: Arquitetura do Simulador 
Uso de Lista Encadeada 

No simulador de sistema de arquivos, a estrutura de dados utilizada para armazenar os arquivos dentro de um diretório é baseada em listas encadeadas, implementadas com a classe LinkedList 

Essa escolha aparece na classe Diretorio 

private List arquivos = new LinkedList<>(); private List subPastas = new LinkedList<>(); 

Essas listas armazenam, respectivamente, os arquivos e subdiretórios pertencentes a um determinado diretório. A utilização de LinkedList é vantajosa neste contexto porque permite inserções e remoções frequentes de elementos com menor custo computacional em comparação a outras estruturas como ArrayList, principalmente quando essas operações não ocorrem no final da lista 

Já na classe Journal, embora seja utilizado um ArrayList internamente na leitura das entradas do arquivo de log, essa estrutura funciona mais como um buffer temporário de leitura 

List entradas = new ArrayList<>(); 


Neste caso, o ArrayList é apropriado porque os dados são apenas carregados sequencialmente uma vez, e não há modificações frequentes durante sua manipulação 

POO(Programação Orientada a objetos)
Nesse trabalho foi utilizada a programação orientada a objetos, com várias classes e elas comunicando entre si

Vantagens de Usar
Cada parte do sistema é representada como uma classe separada, facilitando manutenção e o teste.
O exemplo muito forte usado é na classe abstrata Entrada, na qual ela fornece atributos comuns que são compartilhados entre arquivos, o que ajuda também a reduzir duplicidade

Journaling

O journaling é uma técnica importante para aumentar a confiabilidade do sistema de arquivos, garantindo que as operações sejam registradas para possível recuperação após falhas. No simulador 

A classe Journal mantém um registro sequencial das operações realizadas, como criação de arquivos/diretórios, exclusão, e alterações. 

Cada operação registrada é armazenada como uma entrada de log que pode ser consultada posteriormente para auditoria. 

O journal também suporta o comando undo, que desfaz a última operação registrada, revertendo o estado do sistema de arquivos para a situação anterior. 

Essa implementação simplificada de journaling simula a persistência e segurança encontradas em sistemas reais, embora sem a complexidade de sistemas operacionais modernos. 


Tipo de Journaling Usado:

WAL(Write-Ahead Logging):

Todas as operações são primeiro registradas no journal (arquivo de log) antes de serem aplicadas na estrutura de dados

Vantagens:
Rápida recuperação
Baixo impacto no desempenho
Separação de dados e logs

A explicação mais elaborada está no começo





# Parte 3 Implementação em Java: 

Classe FileSystemSimulator  

Esta é a classe principal que organiza o funcionamento do sistema de arquivos. Ela representa(simula) o sistema de arquivos como um todo e centraliza a lógica para executar as operações. Ela é responsável por manter o diretório raiz e o diretório de trabalho atual, gerenciar operações como criar arquivos e diretórios, navegar pela árvore de diretórios (cd), listar conteúdo (listar), deletar arquivos e diretórios, e operações avançadas como mover e copiar. Ele interage diretamente com o Journal para registrar todas as modificações realizadas, garantindo que o log esteja sempre atualizado. Controla o fluxo da aplicação, recebendo comandos, interpretando e executando as ações correspondentes. 

Essa arquitetura modular permite que cada parte do sistema de arquivos seja implementada e testada separadamente, além de facilitar futuras expansões, como a adição de novas operações, suporte a permissões, ou persistência de dados.

Classes Arquivo, Diretorio e Entrada  

Entrada(abstrata)  

Representa qualquer entrada no sistema de arquivos, seja um arquivo ou diretório. Armazena Nome, Data de criação, Diretório pai e usa boolean isDiretorio() para saber se é uma pasta ou não, além de buscar caminho do arquivo com buscarCaminho() 

Arquivo 

Ela extende a Entrada, apresenta e representa os arquivos. Adicionalmente possui conteúdo textual (String conteudo) com o tamanho calculado em bytes 

conteudo: o conteúdo textual do arquivo

tamanho: Diz o tamanho do arquivo, a partir do conteúdo Essa classe usa operações relacionadas a arquivos, como leitura 

Diretorio  

Extende Entrada e  representa um diretório (como o nome diz), que é uma estrutura capaz de conter outras entradas como Lista de arquivos (Arquivo) e Lista de subdiretórios (Diretorio). 

Journal  

Implementa o sistema de journaling, ou seja, o registro das operações realizadas no sistema de arquivos. Essa classe é muito importante para garantir que as ações importantes (como criação, exclusão, movimentação) sejam armazenadas em um log. Isso possibilita a recuperação do sistema em situações de falhas. O Journal mantém uma lista das operações executadas e oferece métodos para adicionar entradas e desfazer ações. 

App  

Sua responsabilidade é garantir a interação, ele lê os comandos e puxa os métodos da classe FileSystemSimulator. 
Estrutura de Dados Entrada (Entrada.java)  

Esta é uma classe abstrata que serve como base para os tipos de elementos que compõem o sistema de arquivos, sejam arquivos ou diretórios. Ela tem atributos comuns 

nome: Nome do arquivo ou diretório 

dataCriacao: Registro da data em que a entrada foi criada 

pai: Referência ao diretório pai, que permite navegar na hierarquia do sistema 


Comandos disponíveis:  

criar_arquivo nomeArquivo Conteúdo 

 -> Criar arquivo 

renomear_arquivo nomeArquivo novoNome 

 -> Renomear arquivo 

copiar_arquivo nomeArquivo pastaDestino 

 -> Copiar arquivo para pasta  

excluir_arquivo nomeArquivo 

 -> Excluir arquivo 

ler_arquivo nomeArquivo 

 -> Ler o conteúdo do arquivo  

criar_pasta nomePasta 

 -> Criar pasta 

renomear_pasta nomePasta novoNome 

 -> Renomear pasta  

excluir_pasta nomePasta 

 -> Excluir pasta 

cd caminho 

 -> Mudar de pasta 

listar 

 -> Listar arquivos e pastas 

ajuda 

 -> Listar comandos 

sair 

 -> Fechar simulador 

 



# Parte 4 Instalação e Funcionamento: 
Tem o Arquivo, Diretorio, Entrada, Journal, FileSystemSimulator e App tudo funcionando de acordo como foi descrito anteriormente No fim, o código simula um Sistema de Arquivos, manipulando os próprios arquivos(e pastas) em memória. A serialização é feito no arquivo disco.dat, salvando o estado do sistema, a leitura e escrita no arquivo txt é feito por Buffered, o reader e o writer no Journal. As datas ficam no LocalDateTime. Como citado, foram utilizadas listas encadeadas para armazenar arquivos e pastas no diretório. Estrutura de árvore com pastas tendo subpastas e arquivos. E há também busca de caminho 

O funcionamento é feito no App.java, após rodar, fiz isso, testando a maioria dos comandos, testando também de forma errada para poder indicar o “comando inválido” que tá no código 

/home> ajuda
Comandos disponíveis: 

criar_arquivo nomeArquivo Conteúdo -> Criar arquivo 

renomear_arquivo nomeArquivo novoNome -> Renomear arquivo 

copiar_arquivo nomeArquivo pastaDestino -> Copiar arquivo para pasta 

excluir_arquivo nomeArquivo -> Excluir arquivo 

ler_arquivo nomeArquivo -> Ler o conteúdo do arquivo 

criar_pasta nomePasta -> Criar pasta 

renomear_pasta nomePasta novoNome -> Renomear pasta 

excluir_pasta nomePasta -> Excluir pasta 

cd caminho -> Mudar de pasta 

listar -> Listar arquivos e pastas 

ajuda -> Listar comandos 

sair -> Fechar simulador 

/home> comando
Comando Inválido. Digite 'ajuda' para listar comandos válidos. 
/home> criar_arquivo teste1 teste 

/home> criar_arquivo teste2 henry 

/home> criar_arquivo teste3 ahahahah 

/home> criar_pasta funcionamento /home> listar  

Diretório: home 

Arquivos: 

 - teste1 Tamanho: 5B 

- teste2 Tamanho: 5B  

- teste3 Tamanho: 8B 

Pastas:  

- funcionamento  

/home> ler_arquivo teste1  

Conteúdo de teste1: teste  

/home> copiar_arquivo teste1 funcionamento  

/home> listar 

Diretório: home 

Arquivos:  

- teste1 Tamanho: 5B  

- teste2 Tamanho: 5B  

- teste3 Tamanho: 8B 

Pastas:  

- funcionamento /home> cd funcionamento 

 /home/funcionamento> listar 

Diretório: funcionamento 

Arquivos: - teste1 Tamanho: 5B 

Pastas: 

 /home/funcionamento> excluir_arquivo teste1  

/home/funcionamento> listar 

Diretório: funcionamento 

E assim, o journal.log fica  

criar_arquivo /home/teste1 teste  

criar_arquivo /home/teste2 henry  

criar_arquivo /home/teste3 ahahahah 

 criar_pasta /home/funcionamento  

copiar_arquivo /home/teste1  

/home/funcionamento excluir_arquivo  

/home/funcionamento/teste1