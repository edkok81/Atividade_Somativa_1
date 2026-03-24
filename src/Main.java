import java.util.*;

// classe aresta
class Aresta {
    Livro destino;
    int peso; // AFINIDADE ENTRE OS LIVROS, MEDIDOS DE 1 A 10 (1 = menos indicado e 10 = muito indicado)

    public Aresta (Livro destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }
}

// classe Livro
class Livro {
    String titulo;
    String autor;
    int anoPublicacao;
    boolean disponivel = true;

    //LISTA DE CONEXÕES ENTRE OS LIVROS
    List<Aresta> recomendacoes = new ArrayList<>();

    //CRIAÇÃO DOS GALHOS ESQ E DIR PARA USARMOS NA ÁRVORE
    //(em forma de Autorreferência à própria classe Livro! Por isso não é um atributo String por exemplo))
    Livro esquerda, direita;

    // construtor
    public Livro(String titulo, String autor, int anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;

        //INICIALIZAR OS GALHOS COMO NULL (NÃO É NECESSÁRIO EFETIVAMENTE, MAS ESCLARECE O PAPEL COMPLETO DO CONSTRUTOR)
        this. esquerda = null;
        this.direita = null;
    }

    public void adicionarRecomendacao(Livro destino, int peso) {
        this.recomendacoes.add(new Aresta(destino, peso));
    }

    // lógica pro Java imprimir o valor da variável, e não da memória onde está alocada
    @Override
    public String toString() {
        return "Título: " + titulo + " | Autor: " + autor + " | Ano: " + anoPublicacao;
    }
}

// classe Biblioteca
class Biblioteca {
    //CRIAÇÃO DA RAÍZ DA ÁRVORE
    private Livro raiz;

    // lista de acervo de livros
    LinkedList<Livro> acervoLivros;

    //HashMap de títulos
    HashMap<String, Livro> mapaDeLivros;

    // instanciar a fila de espera pelos livros
    Queue<Emprestimo> filaEmprestimo = new LinkedList<>();

    // criar a pilha do histórico de navegação
    Stack<String> pilhaHistorico = new Stack<>();

    // construtor
    Biblioteca() {
        this.acervoLivros = new LinkedList<>();
        this.mapaDeLivros = new HashMap<>(); //INICIA O HASHMAP

        //AQUI, A MESMA LÓGICA PRA INICIALIZAR UM OBJETO NULO, COMO EM ESQ E DIR DA CLASSE LIVRO
        this.raiz = null;
    }

    //MÉTODO PARA INSERIR LIVRO NA ÁRVORE BINÁRIA
    private Livro inserirNaArvore(Livro atual, Livro novo) {
        if (atual == null) {
            return novo;
        }

        //COMPARA OS TÍTULOS EM ORDEM ALFABÉTICA
        if (novo.titulo.compareToIgnoreCase(atual.titulo) < 0) {
            atual.esquerda = inserirNaArvore(atual.esquerda, novo);
        } else if (novo.titulo.compareToIgnoreCase(atual.titulo) > 0) {
            atual.direita = inserirNaArvore(atual.direita, novo);
        }
        return atual;
    }

    // Método para cadastrar um novo livro na lista - IREI DESENVOLVER MELHOR NA SEQUÊNCIA, SE FOR O CASO.
    // Por ex., o sistema deverá pedir os dados pro usuário digitar.
    public void cadastrarLivro(Livro livro) {
        acervoLivros.add(livro);
        mapaDeLivros.put(livro.titulo, livro); //ADICIONA O NOVO LIVRO NA LISTA HASH PRA BUSCA FUTURA

        //CADASTRAR O LIVRO NA ÁRVORE
        this.raiz = inserirNaArvore(this.raiz, livro);
    }

    //MÉTODO PARA BUSCAR NA ÁRVORE
    public Livro buscarNaArvore(String titulo) {
        return buscarRecursivo(this.raiz, titulo);
    }

