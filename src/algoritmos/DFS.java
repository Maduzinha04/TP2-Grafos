
package algoritmos;

import grafos.IGraph;
import grafos.Edge;

import java.util.ArrayList;
import java.util.List;

public class DFS {

    public static List<Integer> executar(IGraph grafo, int inicio) {

        int n = grafo.numVertices();

        if (inicio < 0 || inicio >= n) {
            throw new IndexOutOfBoundsException(
                "Vertice inicial invalido: " + inicio
            );
        }

        boolean[] visitado = new boolean[n];
        List<Integer> ordem = new ArrayList<>();

        visitar(grafo, inicio, visitado, ordem);

        return ordem;
    }

    private static void visitar(
        IGraph grafo,
        int atual,
        boolean[] visitado,
        List<Integer> ordem
    ) {

        visitado[atual] = true;
        ordem.add(atual);

        for (Edge aresta : grafo.adj(atual)) {

            int vizinho = aresta.getV().getId();

            if (!visitado[vizinho]) {
                visitar(grafo, vizinho, visitado, ordem);
            }
        }
    }
}
