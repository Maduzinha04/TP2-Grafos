from pathlib import Path
import numpy as np
import pandas as pd
from scipy import stats
import matplotlib.pyplot as plt
import seaborn as sns

# Caminhos
BASE_DIR = Path(__file__).resolve().parent.parent
RESULTADOS_DIR = BASE_DIR / "resultados"
OUT_DIR = RESULTADOS_DIR / "analise"
FIG_DIR = OUT_DIR / "figuras"

ALPHA = 0.05
PALETTE = {"LA": "#2b6cb0", "MA": "#dd6b20"}


def carregar_dados():
    df_t = pd.read_csv(RESULTADOS_DIR / "tempos_experimento.csv")
    df_m = pd.read_csv(RESULTADOS_DIR / "memoria_estruturas.csv")
    return df_t, df_m


def estatisticas_descritivas(df_t: pd.DataFrame) -> pd.DataFrame:
    rows = []
    for (alg, rep, v, d), g in df_t.groupby(
        ["algoritmo", "representacao", "vertices", "densidade"]
    ):
        vals = g["tempo_ms"].values
        w_stat, p_shap = stats.shapiro(vals)
        q1, med, q3 = np.percentile(vals, [25, 50, 75])
        rows.append(
            {
                "algoritmo": alg,
                "representacao": rep,
                "vertices": v,
                "densidade": d,
                "arestas_arquivo": int(g["arestas_arquivo"].iloc[0]),
                "arestas_unicas": int(g["arestas_unicas"].iloc[0]),
                "n": len(vals),
                "media_ms": vals.mean(),
                "dp_ms": vals.std(ddof=1),
                "cv_pct": (vals.std(ddof=1) / vals.mean()) * 100.0,
                "min_ms": vals.min(),
                "q1_ms": q1,
                "mediana_ms": med,
                "q3_ms": q3,
                "iqr_ms": q3 - q1,
                "max_ms": vals.max(),
                "shapiro_W": w_stat,
                "shapiro_p": p_shap,
                "normal_005": p_shap >= ALPHA,
            }
        )
    return pd.DataFrame(rows)


def testar_fator_a(df_t: pd.DataFrame) -> pd.DataFrame:
    """Fator A (H0'): LA vs MA para cada (algoritmo, V, D)."""
    rows = []
    for (alg, v, d), g in df_t.groupby(["algoritmo", "vertices", "densidade"]):
        la = g[g["representacao"] == "LA"]["tempo_ms"].values
        ma = g[g["representacao"] == "MA"]["tempo_ms"].values
        t_stat, p_welch = stats.ttest_ind(la, ma, equal_var=False)
        u_stat, p_mw = stats.mannwhitneyu(la, ma, alternative="two-sided")
        # Tamanho de efeito rank-biserial (entre -1 e 1)
        r_rb = 1.0 - (2.0 * u_stat) / (len(la) * len(ma))
        rows.append(
            {
                "algoritmo": alg,
                "vertices": v,
                "densidade": d,
                "media_LA_ms": la.mean(),
                "dp_LA_ms": la.std(ddof=1),
                "mediana_LA_ms": np.median(la),
                "media_MA_ms": ma.mean(),
                "dp_MA_ms": ma.std(ddof=1),
                "mediana_MA_ms": np.median(ma),
                "razao_MA_LA": ma.mean() / la.mean(),
                "t_welch": t_stat,
                "p_welch": p_welch,
                "U_mannwhitney": u_stat,
                "p_mannwhitney": p_mw,
                "r_rank_biserial": r_rb,
                "rejeita_H0_linha_mw": p_mw < ALPHA,
                "rejeita_H0_linha_welch": p_welch < ALPHA,
            }
        )
    return pd.DataFrame(rows)


