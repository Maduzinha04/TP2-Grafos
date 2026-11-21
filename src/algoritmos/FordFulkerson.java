package algoritmos;

import grafos.Edge;
import grafos.GraphType;
import grafos.IGraph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class FordFulkerson {
    private FordFulkerson() {}

    public static ResultadoFluxoMaximo executar(
            IGraph grafo, int fonte, int sumidouro) {
        if (grafo.type() != GraphType.DIRECTED) {
            throw new IllegalArgumentException(
                "Fluxo maximo exige grafo dirigido.");
        }
        int n = grafo.numVertices();
        if (fonte < 0 || fonte >= n
                || sumidouro < 0 || sumidouro >= n) {
            throw new IndexOutOfBoundsException(
                "Fonte ou sumidouro invalido.");
        }
        if (fonte == sumidouro) {
            throw new IllegalArgumentException(
                "Fonte e sumidouro devem ser distintos.");
        }

        double[][] capacidade = new double[n][n];
        double[][] residual = new double[n][n];
        double[][] fluxo = new double[n][n];

        for (Edge aresta : grafo.edges()) {
            if (aresta.getWeight() < 0.0) {
                throw new IllegalArgumentException(
                    "Capacidade negativa nao permitida.");
            }
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            capacidade[u][v] += aresta.getWeight();
            residual[u][v] += aresta.getWeight();
        }

        double fluxoMaximo = 0.0;
        int[] pai = new int[n];

        while (true) {
            Arrays.fill(pai, -1);
            boolean[] visitado = new boolean[n];
            Queue<Integer> fila = new ArrayDeque<>();
            fila.add(fonte);
            visitado[fonte] = true;

            while (!fila.isEmpty() && !visitado[sumidouro]) {
                int u = fila.remove();
                for (int v = 0; v < n; v++) {
                    if (!visitado[v] && residual[u][v] > 0.0) {
                        visitado[v] = true;
                        pai[v] = u;
                        fila.add(v);
                        if (v == sumidouro) break;
                    }
                }
            }

            if (!visitado[sumidouro]) break;

            double incremento = Double.POSITIVE_INFINITY;
            for (int v = sumidouro; v != fonte; v = pai[v]) {
                int u = pai[v];
                incremento = Math.min(incremento, residual[u][v]);
            }

            for (int v = sumidouro; v != fonte; v = pai[v]) {
                int u = pai[v];
                residual[u][v] -= incremento;
                residual[v][u] += incremento;
                fluxo[u][v] += incremento;
                fluxo[v][u] -= incremento;
            }
            fluxoMaximo += incremento;
        }

        List<ResultadoFluxoMaximo.FluxoAresta> fluxos =
            new ArrayList<>();
        for (Edge aresta : grafo.edges()) {
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            double efetivo = fluxo[u][v];
            if (efetivo > 0.0) {
                fluxos.add(new ResultadoFluxoMaximo.FluxoAresta(
                    u, v, efetivo,
                    capacidade[u][v]));
            }
        }
        return new ResultadoFluxoMaximo(
            fonte, sumidouro, fluxoMaximo, fluxos);
    }
}
