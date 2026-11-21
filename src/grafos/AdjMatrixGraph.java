
package grafos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AdjMatrixGraph implements IGraph {

    private final List<Vertex> vertices;
    private final Edge[][] matrix;
    private final GraphType type;
    private boolean weighted;
    private int numEdges;

    public AdjMatrixGraph(int numVertices, GraphType type) {
        this(numVertices, type, false);
    }

    public AdjMatrixGraph(int numVertices, GraphType type, boolean weighted) {

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
        this.matrix = new Edge[numVertices][numVertices];
        this.numEdges = 0;

        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
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
            int inicio = type == GraphType.UNDIRECTED ? u : 0;
            for (int v = inicio; v < numVertices(); v++) {
                if (matrix[u][v] != null) {
                    result.add(matrix[u][v]);
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

        Edge existente = matrix[u][v];
        if (type == GraphType.UNDIRECTED && u != v
                && existente == null) {
            existente = matrix[v][u];
        }

        if (existente != null) {
            if (weight >= existente.getWeight()) {
                return;
            }
        }

        Edge nova = new Edge(
            vertices.get(u),
            vertices.get(v),
            weight
        );

        matrix[u][v] = nova;

        if (type == GraphType.UNDIRECTED && u != v) {
            Edge inversa = new Edge(
                vertices.get(v),
                vertices.get(u),
                weight
            );
            matrix[v][u] = inversa;
        }

        if (existente == null) {
            numEdges++;
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
        return Collections.unmodifiableList(adjacent);
    }

    @Override
    public void forEachAdjacent(int u, Consumer<Edge> action) {
        validateVertex(u);
        if (action == null) {
            throw new IllegalArgumentException("Acao nao pode ser nula.");
        }
        for (int v = 0; v < numVertices(); v++) {
            if (matrix[u][v] != null) {
                action.accept(matrix[u][v]);
            }
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
            resumo.append("Grafo de Matriz: ")
                .append(type).append(", ")
                .append(weighted ? "ponderado" : "não-ponderado")
                .append(", |V|=").append(numVertices())
                .append(", |E|=").append(numEdges)
                .append(", amostra=");
            int limite = Math.min(5, numVertices());
            for (int i = 0; i < limite; i++) {
                if (i > 0) resumo.append(", ");
                resumo.append(i);
            }
            return resumo.toString();
        }

        int largura = 5;
        if (weighted) {
            largura = 8;
        }

        StringBuilder saida = new StringBuilder();
        for (int i = 0; i < largura + 1; i++) saida.append(' ');
        for (int v = 0; v < numVertices(); v++) {
            saida.append(String.format("%" + largura + "d", v));
        }
        saida.append('\n');

        for (int u = 0; u < numVertices(); u++) {
            saida.append(String.format("%" + (largura + 1) + "d", u));
            for (int v = 0; v < numVertices(); v++) {
                Edge edge = matrix[u][v];
                String valor;
                if (edge == null) {
                    valor = "∞";
                } else if (weighted) {
                    valor = formatWeight(edge.getWeight());
                } else {
                    valor = "1";
                }
                saida.append(String.format("%" + largura + "s", valor));
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
