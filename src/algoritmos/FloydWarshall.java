package algoritmos;

import grafos.Edge;
import grafos.IGraph;
import java.util.Arrays;

public class FloydWarshall {
    private FloydWarshall() {}

    public static ResultadoTodosPares executar(IGraph grafo) {
        int n = grafo.numVertices();
        double[][] dist = new double[n][n];
        int[][] proximo = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            Arrays.fill(proximo[i], -1);
            dist[i][i] = 0.0;
        }

        for (int u = 0; u < n; u++) {
            final int origem = u;
            grafo.forEachAdjacent(u, aresta -> {
                int v = aresta.getV().getId();
                double peso = aresta.getWeight();
                if (peso < dist[origem][v]) {
                    dist[origem][v] = peso;
                    proximo[origem][v] = v;
                }
            });
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (Double.isInfinite(dist[i][k])) continue;
                for (int j = 0; j < n; j++) {
                    if (Double.isInfinite(dist[k][j])) continue;
                    double viaK = dist[i][k] + dist[k][j];
                    if (viaK < dist[i][j]) {
                        dist[i][j] = viaK;
                        proximo[i][j] = proximo[i][k];
                    }
                }
            }
        }

        boolean cicloNegativo = false;
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0.0) {
                cicloNegativo = true;
                break;
            }
        }
        return new ResultadoTodosPares(
            dist, proximo, cicloNegativo);
    }
}
