
package algoritmos;

import grafos.IGraph;
import grafos.Edge;
import grafos.GraphType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class OrdenacaoTopologica {

    public static List<Integer> executar(IGraph grafo) {

        if (grafo.type() != GraphType.DIRECTED) {
            throw new IllegalArgumentException(
                "Ordenacao topologica exige grafo dirigido."
            );
        }

        int n = grafo.numVertices();

        int[] grauEntrada = new int[n];

        for (Edge aresta : grafo.edges()) {
            int destino = aresta.getV().getId();
            grauEntrada[destino]++;
        }

        Queue<Integer> fila = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (grauEntrada[i] == 0) {
                fila.add(i);
            }
        }

        List<Integer> ordem = new ArrayList<>();

        while (!fila.isEmpty()) {

            int atual = fila.remove();
            ordem.add(atual);

            for (Edge aresta : grafo.adj(atual)) {

                int vizinho = aresta.getV().getId();

                grauEntrada[vizinho]--;

                if (grauEntrada[vizinho] == 0) {
                    fila.add(vizinho);
                }
            }
        }

        if (ordem.size() != n) {
            throw new IllegalArgumentException(
                "O grafo possui ciclo."
            );
        }

        return ordem;
    }
}
