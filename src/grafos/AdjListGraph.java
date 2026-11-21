
package grafos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AdjListGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<List<Edge>> adj;
    private final GraphType type;
    private boolean weighted;
    private int numEdges;

    public AdjListGraph(int numVertices, GraphType type) {
        this(numVertices, type, false);
    }

    public AdjListGraph(int numVertices, GraphType type, boolean weighted) {

        if (numVertices < 0) {
            throw new IllegalArgumentException(
                "Numero de vertices nao pode ser negativo."
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                "Tipo de grafo invalido."
            );
        }

        this.type = type;
        this.weighted = weighted;
        this.vertices = new ArrayList<>();
        this.adj = new ArrayList<>();
        this.numEdges = 0;

        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
            adj.add(new ArrayList<>());
        }
    }

    @Override
    public GraphType type() {
        return type;
    }

    @Override
    public boolean isWeighted() {
        return weighted;
    }

    @Override
    public List<Vertex> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    @Override
    public List<Edge> edges() {
        List<Edge> result = new ArrayList<>();
        for (int u = 0; u < numVertices(); u++) {
            for (Edge edge : adj.get(u)) {
                int v = edge.getV().getId();
                if (type == GraphType.DIRECTED || u <= v) {
                    result.add(edge);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public void addEdge(int u, int v) {
        addEdge(u, v, 1.0);
    }

   
    @Override
    public void addEdge(int u, int v, double weight) {

        validateVertex(u);
        validateVertex(v);

        if (weight != 1.0) {
            weighted = true;
        }

        Edge existente = getEdge(u, v);
        if (type == GraphType.UNDIRECTED && u != v
                && existente == null) {
            existente = getEdge(v, u);
        }

        if (existente != null) {
            if (weight >= existente.getWeight()) {
                return;
            }

            adj.get(u).removeIf(edge -> edge.getV().getId() == v);
            if (type == GraphType.UNDIRECTED && u != v) {
                adj.get(v).removeIf(edge -> edge.getV().getId() == u);
            }
        }

        Edge nova = new Edge(
            vertices.get(u),
            vertices.get(v),
            weight
        );

        adj.get(u).add(nova);

        if (type == GraphType.UNDIRECTED && u != v) {
            Edge inversa = new Edge(
                vertices.get(v),
                vertices.get(u),
                weight
            );
            adj.get(v).add(inversa);
        }

        if (existente == null) {
            numEdges++;
        }
    }


    @Override
    public Edge getEdge(int u, int v) {

        validateVertex(u);
        validateVertex(v);

        for (Edge edge : adj.get(u)) {
            if (edge.getV().getId() == v) {
                return edge;
            }
        }

        return null;
    }

    @Override
    public List<Edge> adj(int u) {
        validateVertex(u);
        return Collections.unmodifiableList(adj.get(u));
    }

    @Override
    public void forEachAdjacent(int u, Consumer<Edge> action) {
        validateVertex(u);
        if (action == null) {
            throw new IllegalArgumentException("Acao nao pode ser nula.");
        }
        for (Edge edge : adj.get(u)) {
            action.accept(edge);
        }
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numEdges() {
        return numEdges;
    }

    @Override
    public String formatar() {
        if (numVertices() > 25) {
            StringBuilder resumo = new StringBuilder();
            resumo.append("Grafo de Lista: ")
                .append(type).append(", ")
                .append(weighted ? "ponderado" : "não-ponderado")
                .append(", |V|=").append(numVertices())
                .append(", |E|=").append(numEdges())
                .append(", amostra=");
            int limite = Math.min(5, numVertices());
            for (int i = 0; i < limite; i++) {
                if (i > 0) resumo.append(", ");
                resumo.append(i);
            }
            return resumo.toString();
        }

        StringBuilder saida = new StringBuilder();
        for (Vertex vertex : vertices) {
            saida.append(vertex.getId()).append(": ");
            boolean primeiro = true;
            for (Edge edge : adj.get(vertex.getId())) {
                if (!primeiro) saida.append(", ");
                if (weighted) {
                    saida.append(edge.getV().getId())
                        .append(" (").append(formatWeight(edge.getWeight()))
                        .append(")");
                } else {
                    saida.append(edge.getV().getId());
                }
                primeiro = false;
            }
            saida.append('\n');
        }
        return saida.toString();
    }

    private String formatWeight(double weight) {
        if (weight == (long) weight) {
            return String.format("%d", (long) weight);
        }
        return String.format("%.2f", weight);
    }

    @Override
    public String toString() {
        return formatar();
    }

    private void validateVertex(int u) {

        if (u < 0 || u >= numVertices()) {
            throw new IndexOutOfBoundsException(
                "Vertice invalido: " + u
            );
        }
    }
}
