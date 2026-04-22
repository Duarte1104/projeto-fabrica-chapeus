package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "material", schema = "public")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "stockatual", nullable = false)
    private BigDecimal stockAtual;

    @Column(name = "stockminimo", nullable = false)
    private BigDecimal stockMinimo;

    @Column(name = "unidade", nullable = false, length = 50)
    private String unidade;

    @Column(name = "custounitario", nullable = false)
    private BigDecimal custoUnitario;

    public Material() {
    }

    public Long getId() {
        return id;
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