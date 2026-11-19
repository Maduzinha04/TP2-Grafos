import grafos.Vertex;

public class Main {
    public static void main(String[] args) {

        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2, "Cidade B");

        System.out.println("Vertice 1: " + v1);
        System.out.println("Vertice 2: " + v2);

        v2.setLabel("Cidade C");

        System.out.println("ID: " + v2.getId());
        System.out.println("Novo nome: " + v2.getLabel());
    }
}
