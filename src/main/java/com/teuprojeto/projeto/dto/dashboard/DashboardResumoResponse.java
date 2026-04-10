package com.teuprojeto.projeto.dto.dashboard;

public class DashboardResumoResponse {

    private long totalEncomendas;
    private long aguardaDesign;
    private long emPreparacao;
    private long prontas;
    private long pagas;

    public DashboardResumoResponse() {
    }

    public long getTotalEncomendas() {
        return totalEncomendas;
    }

    public void setTotalEncomendas(long totalEncomendas) {
        this.totalEncomendas = totalEncomendas;
    }

    public long getAguardaDesign() {
        return aguardaDesign;
    }

    public void setAguardaDesign(long aguardaDesign) {
        this.aguardaDesign = aguardaDesign;
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

    public long getPagas() {
        return pagas;
    }

    public void setPagas(long pagas) {
        this.pagas = pagas;
    }
}