def testar_fator_b(df_t: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame]:
    """Fator B (H0''): Esparso vs Denso (extremos 0.2 vs 1.0 e Kruskal-Wallis nas 5 densidades)."""
    rows_ext = []
    rows_kw = []
    for (alg, rep, v), g in df_t.groupby(["algoritmo", "representacao", "vertices"]):
        esp = g[g["densidade"] == 0.2]["tempo_ms"].values
        den = g[g["densidade"] == 1.0]["tempo_ms"].values
        t_stat, p_welch = stats.ttest_ind(esp, den, equal_var=False)
        u_stat, p_mw = stats.mannwhitneyu(esp, den, alternative="two-sided")
        rows_ext.append(
            {
                "algoritmo": alg,
                "representacao": rep,
                "vertices": v,
                "media_D02_ms": esp.mean(),
                "media_D10_ms": den.mean(),
                "razao_D10_D02": den.mean() / esp.mean(),
                "p_welch": p_welch,
                "p_mannwhitney": p_mw,
                "rejeita_H0_2linhas_mw": p_mw < ALPHA,
            }
        )

        grupos = [g[g["densidade"] == d]["tempo_ms"].values for d in [0.2, 0.4, 0.6, 0.8, 1.0]]
        h_stat, p_kw = stats.kruskal(*grupos)
        f_stat, p_anova = stats.f_oneway(*grupos)
        rows_kw.append(
            {
                "algoritmo": alg,
                "representacao": rep,
                "vertices": v,
                "H_kruskal": h_stat,
                "p_kruskal": p_kw,
                "rejeita_H0_2linhas_kw": p_kw < ALPHA,
                "F_anova": f_stat,
                "p_anova": p_anova,
            }
        )
    return pd.DataFrame(rows_ext), pd.DataFrame(rows_kw)


def gerar_boxplots(df_t: pd.DataFrame, algoritmo: str, prefixo_arquivo: str):
    sub = df_t[df_t["algoritmo"] == algoritmo].copy()
    sub["densidade_str"] = sub["densidade"].map(lambda x: f"{x:.1f}")
    vertices_list = sorted(sub["vertices"].unique())

    fig, axes = plt.subplots(1, len(vertices_list), figsize=(14, 4.4), sharey=False)
    for ax, v in zip(axes, vertices_list):
        data_v = sub[sub["vertices"] == v]
        sns.boxplot(
            data=data_v,
            x="densidade_str",
            y="tempo_ms",
            hue="representacao",
            hue_order=["LA", "MA"],
            palette=PALETTE,
            width=0.6,
            fliersize=4,
            ax=ax,
        )
        ax.set_title(f"|V| = {v} vértices", fontsize=11, fontweight="bold")
        ax.set_xlabel("Densidade nominal (D)")
        ax.set_ylabel("Tempo de execução (ms)" if v == vertices_list[0] else "")
        ax.get_legend().remove()

    # Legenda única fora dos painéis (evita sobreposição com caixas)
    handles, labels = axes[0].get_legend_handles_labels()
    fig.legend(
        handles,
        ["Lista de Adjacência (LA)", "Matriz de Adjacência (MA)"],
        loc="upper center",
        bbox_to_anchor=(0.5, 1.02),
        ncol=2,
        frameon=True,
    )
    fig.suptitle(
        f"Tempo de Execução — {algoritmo} (10 repetições por configuração)",
        fontsize=12.5,
        fontweight="bold",
        y=1.10,
    )
    plt.tight_layout()
    for ext in ("png", "pdf"):
        fig.savefig(FIG_DIR / f"{prefixo_arquivo}.{ext}", dpi=300, bbox_inches="tight")
    plt.close(fig)


def gerar_grafico_memoria(df_m: pd.DataFrame):
    df_plot = df_m.copy()
    df_plot["densidade_str"] = df_plot["densidade"].map(lambda x: f"{x:.1f}")
    vertices_list = sorted(df_plot["vertices"].unique())

    fig, axes = plt.subplots(1, len(vertices_list), figsize=(14, 4.2), sharey=False)
    for ax, v in zip(axes, vertices_list):
        data_v = df_plot[df_plot["vertices"] == v]
        sns.barplot(
            data=data_v,
            x="densidade_str",
            y="memoria_kb",
            hue="representacao",
            hue_order=["LA", "MA"],
            palette=PALETTE,
            ax=ax,
        )
        ax.set_title(f"|V| = {v} vértices", fontsize=11, fontweight="bold")
        ax.set_xlabel("Densidade nominal (D)")
        ax.set_ylabel("Memória aproximada (KB)" if v == vertices_list[0] else "")
        ax.get_legend().remove()

    handles, _ = axes[0].get_legend_handles_labels()
    fig.legend(
        handles,
        ["Lista de Adjacência (LA)", "Matriz de Adjacência (MA)"],
        loc="upper center",
        bbox_to_anchor=(0.5, 1.02),
        ncol=2,
        frameon=True,
    )
    fig.suptitle(
        "Ocupação de Memória das Estruturas de Grafo (LA vs. MA)",
        fontsize=12.5,
        fontweight="bold",
        y=1.10,
    )
    plt.tight_layout()
    for ext in ("png", "pdf"):
        fig.savefig(FIG_DIR / f"comparacao_memoria.{ext}", dpi=300, bbox_inches="tight")
    plt.close(fig)


