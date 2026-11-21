package algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoCaminhoMinimo {
    private final int origem;
    private final double[] distancias;
    private final int[] predecessores;
    private final boolean possuiCicloNegativo;

    public ResultadoCaminhoMinimo(int origem, double[] distancias,
                                  int[] predecessores,
                                  boolean possuiCicloNegativo) {
        this.origem = origem;
        this.distancias = distancias.clone();
        this.predecessores = predecessores.clone();
        this.possuiCicloNegativo = possuiCicloNegativo;
    }

    public int getOrigem() { return origem; }
    public double[] getDistancias() { return distancias.clone(); }
    public int[] getPredecessores() { return predecessores.clone(); }
    public boolean possuiCicloNegativo() { return possuiCicloNegativo; }

    public List<Integer> reconstruirCaminho(int destino) {
        if (destino < 0 || destino >= distancias.length) {
            throw new IndexOutOfBoundsException("Destino invalido: " + destino);
        }
        if (possuiCicloNegativo) {
            return Collections.emptyList();
        }
        if (Double.isInfinite(distancias[destino])) {
            return Collections.emptyList();
        }
        ArrayList<Integer> caminho = new ArrayList<>();
        for (int atual = destino; atual != -1;
             atual = predecessores[atual]) {
            caminho.add(atual);
            if (caminho.size() > distancias.length) {
                return Collections.emptyList();
            }
            if (atual == origem) break;
        }
        Collections.reverse(caminho);
        return caminho;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CaminhoMinimo{origem=").append(origem)
          .append(", cicloNegativo=").append(possuiCicloNegativo)
          .append(", distancias=[");
        for (int i = 0; i < distancias.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(Double.isInfinite(distancias[i])
                ? "INF" : distancias[i]);
        }
        return sb.append("]}").toString();
    }
}
