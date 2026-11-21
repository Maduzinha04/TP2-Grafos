package grafos;

import java.io.IOException;

public class GrafosExemplo {
    private GrafosExemplo() {}

    public static IGraph bfsLista() {
        IGraph g = new AdjListGraph(7, GraphType.UNDIRECTED, false);
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3);
        g.addEdge(1, 4); g.addEdge(2, 5); g.addEdge(5, 6);
        return g;
    }

    public static IGraph dfsLista() {
        IGraph g = new AdjListGraph(7, GraphType.UNDIRECTED, false);
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3);
        g.addEdge(1, 4); g.addEdge(2, 5); g.addEdge(5, 6);
        g.addEdge(4, 6);
        return g;
    }

    public static IGraph ordemTopologica() {
        IGraph g = new AdjListGraph(6, GraphType.DIRECTED, false);
        g.addEdge(0, 2); g.addEdge(1, 2); g.addEdge(1, 3);
        g.addEdge(2, 4); g.addEdge(3, 4); g.addEdge(4, 5);
        return g;
    }

    public static IGraph agmKruskalLista() {
        IGraph g = new AdjListGraph(7, GraphType.UNDIRECTED, true);
        g.addEdge(0, 1, 7); g.addEdge(0, 3, 5);
        g.addEdge(1, 2, 8); g.addEdge(1, 3, 9);
        g.addEdge(1, 4, 7); g.addEdge(2, 4, 5);
        g.addEdge(3, 4, 15); g.addEdge(3, 5, 6);
        g.addEdge(4, 5, 8); g.addEdge(4, 6, 9);
        g.addEdge(5, 6, 11);
        return g;
    }

    public static IGraph agmPrimLista() {
        return agmKruskalLista();
    }

    public static IGraph bellmanFord(boolean matriz) {
        IGraph g = matriz
            ? new AdjMatrixGraph(5, GraphType.DIRECTED, true)
            : new AdjListGraph(5, GraphType.DIRECTED, true);
        g.addEdge(0, 1, 6); g.addEdge(0, 2, 7);
        g.addEdge(1, 2, 8); g.addEdge(1, 3, 5);
        g.addEdge(1, 4, -4); g.addEdge(2, 3, -3);
        g.addEdge(2, 4, 9);
        g.addEdge(4, 0, 2); g.addEdge(4, 3, 7);
        return g;
    }

    public static IGraph dijkstra(boolean matriz) {
        IGraph g = matriz
            ? new AdjMatrixGraph(6, GraphType.DIRECTED, true)
            : new AdjListGraph(6, GraphType.DIRECTED, true);
        g.addEdge(0, 1, 7); g.addEdge(0, 2, 9);
        g.addEdge(0, 5, 14); g.addEdge(1, 2, 10);
        g.addEdge(1, 3, 15); g.addEdge(2, 3, 11);
        g.addEdge(2, 5, 2); g.addEdge(3, 4, 6);
        g.addEdge(4, 5, 9);
        return g;
    }

    public static IGraph floydWarshallMatriz() {
        IGraph g = new AdjMatrixGraph(5, GraphType.DIRECTED, true);
        g.addEdge(0, 1, 3); g.addEdge(0, 2, 8);
        g.addEdge(0, 4, -4); g.addEdge(1, 3, 1);
        g.addEdge(1, 4, 7); g.addEdge(2, 1, 4);
        g.addEdge(3, 0, 2); g.addEdge(3, 2, -5);
        g.addEdge(4, 3, 6);
        return g;
    }

    public static IGraph redeFluxo() {
        IGraph g = new AdjListGraph(6, GraphType.DIRECTED, true);
        int[][] a = {
            {0,1,16},{0,2,13},{1,2,10},{1,3,12},
            {2,1,4},{2,4,14},{3,2,9},{3,5,20},
            {4,3,7},{4,5,4}
        };
        for (int[] e : a) g.addEdge(e[0], e[1], e[2]);
        return g;
    }

    public static IGraph lerDados(String nome) throws IOException {
        return LeitorGrafo.ler("dados/" + nome, false);
    }
}
