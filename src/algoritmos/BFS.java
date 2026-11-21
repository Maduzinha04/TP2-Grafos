
package algoritmos;

import grafos.IGraph;
import grafos.Edge;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class BFS {

    public static List<Integer> executar(IGraph grafo, int inicio) {

        int n = grafo.numVertices();

        if (inicio < 0 || inicio >= n) {
            throw new IndexOutOfBoundsException(
                "Vertice inicial invalido: " + inicio
            );
        }

        boolean[] visitado = new boolean[n];
        Queue<Integer> fila = new ArrayDeque<>();
        List<Integer> ordem = new ArrayList<>();

        visitado[inicio] = true;
        fila.add(inicio);

        while (!fila.isEmpty()) {

            int atual = fila.remove();
            ordem.add(atual);

            for (Edge aresta : grafo.adj(atual)) {

                int vizinho = aresta.getV().getId();

                if (!visitado[vizinho]) {
                    visitado[vizinho] = true;
                    fila.add(vizinho);
                }
            }
        }

        return ordem;
    }
}
