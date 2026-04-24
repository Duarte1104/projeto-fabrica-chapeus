package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardRececionistaResponseDto {

    private long totalClientes;
    private long totalEncomendas;
    private long aguardaDesign;
    private long emPreparacao;
    private long prontas;
    private long pagas;
    private long totalFaturas;

    public DashboardRececionistaResponseDto() {
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(long totalClientes) {
        this.totalClientes = totalClientes;
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

    public long getTotalFaturas() {
        return totalFaturas;
    }

    public void setTotalFaturas(long totalFaturas) {
        this.totalFaturas = totalFaturas;
    }
}