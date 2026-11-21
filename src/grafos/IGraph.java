
package grafos;

import java.util.List;
import java.util.function.Consumer;

public interface IGraph {

    GraphType type();

    boolean isWeighted();

    List<Vertex> vertices();

    List<Edge> edges();

    void addEdge(int u, int v);

    void addEdge(int u, int v, double weight);

    Edge getEdge(int u, int v);

    List<Edge> adj(int u);

    void forEachAdjacent(int u, Consumer<Edge> action);

    int numVertices();

    int numEdges();

    String formatar();
}