def gerar_tabela_latex_fator_a(df_fa: pd.DataFrame, algoritmo: str) -> str:
    sub = df_fa[df_fa["algoritmo"] == algoritmo]
    linhas = [
        r"\begin{table}[htbp]",
        r"\centering",
        rf"\caption{{Comparação de tempos (ms) e teste Mann-Whitney U (Fator A: LA vs. MA) para {algoritmo}.}}",
        rf"\label{{tab:fator_a_{algoritmo.lower().replace('-', '_')}}}",
        r"\begin{tabular}{cccccc}",
        r"\hline",
        r"$|V|$ & $D$ & LA (média $\pm$ dp) & MA (média $\pm$ dp) & MA/LA & $p$-valor (MW) \\",
        r"\hline",
    ]
    for _, r in sub.iterrows():
        p_fmt = f"{r['p_mannwhitney']:.2e}" if r["p_mannwhitney"] < 0.001 else f"{r['p_mannwhitney']:.4f}"
        linhas.append(
            f"{int(r['vertices'])} & {r['densidade']:.1f} & "
            f"{r['media_LA_ms']:.3f} $\\pm$ {r['dp_LA_ms']:.3f} & "
            f"{r['media_MA_ms']:.3f} $\\pm$ {r['dp_MA_ms']:.3f} & "
            f"{r['razao_MA_LA']:.2f}x & {p_fmt} \\\\"
        )
    linhas += [r"\hline", r"\end{tabular}", r"\end{table}"]
    return "\n".join(linhas)


def main():
    sns.set_theme(style="whitegrid", context="paper", font_scale=1.15)
    FIG_DIR.mkdir(parents=True, exist_ok=True)

    df_t, df_m = carregar_dados()

    df_desc = estatisticas_descritivas(df_t)
    df_fa = testar_fator_a(df_t)
    df_fb_ext, df_fb_kw = testar_fator_b(df_t)

    df_desc.to_csv(OUT_DIR / "resumo_descritivo.csv", index=False)
    df_fa.to_csv(OUT_DIR / "teste_fator_a_LA_vs_MA.csv", index=False)
    df_fb_ext.to_csv(OUT_DIR / "teste_fator_b_extremos.csv", index=False)
    df_fb_kw.to_csv(OUT_DIR / "teste_fator_b_kruskal.csv", index=False)

    # Tabelas LaTeX prontas para Overleaf
    (OUT_DIR / "tabela_fator_a_bellman_ford.tex").write_text(
        gerar_tabela_latex_fator_a(df_fa, "Bellman-Ford"), encoding="utf-8"
    )
    (OUT_DIR / "tabela_fator_a_dijkstra.tex").write_text(
        gerar_tabela_latex_fator_a(df_fa, "Dijkstra"), encoding="utf-8"
    )

    gerar_boxplots(df_t, "Bellman-Ford", "boxplot_bellman_ford")
    gerar_boxplots(df_t, "Dijkstra", "boxplot_dijkstra")
    gerar_grafico_memoria(df_m)

    # Resumo no terminal
    n_norm = int(df_desc["normal_005"].sum())
    print(f"Normalidade (Shapiro-Wilk, alpha={ALPHA}): {n_norm}/{len(df_desc)} grupos normais")
    print(f"Fator A (H0' LA vs MA) rejeitadas (MW):    {int(df_fa['rejeita_H0_linha_mw'].sum())}/{len(df_fa)}")
    print(f"Fator B (H0'' 0.2 vs 1.0) rejeitadas (MW): {int(df_fb_ext['rejeita_H0_2linhas_mw'].sum())}/{len(df_fb_ext)}")
    print(f"Fator B (H0'' 5 dens.) rejeitadas (KW):    {int(df_fb_kw['rejeita_H0_2linhas_kw'].sum())}/{len(df_fb_kw)}")
    print(f"\nArquivos salvos em: {OUT_DIR}")


if __name__ == "__main__":
    main()
