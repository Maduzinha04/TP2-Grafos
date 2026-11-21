package algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoFluxoMaximo {
    public static class FluxoAresta {
        public final int origem;
        public final int destino;
        public final double fluxo;
        public final double capacidade;

        public FluxoAresta(int origem, int destino,
                           double fluxo, double capacidade) {
            this.origem = origem;
            this.destino = destino;
            this.fluxo = fluxo;
            this.capacidade = capacidade;
        }

        @Override
        public String toString() {
            return origem + " -> " + destino + ": "
                + fluxo + "/" + capacidade;
        }
    }

    private final int origem;
    private final int destino;
    private final double fluxoMaximo;
    private final List<FluxoAresta> fluxos;

    public ResultadoFluxoMaximo(int origem, int destino,
                                double fluxoMaximo,
                                List<FluxoAresta> fluxos) {
        this.origem = origem;
        this.destino = destino;
        this.fluxoMaximo = fluxoMaximo;
        this.fluxos = Collections.unmodifiableList(
            new ArrayList<>(fluxos));
    }

    public int getOrigem() { return origem; }
    public int getDestino() { return destino; }
    public double getFluxoMaximo() { return fluxoMaximo; }
    public List<FluxoAresta> getFluxos() { return fluxos; }

    @Override
    public String toString() {
        return "FluxoMaximo{origem=" + origem
            + ", destino=" + destino
            + ", fluxoMaximo=" + fluxoMaximo
            + ", fluxos=" + fluxos + "}";
    }
}
