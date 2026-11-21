<<<<<<< HEAD
package algoritmos;

import grafos.Edge;
import grafos.GraphType;
import grafos.IGraph;
=======

package algoritmos;

import grafos.*;

>>>>>>> origin/main
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kruskal {
<<<<<<< HEAD
    private Kruskal() {}

    public static ResultadoAGM executar(IGraph grafo) {
        if (grafo.type() != GraphType.UNDIRECTED) {
            throw new IllegalArgumentException(
                "Kruskal exige grafo nao-dirigido.");
        }

        int n = grafo.numVertices();
        DSU dsu = new DSU(n);
        List<Edge> arestas = new ArrayList<>(grafo.edges());
        arestas.sort(Comparator.comparingDouble(Edge::getWeight));
        ArrayList<Edge> selecionadas = new ArrayList<>();
        double custo = 0.0;

        for (Edge aresta : arestas) {
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            if (dsu.union(u, v)) {
                selecionadas.add(aresta);
                custo += aresta.getWeight();
                if (selecionadas.size() == n - 1) break;
            }
        }

        boolean conexo = n == 0
            || selecionadas.size() == n - 1;
        return new ResultadoAGM(selecionadas, custo, conexo,
            conexo ? n : dsu.contarAlcancados());
    }

    private static class DSU {
        final int[] pai;
        final int[] rank;

        DSU(int n) {
            pai = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) pai[i] = i;
        }

        int find(int x) {
            if (pai[x] != x) pai[x] = find(pai[x]);
            return pai[x];
        }

        boolean union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return false;
            if (rank[ra] < rank[rb]) {
                int temp = ra;
                ra = rb;
                rb = temp;
            }
            pai[rb] = ra;
            if (rank[ra] == rank[rb]) rank[ra]++;
            return true;
        }

        int contarAlcancados() {
            if (pai.length == 0) return 0;
            int raiz = find(0);
            int total = 0;
            for (int i = 0; i < pai.length; i++) {
                if (find(i) == raiz) total++;
            }
            return total;
=======

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
>>>>>>> origin/main
        }
    }
}
