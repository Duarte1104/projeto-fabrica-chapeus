package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra_material", schema = "public")
public class CompraMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idmaterial", nullable = false)
    private Long idMaterial;

    @Column(name = "quantidade", nullable = false)
    private BigDecimal quantidade;

    @Column(name = "custototal", nullable = false)
    private BigDecimal custoTotal;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    public CompraMaterial() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}