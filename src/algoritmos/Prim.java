package algoritmos;

import grafos.Edge;
import grafos.GraphType;
import grafos.IGraph;
import grafos.Vertex;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Consumer;

public class Prim {
    private Prim() {}

    public static ResultadoAGM executar(IGraph grafo, int inicio) {
        if (grafo.type() != GraphType.UNDIRECTED) {
            throw new IllegalArgumentException(
                "Prim exige grafo nao-dirigido.");
        }
        if (inicio < 0 || inicio >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException(
                "Vertice inicial invalido: " + inicio);
        }

        int n = grafo.numVertices();
        boolean[] visitado = new boolean[n];
        ArrayList<Edge> selecionadas = new ArrayList<>();
        double custo = 0.0;
        java.util.List<Vertex> vertices = grafo.vertices();

        PriorityQueue<Candidato> fila = new PriorityQueue<>(
            Comparator.comparingDouble((Candidato c) -> c.peso)
                .thenComparingInt(c -> c.u)
                .thenComparingInt(c -> c.v));
        Consumer<Edge> adicionar = edge -> {
            int v = edge.getV().getId();
            if (!visitado[v]) {
                fila.add(new Candidato(
                    edge.getU().getId(), v, edge.getWeight()));
            }
        };

        visitado[inicio] = true;
        grafo.forEachAdjacent(inicio, adicionar);

        while (!fila.isEmpty() && selecionadas.size() < n - 1) {
            Candidato candidato = fila.remove();
            if (visitado[candidato.v]) continue;

            visitado[candidato.v] = true;
            custo += candidato.peso;
            selecionadas.add(new Edge(
                vertices.get(candidato.u),
                vertices.get(candidato.v),
                candidato.peso));
            grafo.forEachAdjacent(candidato.v, adicionar);
        }

        int alcancados = 0;
        for (boolean v : visitado) if (v) alcancados++;
        boolean conexo = alcancados == n;
        return new ResultadoAGM(selecionadas, custo, conexo, alcancados);
    }

    private static class Candidato {
        final int u;
        final int v;
        final double peso;

        Candidato(int u, int v, double peso) {
            this.u = u;
            this.v = v;
            this.peso = peso;
        }
    }
}
