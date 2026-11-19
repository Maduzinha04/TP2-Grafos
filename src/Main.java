import grafos.Vertex;
import grafos.Edge;

public class Main {
    public static void main(String[] args) {

        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2);
        Vertex v3 = new Vertex(3);

        Edge a1 = new Edge(v1, v2);
        Edge a2 = new Edge(v2, v3, 5.0);

        System.out.println("Aresta 1: " + a1);
        System.out.println("Aresta 2: " + a2);

        System.out.println("Origem: " + a2.getU());
        System.out.println("Destino: " + a2.getV());
        System.out.println("Peso: " + a2.getWeight());
    }
}
