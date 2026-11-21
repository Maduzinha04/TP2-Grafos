package experimento;

import algoritmos.BellmanFord;
import algoritmos.Dijkstra;
import algoritmos.ResultadoCaminhoMinimo;
import grafos.IGraph;
import grafos.LeitorGrafo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ExperimentoRunner {
    public static final int NUM_REPETICOES = 10;
    private static volatile double sink;

    public static void main(String[] args) throws Exception {
        executar();
    }

    public static void executar() throws Exception {
        Locale.setDefault(Locale.US);
        Configuracao[] grafos = configuracoes();
        warmup();

        Files.createDirectories(Path.of("resultados"));
        try (BufferedWriter tempos = Files.newBufferedWriter(
                Path.of("resultados/tempos_experimento.csv"),
                StandardCharsets.UTF_8);
             BufferedWriter memoria = Files.newBufferedWriter(
                Path.of("resultados/memoria_estruturas.csv"),
                StandardCharsets.UTF_8)) {

            tempos.write("algoritmo,representacao,arquivo,vertices,"
                + "arestas_arquivo,arestas_unicas,densidade,"
                + "repeticao,tempo_ns,tempo_ms");
            tempos.newLine();
            memoria.write("representacao,arquivo,vertices,"
                + "arestas_arquivo,arestas_unicas,densidade,"
                + "memoria_bytes,memoria_kb,tempo_leitura_ms");
            memoria.newLine();

            long[] somaNs = new long[4];
            long[] contagem = new long[4];

            for (int i = 0; i < grafos.length; i++) {
                Configuracao cfg = grafos[i];
                IGraph la = LeitorGrafo.ler("dados/" + cfg.arquivo, false);
                IGraph ma = LeitorGrafo.ler("dados/" + cfg.arquivo, true);
                validarSanity(cfg, la, ma);

                long bfLa = executarMedidas(
                    tempos, "Bellman-Ford", "LA", cfg, la, false,
                    somaNs, contagem, 0);
                long bfMa = executarMedidas(
                    tempos, "Bellman-Ford", "MA", cfg, ma, false,
                    somaNs, contagem, 1);
                long djLa = executarMedidas(
                    tempos, "Dijkstra", "LA", cfg, la, true,
                    somaNs, contagem, 2);
                long djMa = executarMedidas(
                    tempos, "Dijkstra", "MA", cfg, ma, true,
                    somaNs, contagem, 3);

                la = null;
                ma = null;
                escreverMemoria(memoria, "LA", cfg);
                escreverMemoria(memoria, "MA", cfg);

                System.out.printf(Locale.US,
                    "[%d/%d] %s | BF-LA: %.3f ms | BF-MA: %.3f ms"
                    + " | Dijk-LA: %.3f ms | Dijk-MA: %.3f ms%n",
                    i + 1, grafos.length, cfg.arquivo,
                    bfLa / 1_000_000.0, bfMa / 1_000_000.0,
                    djLa / 1_000_000.0, djMa / 1_000_000.0);
            }

            System.out.println("\nResumo das medias:");
            String[] nomes = {"BF-LA", "BF-MA", "Dijk-LA", "Dijk-MA"};
            for (int i = 0; i < nomes.length; i++) {
                System.out.printf(Locale.US, "%s: %.3f ms%n",
                    nomes[i], somaNs[i] / (double) contagem[i] / 1_000_000.0);
            }
        }
        System.out.println("CSV gerados em resultados/");
    }

    private static void warmup() throws IOException {
        String[] arquivos = {"sample100-1980.gr", "sample100-9900.gr"};
        for (String arquivo : arquivos) {
            IGraph la = LeitorGrafo.ler("dados/" + arquivo, false);
            IGraph ma = LeitorGrafo.ler("dados/" + arquivo, true);
            for (int i = 0; i < 15; i++) {
                sink = Dijkstra.executar(la, 0)
                    .getDistancias()[la.numVertices() - 1];
                sink = Dijkstra.executar(ma, 0)
                    .getDistancias()[ma.numVertices() - 1];
            }
            for (int i = 0; i < 10; i++) {
                sink = BellmanFord.executar(la, 0)
                    .getDistancias()[la.numVertices() - 1];
                sink = BellmanFord.executar(ma, 0)
                    .getDistancias()[ma.numVertices() - 1];
            }
        }
    }

    private static void validarSanity(
            Configuracao cfg, IGraph la, IGraph ma) throws IOException {
        ResultadoCaminhoMinimo bfLa = BellmanFord.executar(la, 0);
        ResultadoCaminhoMinimo bfMa = BellmanFord.executar(ma, 0);
        ResultadoCaminhoMinimo djLa = Dijkstra.executar(la, 0);
        ResultadoCaminhoMinimo djMa = Dijkstra.executar(ma, 0);

        if (bfLa.possuiCicloNegativo() || bfMa.possuiCicloNegativo()) {
            throw new IllegalStateException(
                "Ciclo negativo detectado em " + cfg.arquivo);
        }
        double[] vetores[] = {
            bfLa.getDistancias(), bfMa.getDistancias(),
            djLa.getDistancias(), djMa.getDistancias()
        };
        for (double[] vetor : vetores) {
            if (vetor.length != la.numVertices()) {
                throw new IllegalStateException(
                    "Tamanho invalido em " + cfg.arquivo);
            }
            for (int i = 0; i < vetor.length; i++) {
                if (Math.abs(vetor[i] - vetores[0][i]) > 1e-9) {
                    throw new IllegalStateException(
                        "Sanity check divergente em " + cfg.arquivo
                        + " vertice " + i);
                }
            }
        }
    }

    private static long executarMedidas(
            BufferedWriter saida, String algoritmo, String representacao,
            Configuracao cfg, IGraph grafo, boolean dijkstra,
            long[] somaNs, long[] contagem, int indice) throws IOException {

        long soma = 0;
        for (int repeticao = 1;
             repeticao <= NUM_REPETICOES; repeticao++) {
            long inicio = System.nanoTime();
            ResultadoCaminhoMinimo resultado = dijkstra
                ? Dijkstra.executar(grafo, 0)
                : BellmanFord.executar(grafo, 0);
            long fim = System.nanoTime();
            long tempo = fim - inicio;
            sink = resultado.getDistancias()[grafo.numVertices() - 1];
            soma += tempo;

            saida.write(String.format(Locale.US,
                "%s,%s,%s,%d,%d,%d,%.1f,%d,%d,%.6f",
                algoritmo, representacao, cfg.arquivo,
                cfg.vertices, cfg.arestasArquivo,
                grafo.numEdges(), cfg.densidade,
                repeticao, tempo, tempo / 1_000_000.0));
            saida.newLine();
        }
        somaNs[indice] += soma;
        contagem[indice] += NUM_REPETICOES;
        return soma / NUM_REPETICOES;
    }

    private static void escreverMemoria(
            BufferedWriter saida, String representacao,
            Configuracao cfg) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        long memoria = 0;
        long tempoLeitura = 0;
        int arestasUnicas = 0;

        for (int tentativa = 0; tentativa < 3 && memoria <= 0; tentativa++) {
            estabilizarMemoria();
            long antes = runtime.totalMemory() - runtime.freeMemory();

            long inicio = System.nanoTime();
            IGraph referencia = representacao.equals("LA")
                ? LeitorGrafo.ler("dados/" + cfg.arquivo, false)
                : LeitorGrafo.ler("dados/" + cfg.arquivo, true);
            tempoLeitura = System.nanoTime() - inicio;
            arestasUnicas = referencia.numEdges();

            estabilizarMemoria();
            long depois = runtime.totalMemory() - runtime.freeMemory();
            memoria = depois - antes;
            sink = referencia.numVertices() + referencia.numEdges();
        }

        if (memoria <= 0) {
            memoria = 1;
        }

        saida.write(String.format(Locale.US,
            "%s,%s,%d,%d,%d,%.1f,%d,%.3f,%.6f",
            representacao, cfg.arquivo, cfg.vertices,
            cfg.arestasArquivo, arestasUnicas,
            cfg.densidade, memoria, memoria / 1024.0,
            tempoLeitura / 1_000_000.0));
        saida.newLine();
    }

    private static void estabilizarMemoria() {
        Runtime runtime = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            runtime.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static Configuracao[] configuracoes() {
        return new Configuracao[] {
            new Configuracao("sample100-1980.gr", 100, 1980, 0.2),
            new Configuracao("sample100-3960.gr", 100, 3960, 0.4),
            new Configuracao("sample100-5940.gr", 100, 5940, 0.6),
            new Configuracao("sample100-7920.gr", 100, 7920, 0.8),
            new Configuracao("sample100-9900.gr", 100, 9900, 1.0),
            new Configuracao("sample200-7960.gr", 200, 7960, 0.2),
            new Configuracao("sample200-15920.gr", 200, 15920, 0.4),
            new Configuracao("sample200-23880.gr", 200, 23880, 0.6),
            new Configuracao("sample200-31840.gr", 200, 31840, 0.8),
            new Configuracao("sample200-39800.gr", 200, 39800, 1.0),
            new Configuracao("sample500-49900.gr", 500, 49900, 0.2),
            new Configuracao("sample500-99800.gr", 500, 99800, 0.4),
            new Configuracao("sample500-149700.gr", 500, 149700, 0.6),
            new Configuracao("sample500-199600.gr", 500, 199600, 0.8),
            new Configuracao("sample500-249500.gr", 500, 249500, 1.0)
        };
    }

    private static class Configuracao {
        final String arquivo;
        final int vertices;
        final int arestasArquivo;
        final double densidade;

        Configuracao(String arquivo, int vertices,
                     int arestasArquivo, double densidade) {
            this.arquivo = arquivo;
            this.vertices = vertices;
            this.arestasArquivo = arestasArquivo;
            this.densidade = densidade;
        }
    }
}
