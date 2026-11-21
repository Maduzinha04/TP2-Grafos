
package grafos;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LeitorGrafo {

    public static IGraph ler(String caminho, boolean usarMatriz)
            throws IOException {

        IGraph grafo = null;
        int arestasDeclaradas = -1;
        int arestasLidas = 0;

        try (BufferedReader leitor =
                Files.newBufferedReader(Path.of(caminho))) {

            String linha;

            while ((linha = leitor.readLine()) != null) {

                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("c")) {
                    continue;
                }

                String[] partes = linha.split("\\s+");

                if (partes[0].equals("p")) {

                    if (grafo != null || partes.length != 4
                            || !partes[1].equals("sp")) {
                        throw new IOException(
                            "Cabecalho invalido: " + linha
                        );
                    }

                    int numVertices = Integer.parseInt(partes[2]);
                    arestasDeclaradas = Integer.parseInt(partes[3]);

                    if (numVertices < 0 || arestasDeclaradas < 0) {
                        throw new IOException(
                            "Quantidade negativa no cabecalho."
                        );
                    }

                    if (usarMatriz) {
                        grafo = new AdjMatrixGraph(
                            numVertices, GraphType.DIRECTED
                        );
                    } else {
                        grafo = new AdjListGraph(
                            numVertices, GraphType.DIRECTED
                        );
                    }

                } else if (partes[0].equals("a")) {

                    if (grafo == null || partes.length != 4) {
                        throw new IOException(
                            "Linha de aresta invalida: " + linha
                        );
                    }

                    // DIMACS comeca em 1; Java comeca em 0.
                    int u = Integer.parseInt(partes[1]) - 1;
                    int v = Integer.parseInt(partes[2]) - 1;
                    double peso = Double.parseDouble(partes[3]);

                    if (u < 0 || u >= grafo.numVertices()
                            || v < 0 || v >= grafo.numVertices()) {
                        throw new IOException(
                            "Vertice fora do intervalo: " + linha
                        );
                    }

                    grafo.addEdge(u, v, peso);
                    arestasLidas++;

                } else {
                    throw new IOException(
                        "Tipo de linha desconhecido: " + linha
                    );
                }
            }
        }

        if (grafo == null) {
            throw new IOException("Arquivo sem cabecalho.");
        }

        if (arestasLidas != arestasDeclaradas) {
            throw new IOException(
                "Esperadas " + arestasDeclaradas
                + " arestas, mas lidas " + arestasLidas
            );
        }

        return grafo;
    }
}
