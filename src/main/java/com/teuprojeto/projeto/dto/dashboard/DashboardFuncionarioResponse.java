package com.teuprojeto.projeto.dto.dashboard;

public class DashboardFuncionarioResponse {

    private long emPreparacao;
    private long prontas;
    private long totalRegistosProducao;
    private long totalGastosMaterial;

    public DashboardFuncionarioResponse() {
    }

    public long getEmPreparacao() {
        return emPreparacao;
    }

    public void setEmPreparacao(long emPreparacao) {
        this.emPreparacao = emPreparacao;
    }

    public long getProntas() {
        return prontas;
    }

    public void setProntas(long prontas) {
        this.prontas = prontas;
    }

    public long getTotalRegistosProducao() {
        return totalRegistosProducao;
    }

    public void setTotalRegistosProducao(long totalRegistosProducao) {
        this.totalRegistosProducao = totalRegistosProducao;
    }

    public long getTotalGastosMaterial() {
        return totalGastosMaterial;
    }

    public void setTotalGastosMaterial(long totalGastosMaterial) {
        this.totalGastosMaterial = totalGastosMaterial;
    }
}