    //MÉTODO COM A LÓGICA PARA REALIZAR A BUSCA RECURSIVAMENTE
    private Livro buscarRecursivo(Livro atual, String titulo) {
        if (atual == null || atual.titulo.equalsIgnoreCase(titulo)) {
            return atual;
        }

        if (titulo.compareToIgnoreCase(atual.titulo) < 0) {
            return buscarRecursivo(atual.esquerda, titulo);
        }

        return buscarRecursivo(atual.direita, titulo);
    }


    // MÉTODO PARA CONECTAR LIVROS USANDO O HASHMAP
    public void conectarLivros(String tituloA, String tituloB, int peso) {
        Livro livroA = mapaDeLivros.get(tituloA);
        Livro livroB = mapaDeLivros.get(tituloB);

        if (livroA != null && livroB != null) {
            //ADICIONAR O MESMO PESO DE RECOMENDAÇÃO UNILATERALMENTE
            livroA.adicionarRecomendacao(livroB, peso);
            livroB.adicionarRecomendacao(livroA, peso);
        }
    }

    // mostrar no console a lista de livros cadastrados ou mensagem, se estiver vazia
    public void imprimirAcervo() {
        if (acervoLivros.isEmpty()) {
            System.out.println("== Não há livros cadastrados ==");
        } else {
            System.out.println("== Lista de Livros Cadastrados ==");
            for (Livro livro : acervoLivros) {
                System.out.println(livro);
            }
        }
    }
    // PESQUISEI SOBRE O MÉTODO DE Distância de Levenshtein E APLIQUEI AQUI
    // Calcula quantas diferenças (inserir, deletar, trocar letra) separam duas strings.
    private int calcularDistanciaLevenshtein(String s1, String s2) {
        s1 = s1.toLowerCase().trim(); // Normaliza tudo para minúsculo e remove espaços extras
        s2 = s2.toLowerCase().trim();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    // NOVA FUNÇÃO: Busca Inteligente (chamada de Fuzzy Search)
    // Percorre o acervo e encontra o livro com a menor "diferença" do que foi digitado.
    public Livro buscarLivroAproximado(String nomeDigitado) {
        Livro melhorMatch = null;
        int menorDistancia = Integer.MAX_VALUE;

        // Limite de erro aceitável (ex: até 3 letras erradas para títulos longos)
        int limiteErro = 3;

        for (Livro livro : acervoLivros) {
            int distancia = calcularDistanciaLevenshtein(nomeDigitado, livro.titulo);

            // Se achou uma distância menor e está dentro do limite de erro
            if (distancia < menorDistancia && distancia <= limiteErro) {
                menorDistancia = distancia;
                melhorMatch = livro;
            }

            // Se a distância for 0, é uma busca exata (mas case-insensitive)
            if (distancia == 0) return livro;
        }
        return melhorMatch; // Retorna o livro mais parecido ou null se estiver muito diferente
    }

    // MÉTODO PARA REGISTRAR QUALQUER BUSCA OU INTERAÇÃO COM UM LIVRO NO HISTÓRICO
    void registrarNoHistorico(String titulo) {
        // Só adiciona se o título não for nulo ou vazio
        if (titulo != null && !titulo.isEmpty()) {
            // Se o último item da pilha for igual ao anterior, ELE NÃO É ADICIONADO DE NOVO (evita duplicados seguidos)
            if (pilhaHistorico.isEmpty() || !pilhaHistorico.peek().equals(titulo)) {
                pilhaHistorico.push(titulo);
            }
        }
    }
}

// classe Empréstimo
class Emprestimo {
    String nome;
    String livro;

    // construtor
    Emprestimo(String nome, String livro) {
        this.nome = nome;
        this.livro = livro;
    }

