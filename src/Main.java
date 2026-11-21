
import algoritmos.*;
import grafos.*;
import experimento.ExperimentoRunner;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            imprimirMenu();
            int opcao;
            try {
                String linha = scanner.nextLine().trim();
                if (linha.isEmpty()) continue;
                opcao = Integer.parseInt(linha);
            } catch (Exception e) {
                System.out.println("Opcao invalida.");
                continue;
            }
            if (opcao == 0) break;
            try {
                executarOpcao(opcao);
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Encerrando.");
    }

    private static void imprimirMenu() {
        System.out.println("\n=== TP2 - Algoritmos em Grafos ===");
        System.out.println("1. BFS");
        System.out.println("2. DFS");
        System.out.println("3. Ordenacao Topologica");
        System.out.println("4. Kruskal");
        System.out.println("5. Prim");
        System.out.println("6. Bellman-Ford");
        System.out.println("7. Dijkstra");
        System.out.println("8. Floyd-Warshall");
        System.out.println("9. Ford-Fulkerson");
        System.out.println("10. Executar Experimento Completo");
        System.out.println("0. Sair");
        System.out.print("Opcao: ");
    }

    private static void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                IGraph bfs = GrafosExemplo.bfsLista();
                cabecalho("BFS", "Lista de Adjacencia", "Nao-direcionado, nao-ponderado");
                System.out.println(bfs.formatar());
                medir(() -> BFS.executar(bfs, 0), "Ordem");
                break;
            case 2:
                IGraph dfs = GrafosExemplo.dfsLista();
                cabecalho("DFS", "Lista de Adjacencia", "Nao-direcionado, nao-ponderado");
                System.out.println(dfs.formatar());
                medir(() -> DFS.executar(dfs, 0), "Ordem");
                break;
            case 3:
                IGraph topo = GrafosExemplo.ordemTopologica();
                cabecalho("Ordenacao Topologica", "Lista de Adjacencia", "Direcionado, nao-ponderado, DAG");
                System.out.println(topo.formatar());
                medir(() -> OrdenacaoTopologica.executar(topo), "Ordem");
                break;
            case 4:
                IGraph kruskal = GrafosExemplo.agmKruskalLista();
                cabecalho("Kruskal", "Lista de Adjacencia", "Nao-direcionado, ponderado");
                System.out.println(kruskal.formatar());
                medir(() -> Kruskal.executar(kruskal), "Resultado");
                break;
            case 5:
                IGraph prim = GrafosExemplo.agmPrimLista();
                cabecalho("Prim", "Lista de Adjacencia", "Nao-direcionado, ponderado");
                System.out.println(prim.formatar());
                medir(() -> Prim.executar(prim, 0), "Resultado");
                break;
            case 6:
                IGraph bfLa = GrafosExemplo.bellmanFord(false);
                IGraph bfMa = GrafosExemplo.bellmanFord(true);
                cabecalho("Bellman-Ford", "LA e MA", "Direcionado, ponderado, com arestas negativas");
                System.out.println("--- Lista ---\n" + bfLa.formatar());
                medir(() -> BellmanFord.executar(bfLa, 0), "Resultado");
                System.out.println("Caminho reconstruido 0 -> 4: "
                    + BellmanFord.executar(bfLa, 0).reconstruirCaminho(4));
                System.out.println("--- Matriz ---\n" + bfMa.formatar());
                medir(() -> BellmanFord.executar(bfMa, 0), "Resultado");
                System.out.println("Caminho reconstruido 0 -> 4: "
                    + BellmanFord.executar(bfMa, 0).reconstruirCaminho(4));
                break;
            case 7:
                IGraph djLa = GrafosExemplo.dijkstra(false);
                IGraph djMa = GrafosExemplo.dijkstra(true);
                cabecalho("Dijkstra", "LA e MA", "Direcionado, ponderado, pesos nao-negativos");
                System.out.println("--- Lista ---\n" + djLa.formatar());
                medir(() -> Dijkstra.executar(djLa, 0), "Resultado");
                System.out.println("Caminho reconstruido 0 -> 4: "
                    + Dijkstra.executar(djLa, 0).reconstruirCaminho(4));
                System.out.println("--- Matriz ---\n" + djMa.formatar());
                medir(() -> Dijkstra.executar(djMa, 0), "Resultado");
                System.out.println("Caminho reconstruido 0 -> 4: "
                    + Dijkstra.executar(djMa, 0).reconstruirCaminho(4));
                break;
            case 8:
                IGraph fw = GrafosExemplo.floydWarshallMatriz();
                cabecalho("Floyd-Warshall", "Matriz de Adjacencia", "Direcionado, ponderado");
                System.out.println(fw.formatar());
                medir(() -> FloydWarshall.executar(fw), "Resultado");
                var resultadoFw = FloydWarshall.executar(fw);
                System.out.println("Caminho reconstruido 0 -> 4: "
                    + resultadoFw.reconstruirCaminho(0, 4));
                break;
            case 9:
                IGraph rede = GrafosExemplo.redeFluxo();
                cabecalho("Ford-Fulkerson (Edmonds-Karp)", "Lista de Adjacencia", "Direcionado, ponderado por capacidade");
                System.out.println(rede.formatar());
                medir(() -> FordFulkerson.executar(rede, 0, 5), "Resultado");
                break;
            case 10:
                cabecalho("Experimento Completo", "LA e MA", "15 arquivos de dados");
                try {
                    ExperimentoRunner.executar();
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Falha no experimento: " + e.getMessage(), e);
                }
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    private static void cabecalho(String algoritmo, String representacao, String tipo) {
        System.out.println("\nAlgoritmo: " + algoritmo);
        System.out.println("Representacao: " + representacao);
        System.out.println("Grafo: " + tipo);
        System.out.println("Grafo de entrada:");
    }

    private static <T> void medir(java.util.concurrent.Callable<T> chamada, String rotulo) {
        try {
            long inicio = System.nanoTime();
            T resultado = chamada.call();
            long fim = System.nanoTime();
            long tempo = fim - inicio;
            System.out.println(rotulo + ": " + resultado);
            System.out.printf(Locale.US, "Tempo: %.3f ms (%d ns)%n",
                tempo / 1_000_000.0, tempo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
