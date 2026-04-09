package com.teuprojeto.projeto.dto.gestor;

import java.math.BigDecimal;

public class CriarMaterialRequest {

    private String nome;
    private BigDecimal stockAtual;
    private BigDecimal stockMinimo;
    private String unidade;
    private BigDecimal custoUnitario;

    public CriarMaterialRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getStockAtual() {
        return stockAtual;
    }

    public void setStockAtual(BigDecimal stockAtual) {
        this.stockAtual = stockAtual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }
}