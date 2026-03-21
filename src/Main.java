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

    // construtor
    public Livro(String titulo, String autor, int anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
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
    // lista de acervo de livros
    LinkedList<Livro> acervoLivros;

    //HashMap de títulos
    HashMap<Livro, Set<Livro>> mapaDeLivros;

    // instanciar a fila de espera pelos livros
    Queue<Emprestimo> filaEmprestimo = new LinkedList<>();

    // criar a pilha do histórico de navegação
    Stack<String> pilhaHistorico = new Stack<>();

    // construtor
    Biblioteca() {
        this.acervoLivros = new LinkedList<>();

    }

    // Método para cadastrar um novo livro na lista - IREI DESENVOLVER MELHOR NA SEQUÊNCIA.
    // Por ex., o sistema deverá pedir os dados pro usuário digitar.
    // Bem como, criarei um Menu Interativo para navegar pelo sistema.
    void cadastrarLivro(Livro livro) {
        acervoLivros.add(livro);
        //System.out.println("** Novo Livro cadastrado com sucesso! **\n\n");
    }

    // mostrar no console a lista de livros cadastrados ou mensagem, se estiver vazia
    void imprimirAcervo() {
        if (acervoLivros.isEmpty()) {
            System.out.println("== Não há livros cadastrados ==");
        } else {
            System.out.println("== Lista de Livros Cadastrados ==");
            for (Livro livro : acervoLivros) {
                System.out.println(livro);
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

        // realizar o armazenamento dos livros no objeto livro instanciado
        minhaBiblioteca.cadastrarLivro(livro1);
        minhaBiblioteca.cadastrarLivro(livro2);
        minhaBiblioteca.cadastrarLivro(livro3);

        int opcao = -1;

        do {
            System.out.println("\n========== MENU BIBLIOTECA VIRTUAL ==========\n");
            System.out.println("1. Ver Acervo Completo / Reservar");
            System.out.println("2. Ver Histórico de Navegação");
            System.out.println("3. Ver Lista de Espera/Reservas");
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
                                minhaBiblioteca.pilhaHistorico.push(livroEscolhido.titulo);

                                // verificar se o livro está disponível
                                if (livroEscolhido.disponivel) {
                                    System.out.println("\nO livro '" + livroEscolhido.titulo + "' está disponível!\n");
                                    System.out.println("Confirma o empréstimo? (S/N): ");
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