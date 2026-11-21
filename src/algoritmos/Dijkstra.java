package algoritmos;

import grafos.Edge;
import grafos.IGraph;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private Dijkstra() {}

    public static ResultadoCaminhoMinimo executar(IGraph grafo, int origem) {
        int n = grafo.numVertices();
        if (origem < 0 || origem >= n) {
            throw new IndexOutOfBoundsException(
                "Origem invalida: " + origem);
        }

        double[] dist = new double[n];
        int[] pred = new int[n];
        boolean[] visitado = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        dist[origem] = 0.0;

        PriorityQueue<No> fila = new PriorityQueue<>(
            Comparator.comparingDouble((No no) -> no.distancia));
        fila.add(new No(origem, 0.0));

        while (!fila.isEmpty()) {
            No atual = fila.remove();
            int u = atual.vertice;
            if (visitado[u]) continue;
            if (atual.distancia > dist[u]) continue;
            visitado[u] = true;

            final int origemAtual = u;
            grafo.forEachAdjacent(u, aresta -> {
                if (aresta.getWeight() < 0.0) {
                    throw new IllegalArgumentException(
                        "Dijkstra nao suporta arestas com peso negativo.");
                }
                int v = aresta.getV().getId();
                double novaDist = dist[origemAtual]
                    + aresta.getWeight();
                if (!visitado[v] && novaDist < dist[v]) {
                    dist[v] = novaDist;
                    pred[v] = origemAtual;
                    fila.add(new No(v, novaDist));
                }
            });
        }

        return new ResultadoCaminhoMinimo(
            origem, dist, pred, false);
    }

    private static class No {
        final int vertice;
        final double distancia;

        No(int vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }
    }
}
