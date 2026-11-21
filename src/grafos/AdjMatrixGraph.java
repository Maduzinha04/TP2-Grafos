
package grafos;

import java.util.ArrayList;
import java.util.List;

public class AdjMatrixGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Edge[][] matrix;
    private final GraphType type;

    public AdjMatrixGraph(int numVertices, GraphType type) {

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
        this.matrix = new Edge[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
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
    matrix[u][v] = edge;

    if (type == GraphType.UNDIRECTED && u != v) {

        Edge reverse = new Edge(
            vertices.get(v),
            vertices.get(u),
            weight
        );

        matrix[v][u] = reverse;
    }
}

    @Override
    public Edge getEdge(int u, int v) {

        validateVertex(u);
        validateVertex(v);

        return matrix[u][v];
    }

    @Override
    public List<Edge> adj(int u) {

        validateVertex(u);

        List<Edge> adjacent = new ArrayList<>();

        for (int v = 0; v < numVertices(); v++) {

            if (matrix[u][v] != null) {
                adjacent.add(matrix[u][v]);
            }
        }

        return adjacent;
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
