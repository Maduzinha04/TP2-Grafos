
import grafos.*;
import algoritmos.OrdenacaoTopologica;

public class Main {

    public static void main(String[] args) {

        IGraph lista = new AdjListGraph(
            6, GraphType.DIRECTED
        );

        IGraph matriz = new AdjMatrixGraph(
            6, GraphType.DIRECTED
        );

        int[][] arestas = {
            {0, 1},
            {0, 2},
            {1, 2},
            {1, 3},
            {2, 3}
        };

        for (int[] aresta : arestas) {
            lista.addEdge(aresta[0], aresta[1]);
            matriz.addEdge(aresta[0], aresta[1]);
        }

        System.out.println("Topologica - Lista: "
            + OrdenacaoTopologica.executar(lista));

        System.out.println("Topologica - Matriz: "
            + OrdenacaoTopologica.executar(matriz));

            IGraph ciclo = new AdjListGraph(3, GraphType.DIRECTED);

            ciclo.addEdge(0, 1);
            ciclo.addEdge(1, 2);
            ciclo.addEdge(2, 0);

            try {
                System.out.println(OrdenacaoTopologica.executar(ciclo));
            } catch (IllegalArgumentException e) {
                System.out.println("Ciclo detectado: " + e.getMessage());
            }
    }
    
}
