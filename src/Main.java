
import grafos.*;

public class Main {

    public static void main(String[] args) {

        IGraph grafo = new AdjMatrixGraph(
            4, GraphType.DIRECTED
        );

        grafo.addEdge(0, 1, 5);
        grafo.addEdge(0, 2, 3);
        grafo.addEdge(1, 2, 2);
        grafo.addEdge(2, 3, 4);

        System.out.println("Tipo: " + grafo.type());
        System.out.println("Vertices: " + grafo.numVertices());
        System.out.println("Arestas: " + grafo.numEdges());

        System.out.println("\nMatriz de adjacencia:");

        System.out.print("    ");

        for (int j = 0; j < grafo.numVertices(); j++) {
            System.out.printf("%6d", j);
        }

        System.out.println();

        for (int i = 0; i < grafo.numVertices(); i++) {

            System.out.printf("%4d", i);

            for (int j = 0; j < grafo.numVertices(); j++) {

                Edge edge = grafo.getEdge(i, j);

                if (edge == null) {
                    System.out.printf("%6s", "-");
                } else {
                    System.out.printf(
                        "%6.1f", edge.getWeight()
                    );
                }
            }

            System.out.println();
        }

        System.out.println(
            "\nAresta 0 -> 1: " + grafo.getEdge(0, 1)
        );
    }
}
