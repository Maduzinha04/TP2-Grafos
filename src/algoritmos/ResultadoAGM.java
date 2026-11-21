package algoritmos;

import grafos.Edge;
import java.util.Collections;
import java.util.List;

public class ResultadoAGM {
    private final List<Edge> arestas;
    private final double custoTotal;
    private final boolean conexo;
    private final int verticesAlcancados;

    public ResultadoAGM(List<Edge> arestas, double custoTotal,
                        boolean conexo, int verticesAlcancados) {
        this.arestas = Collections.unmodifiableList(
            new java.util.ArrayList<>(arestas));
        this.custoTotal = custoTotal;
        this.conexo = conexo;
        this.verticesAlcancados = verticesAlcancados;
    }

    public List<Edge> getArestas() { return arestas; }
    public double getCustoTotal() { return custoTotal; }
    public boolean isConexo() { return conexo; }
    public int getVerticesAlcancados() { return verticesAlcancados; }

    @Override
    public String toString() {
        return "AGM{conexo=" + conexo
            + ", verticesAlcancados=" + verticesAlcancados
            + ", custoTotal=" + custoTotal
            + ", arestas=" + arestas + "}";
    }
}