    // método para criar a lista de empréstimo
    @Override
    public String toString() {
        return "Usuário: " + nome + " | Aguardando: " + livro;
    }
}

// início da execução
public class Main {
    public static void main(String[] args) {
        // instanciando uma nova Biblioteca
        Biblioteca minhaBiblioteca = new Biblioteca();

        // lógica para criar variável pra receber dados do usuário
        Scanner leituraDadosUsuario = new Scanner(System.in);

        // instanciando os objetos de Livro para criar os livros que existem para empréstimo
        Livro livro1 = new Livro("Assim Falou Zaratustra", "Friedrich Nietzsche", 1893);
        Livro livro2 = new Livro("Cem Anos de Solidão", "Gabriel Garcis Marquez", 1967);
        Livro livro3 = new Livro("1984", "George Orwell", 1949);
        Livro livro4 = new Livro("A Sombra do Vento", "Carlos Ruiz Zafón", 2001);
        Livro livro5 = new Livro("Flores para Algernon", "Daniel Keyes", 1959);
        Livro livro6 = new Livro("A Metamorfose", "Franz Kafka", 1915);
        Livro livro7 = new Livro("O Estrangeiro", "Albert Camus", 1942);
        Livro livro8 = new Livro("O Ladrão Honesto", "Fiodor Dostoiévski", 1860);
        Livro livro9 = new Livro("O Velho e o Mar", "Ernest Hemingway", 1952);
        Livro livro10 = new Livro("Antes que o Café Esfrie", "Toshikazu Kawaguchi", 2015);

        // realizar o armazenamento dos livros no objeto livro instanciado
        minhaBiblioteca.cadastrarLivro(livro1);
        minhaBiblioteca.cadastrarLivro(livro2);
        minhaBiblioteca.cadastrarLivro(livro3);
        minhaBiblioteca.cadastrarLivro(livro4);
        minhaBiblioteca.cadastrarLivro(livro5);
        minhaBiblioteca.cadastrarLivro(livro6);
        minhaBiblioteca.cadastrarLivro(livro7);
        minhaBiblioteca.cadastrarLivro(livro8);
        minhaBiblioteca.cadastrarLivro(livro9);
        minhaBiblioteca.cadastrarLivro(livro10);

        //LÓGICA PARA ADICIONAR A ARESTA DE RECOMENDAÇÃO ENTRE LIVROS, COM O PESO
        minhaBiblioteca.conectarLivros("Assim Falou Zaratustra", "1984", 4);
        minhaBiblioteca.conectarLivros("Assim Falou Zaratustra", "O Estrangeiro", 8);
        minhaBiblioteca.conectarLivros("Assim Falou Zaratustra", "Antes que o Café Esfrie", 7);
        minhaBiblioteca.conectarLivros("Cem Anos de Solidão", "A Sombra do Vento", 10);
        minhaBiblioteca.conectarLivros("Cem Anos de Solidão", "Antes que o Café Esfrie", 8);
        minhaBiblioteca.conectarLivros("Cem Anos de Solidão", "Flores para Algernon", 8);
        minhaBiblioteca.conectarLivros("1984", "A Sombra do Vento", 6);
        minhaBiblioteca.conectarLivros("1984", "Cem Anos de Solidão", 6);
        minhaBiblioteca.conectarLivros("1984", "Antes que o Café Esfrie", 7);
        minhaBiblioteca.conectarLivros("A Sombra do Vento", "Antes que o Café Esfrie", 8);
        minhaBiblioteca.conectarLivros("A Sombra do Vento", "Flores para Algernon", 8);
        minhaBiblioteca.conectarLivros("A Sombra do Vento", "O Velho e o Mar", 9);
        minhaBiblioteca.conectarLivros("Flores para Algernon", "O Velho e o Mar", 8);
        minhaBiblioteca.conectarLivros("Flores para Algernon", "Antes que o Café Esfrie", 8);
        minhaBiblioteca.conectarLivros("Flores para Algernon", "1984", 8);
        minhaBiblioteca.conectarLivros("A Metamorfose", "O Ladrão Honesto", 5);
        minhaBiblioteca.conectarLivros("A Metamorfose", "Flores para Algernon", 7);
        minhaBiblioteca.conectarLivros("A Metamorfose", "Cem anos de Solidão", 6);
        minhaBiblioteca.conectarLivros("O Estrangeiro", "O Ladrão Honesto", 7);
        minhaBiblioteca.conectarLivros("O Estrangeiro", "1984", 6);
        minhaBiblioteca.conectarLivros("O Estrangeiro", "O Velho e o Mar", 5);
        minhaBiblioteca.conectarLivros("O Ladrão Honesto", "O Estrangeiro", 6);
        minhaBiblioteca.conectarLivros("O Ladrão Honesto", "Flores para Algernon", 4);
        minhaBiblioteca.conectarLivros("O Ladrão Honesto", "1984", 6);
        minhaBiblioteca.conectarLivros("O Velho e o Mar", "Cem Anos de Solidão", 7);
        minhaBiblioteca.conectarLivros("O Velho e o Mar", "A Sombra do Vento", 7);
        minhaBiblioteca.conectarLivros("O Velho e o Mar", "Assim Falou Zaratustra", 5);
        minhaBiblioteca.conectarLivros("Antes que o Café Esfrie", "O Velho e o Mar", 6);
        minhaBiblioteca.conectarLivros("Antes que o Café Esfrie", "A Metamorfose", 4);
        minhaBiblioteca.conectarLivros("Antes que o Café Esfrie", "O Estrangeiro", 5);

        int opcao = -1;

        do {
            System.out.println("\n========== MENU BIBLIOTECA VIRTUAL ==========\n");
            System.out.println("1. Ver Acervo Completo / Reservar");
            System.out.println("2. Ver Histórico de Navegação");
            System.out.println("3. Ver Lista de Espera/Reservas");
            System.out.println("4. Verificar Recomendações de livros");
            System.out.println("0. Sair");
            System.out.println("\nEscolha uma das opções (número referente à opção) e pressione ENTER");

            opcao = leituraDadosUsuario.nextInt();
            leituraDadosUsuario.nextLine(); //limpa o buffer

            switch (opcao) {
                case 1:
                    if (minhaBiblioteca.acervoLivros.isEmpty()) {
                        System.out.println("=== Acervo Vazio ===");
                    } else {
                        minhaBiblioteca.imprimirAcervo();
                        System.out.println("\nGostaria de reservar algum dos livros? (S/N): ");
                        String respostaReserva = leituraDadosUsuario.nextLine();

                        if (respostaReserva.equalsIgnoreCase("S")) {
                            System.out.println("Selecione um dos livros pelo seu número: ");
                            for (int i = 0; i < minhaBiblioteca.acervoLivros.size(); i++) {
                                System.out.println((i + 1) + ". " + minhaBiblioteca.acervoLivros.get(i));
                            }

                            System.out.print("\nDigite o número referente ao livro que deseja: ");
                            int indexEscolhido = leituraDadosUsuario.nextInt() - 1; // para o index escolher o livro correto na lista
                            leituraDadosUsuario.nextLine(); // limpa o buffer

                            // verificar se o número digitado pelo usuário existe na lista
                            if (indexEscolhido >= 0 && indexEscolhido < minhaBiblioteca.acervoLivros.size()) {
                                Livro livroEscolhido = minhaBiblioteca.acervoLivros.get(indexEscolhido);

                                // registrar a visualização do livro escolhido na pilha (histórico)
                                minhaBiblioteca.registrarNoHistorico(livroEscolhido.titulo);

                                // verificar se o livro está disponível
                                if (livroEscolhido.disponivel) {
                                    System.out.println("\nO livro '" + livroEscolhido.titulo + "' está disponível!\n");

                                    //INDICAR OUTROS LIVROS DE ACORDO COM ESSA ESCOLHA
                                    if (!livroEscolhido.recomendacoes.isEmpty()) {
                                        System.out.println("DICA: Quem leu '" + livroEscolhido.titulo + "' também gostou de: \n");
                                        for (Aresta a : livroEscolhido.recomendacoes) {
                                            if (a.peso >= 7) {
                                                System.out.println("-> " + a.destino.titulo + " (Afinidade: " + a.peso + "/10)");
                                            }
                                        }
                                        System.out.println("");
                                    }

                                    System.out.println("Confirma o empréstimo de '" + livroEscolhido.titulo + "'? (S/N): ");
                                    String confirmacao = leituraDadosUsuario.nextLine();

                                    if (confirmacao.equalsIgnoreCase("S")) {
                                        livroEscolhido.disponivel = false; // deixa o livro com o status de indisponível
                                        System.out.println("\n=== Empréstimo realizado com sucesso! ===\n");
                                    }
                                }
                                else {
                                    // se já não estiver disponível, entra na fila
                                    System.out.println("\n*** Atenção: o livro " + livroEscolhido.titulo + " já está emprestado! ***\n");
                                    System.out.println("Deseja entrar na fila de reserva de empréstimo? (S/N): ");
                                    String usuarioQuerReserva = leituraDadosUsuario.nextLine();

                                    if (usuarioQuerReserva.equalsIgnoreCase("S")) {
                                        System.out.println("\nDigite seu nome: ");
                                        String nome = leituraDadosUsuario.nextLine();

                                        // add na fila de empréstimos
                                        minhaBiblioteca.filaEmprestimo.add(new Emprestimo(nome, livroEscolhido.titulo));
                                        System.out.println("Você foi adicionado na fila de espera do livro " + livroEscolhido.titulo);
                                    }
                                }
                            }
                            else {
                                System.out.println("Número inválido!");
                            }
                        }
                    }
                    break;

                case 2:
                    System.out.println("\n--- HISTÓRICO DE VISUALIZAÇÃO DE LIVROS (Mais recente no topo) ---\n");

                    // condicional sobre se existe busca ou não
                    if (minhaBiblioteca.pilhaHistorico.isEmpty()) {
                        System.out.println("Histórico vazio.");
                    } else {
                        for (int i = minhaBiblioteca.pilhaHistorico.size() -1; i >= 0; i--) {
                            System.out.println("Visualizado: " + minhaBiblioteca.pilhaHistorico.get(i));
                        }
                    }
                    break;

                case 3:
                    System.out.println("\n=== LISTA DE ESPERA PARA EMPRÉSTIMO ===\n");
                    if (minhaBiblioteca.filaEmprestimo.isEmpty()) {
                        System.out.println("Não há ninguém na fila de empréstimo!");
                    } else {
                        for (Emprestimo e : minhaBiblioteca.filaEmprestimo) {
                            System.out.println(e);
                        }
                    }
                    break;

                // IMPLEMENTAÇÃO DO CASE 4: Verificar Recomendações com Busca Inteligente usando Método de Distância Levenshtein
                case 4:
                    System.out.println("\n=== VERIFICAR RECOMENDAÇÕES DE LIVROS ===");
                    System.out.print("\nDigite o título do livro (não precisa ser exato): ");
                    String buscaTitulo = leituraDadosUsuario.nextLine();

                    if (buscaTitulo.trim().isEmpty()) {
                        System.out.println("Por favor, digite algo para buscar.");
                        break;
                    }
                    // Usamos o novo método de busca inteligente da Biblioteca
                    Livro livroEncontrado = minhaBiblioteca.buscarLivroAproximado(buscaTitulo);

                    if (livroEncontrado != null) {
                        // REGISTRA O LIVRO NO HISTÓRICO
                        minhaBiblioteca.registrarNoHistorico(livroEncontrado.titulo);

                        // Se a distância for > 0, o sistema avisa qual livro foi achado
                        System.out.println("\n------------------------------------------------");
                        System.out.println("Livro encontrado: " + livroEncontrado.titulo);
                        System.out.println("------------------------------------------------");

                        // Lógica de Recomendação do Grafo (a mesma do CASE 1)
                        if (!livroEncontrado.recomendacoes.isEmpty()) {
                            System.out.println("DICA: Quem leu '" + livroEncontrado.titulo + "' também gostou de: \n");
                            for (Aresta a : livroEncontrado.recomendacoes) {
                                if (a.peso >= 7) {
                                    System.out.println(" -> " + a.destino.titulo + " (Afinidade: " + a.peso + "/10)");
                                }
                            }
                        } else {
                            System.out.println("Ainda não temos recomendações cadastradas para este livro.");
                        }
                        System.out.println("------------------------------------------------\n");

                    } else {
                        System.out.println("\nLivro não encontrado no acervo. Tente verificar a ortografia!");
                    }
                    break;

                case 0:
                    System.out.println("*** Saíndo do Sistema ***");
                    break;

                default:
                    System.out.println("Opção inválida");
            }
        } while (opcao != 0);

        leituraDadosUsuario.close();
    }
}