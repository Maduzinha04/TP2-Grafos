package algoritmos;

import grafos.Edge;
import grafos.IGraph;
import java.util.Arrays;
import java.util.function.Consumer;

public class BellmanFord {
    private BellmanFord() {}

    public static ResultadoCaminhoMinimo executar(IGraph grafo, int origem) {
        int n = grafo.numVertices();
        if (origem < 0 || origem >= n) {
            throw new IndexOutOfBoundsException(
                "Origem invalida: " + origem);
        }

        double[] dist = new double[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        dist[origem] = 0.0;

        Consumer<Edge> relaxar = aresta -> {
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            double novaDist = dist[u] + aresta.getWeight();
            if (novaDist < dist[v]) {
                dist[v] = novaDist;
                pred[v] = u;
            }
        };

        for (int passo = 0; passo < n - 1; passo++) {
            for (int u = 0; u < n; u++) {
                if (dist[u] == Double.POSITIVE_INFINITY) continue;
                grafo.forEachAdjacent(u, relaxar);
            }
        }

        boolean[] ciclo = {false};
        Consumer<Edge> verificar = aresta -> {
            if (ciclo[0]) return;
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            if (dist[u] + aresta.getWeight() < dist[v]) {
                ciclo[0] = true;
            }
        };
        for (int u = 0; u < n && !ciclo[0]; u++) {
            if (dist[u] == Double.POSITIVE_INFINITY) continue;
            grafo.forEachAdjacent(u, verificar);
        }
        return new ResultadoCaminhoMinimo(
            origem, dist, pred, ciclo[0]);
    }
}
