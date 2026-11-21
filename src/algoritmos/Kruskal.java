
package algoritmos;

import grafos.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kruskal {

    public static List<Edge> executar(IGraph grafo) {

        if (grafo.type() != GraphType.UNDIRECTED) {
            throw new IllegalArgumentException(
                "Kruskal exige grafo nao dirigido."
            );
        }

        int n = grafo.numVertices();

        List<Edge> arestas = new ArrayList<>(grafo.edges());

        arestas.sort(
            Comparator.comparingDouble(Edge::getWeight)
        );

        int[] pai = new int[n];
        int[] tamanho = new int[n];

        for (int i = 0; i < n; i++) {
            pai[i] = i;
            tamanho[i] = 1;
        }

        List<Edge> arvore = new ArrayList<>();

        for (Edge aresta : arestas) {

            int u = aresta.getU().getId();
            int v = aresta.getV().getId();

            int raizU = encontrar(pai, u);
            int raizV = encontrar(pai, v);

            if (raizU != raizV) {

                arvore.add(aresta);

                unir(pai, tamanho, raizU, raizV);

                if (arvore.size() == n - 1) {
                    break;
                }
            }
        }

        return arvore;
    }

    private static int encontrar(int[] pai, int u) {

        if (pai[u] != u) {
            pai[u] = encontrar(pai, pai[u]);
        }

        return pai[u];
    }

    private static void unir(
        int[] pai,
        int[] tamanho,
        int raizU,
        int raizV
    ) {

        if (tamanho[raizU] < tamanho[raizV]) {

            pai[raizU] = raizV;
            tamanho[raizV] += tamanho[raizU];

        } else {

            pai[raizV] = raizU;
            tamanho[raizU] += tamanho[raizV];
        }
    }
}
