
import grafos.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        int aprovados = 0;
        int erros = 0;

        try (Stream<Path> arquivos = Files.list(Path.of("dados"))) {

            List<Path> grafos = arquivos
                .filter(p -> p.toString().endsWith(".gr"))
                .sorted()
                .toList();

            for (Path arquivo : grafos) {

                try {
                    IGraph lista = LeitorGrafo.ler(
                        arquivo.toString(), false
                    );

                    IGraph matriz = LeitorGrafo.ler(
                        arquivo.toString(), true
                    );

                    boolean iguais =
                        lista.numVertices() == matriz.numVertices()
                        && lista.numEdges() == matriz.numEdges();

                    if (iguais) {

                        for (Edge aresta : lista.edges()) {

                            int u = aresta.getU().getId();
                            int v = aresta.getV().getId();

                            Edge outra = matriz.getEdge(u, v);

                            if (outra == null ||
                                Double.compare(
                                    aresta.getWeight(),
                                    outra.getWeight()
                                ) != 0) {

                                iguais = false;
                                break;
                            }
                        }
                    }

                    if (iguais) {
                        System.out.println(
                            "[OK] " + arquivo.getFileName()
                            + " | V=" + lista.numVertices()
                            + " | E=" + lista.numEdges()
                        );
                        aprovados++;
                    } else {
                        System.out.println(
                            "[ERRO] " + arquivo.getFileName()
                            + " | Representacoes diferentes"
                        );
                        erros++;
                    }

                } catch (Exception e) {
                    System.out.println(
                        "[ERRO] " + arquivo.getFileName()
                        + " | " + e.getMessage()
                    );
                    erros++;
                }
            }

            System.out.println("\n===== RESUMO =====");
            System.out.println("Aprovados: " + aprovados);
            System.out.println("Erros: " + erros);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
