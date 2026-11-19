package grafos;

public class Edge {

    private final Vertex u;
    private final Vertex v;
    private final double weight;

    public Edge(Vertex u, Vertex v) {
        this.u = u;
        this.v = v;
        this.weight = 1.0;
    }

    public Edge(Vertex u, Vertex v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public Vertex getU() {
        return u;
    }

    public Vertex getV() {
        return v;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return u + " -> " + v + " (peso: " + weight + ")";
    }
}
