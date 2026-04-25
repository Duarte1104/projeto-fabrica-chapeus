package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialDto {

    private Long id;
    private String nome;
    private BigDecimal stockAtual;
    private BigDecimal stockMinimo;
    private String unidade;
    private BigDecimal custoUnitario;

    public MaterialDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getStockAtual() {
        return stockAtual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public String getUnidade() {
        return unidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setStockAtual(BigDecimal stockAtual) {
        this.stockAtual = stockAtual;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }
}