
package grafos;

import java.util.List;

public interface IGraph {

    GraphType type();

    List<Vertex> vertices();

    List<Edge> edges();

    void addEdge(int u, int v);

    void addEdge(int u, int v, double weight);

    Edge getEdge(int u, int v);

    List<Edge> adj(int u);

    int numVertices();

    int numEdges();
}
