package algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoTodosPares {
    private final double[][] distancias;
    private final int[][] proximo;
    private final boolean possuiCicloNegativo;

    public ResultadoTodosPares(double[][] distancias, int[][] proximo,
                               boolean possuiCicloNegativo) {
        int n = distancias.length;
        this.distancias = new double[n][];
        this.proximo = new int[n][];
        for (int i = 0; i < n; i++) {
            this.distancias[i] = distancias[i].clone();
            this.proximo[i] = proximo[i].clone();
        }
        this.possuiCicloNegativo = possuiCicloNegativo;
    }

    public double[][] getDistancias() {
        double[][] copia = new double[distancias.length][];
        for (int i = 0; i < distancias.length; i++) {
            copia[i] = distancias[i].clone();
        }
        return copia;
    }

    public int[][] getProximo() {
        int[][] copia = new int[proximo.length][];
        for (int i = 0; i < proximo.length; i++) {
            copia[i] = proximo[i].clone();
        }
        return copia;
    }

    public boolean possuiCicloNegativo() { return possuiCicloNegativo; }

    public List<Integer> reconstruirCaminho(int origem, int destino) {
        if (origem < 0 || destino < 0
                || origem >= distancias.length
                || destino >= distancias.length
                || Double.isInfinite(distancias[origem][destino])) {
            return Collections.emptyList();
        }
        ArrayList<Integer> caminho = new ArrayList<>();
        int atual = origem;
        while (atual != -1) {
            caminho.add(atual);
            if (atual == destino) break;
            atual = proximo[atual][destino];
            if (caminho.size() > distancias.length) return Collections.emptyList();
        }
        if (caminho.isEmpty() || caminho.get(caminho.size() - 1) != destino) {
            return Collections.emptyList();
        }
        return caminho;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TodosPares{cicloNegativo=")
            .append(possuiCicloNegativo).append(", distancias=\n");
        for (double[] linha : distancias) {
            sb.append("[");
            for (int j = 0; j < linha.length; j++) {
                if (j > 0) sb.append(", ");
                sb.append(Double.isInfinite(linha[j]) ? "INF" : linha[j]);
            }
            sb.append("]\n");
        }
        return sb.append("}").toString();
    }
}
