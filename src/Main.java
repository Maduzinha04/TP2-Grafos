
import grafos.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        Path pasta = Path.of("dados");

        int aprovados = 0;
        int erros = 0;

        try (Stream<Path> arquivos = Files.list(pasta)) {

            List<Path> grafos = arquivos
                .filter(p -> p.toString().endsWith(".gr"))
                .sorted()
                .toList();

            System.out.println("Arquivos encontrados: " + grafos.size());
            System.out.println();

            for (Path arquivo : grafos) {

                try {
                    IGraph lista = LeitorGrafo.ler(
                        arquivo.toString(), false
                    );

                    IGraph matriz = LeitorGrafo.ler(
                        arquivo.toString(), true
                    );

                    boolean valido =
                        lista.numVertices() == matriz.numVertices()
                        && lista.numEdges() == matriz.numEdges();

                    if (valido) {
                        System.out.println(
                            "[OK] " + arquivo.getFileName()
                            + " | V=" + lista.numVertices()
                            + " | E=" + lista.numEdges()
                        );
                        aprovados++;
                    } else {
                        System.out.println(
                            "[ERRO] " + arquivo.getFileName()
                            + " | Contagens diferentes"
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

            System.out.println();
            System.out.println("===== RESUMO =====");
            System.out.println("Aprovados: " + aprovados);
            System.out.println("Erros: " + erros);

            if (grafos.size() != 15) {
                System.out.println(
                    "ATENCAO: Esperados 15 arquivos .gr."
                );
            }

        } catch (IOException e) {
            System.out.println(
                "Erro ao acessar a pasta dados: " + e.getMessage()
            );
        }
    }
}
