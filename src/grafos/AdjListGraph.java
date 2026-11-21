
package grafos;

import java.util.ArrayList;
import java.util.List;

public class AdjListGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adj;
    private final GraphType type;

    public AdjListGraph(int numVertices, GraphType type) {

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
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>();

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
    public List<Vertex> vertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public List<Edge> edges() {
        return new ArrayList<>(edges);
    }

    @Override
    public void addEdge(int u, int v) {
        addEdge(u, v, 1.0);
    }

    @Override
    public void addEdge(int u, int v, double weight) {

        validateVertex(u);
        validateVertex(v);

        Edge edge = new Edge(
            vertices.get(u),
            vertices.get(v),
            weight
        );

        edges.add(edge);
        adj.get(u).add(edge);

        if (type == GraphType.UNDIRECTED && u != v) {
            Edge reverse = new Edge(
                vertices.get(v),
                vertices.get(u),
                weight
            );

            adj.get(v).add(reverse);
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

        return new ArrayList<>(adj.get(u));
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numEdges() {
        return edges.size();
    }

    private void validateVertex(int u) {

        if (u < 0 || u >= numVertices()) {
            throw new IndexOutOfBoundsException(
                "Vertice invalido: " + u
            );
        }
    }
}
