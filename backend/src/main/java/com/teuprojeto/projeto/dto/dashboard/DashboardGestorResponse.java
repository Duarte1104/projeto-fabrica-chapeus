package com.teuprojeto.projeto.dto.dashboard;

import java.math.BigDecimal;

public class DashboardGestorResponse {

    private long totalEncomendas;
    private long aguardaDesign;
    private long emPreparacao;
    private long prontas;
    private long pagas;
    private BigDecimal saldoAtual;
    private long totalMovimentos;
    private long materiaisAbaixoMinimo;

    public DashboardGestorResponse() {
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

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public long getTotalMovimentos() {
        return totalMovimentos;
    }

    public void setTotalMovimentos(long totalMovimentos) {
        this.totalMovimentos = totalMovimentos;
    }

    public long getMateriaisAbaixoMinimo() {
        return materiaisAbaixoMinimo;
    }

    public void setMateriaisAbaixoMinimo(long materiaisAbaixoMinimo) {
        this.materiaisAbaixoMinimo = materiaisAbaixoMinimo;
    }
}