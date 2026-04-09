package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "\"GastoMaterial\"", schema = "public")
public class GastoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"idencomenda\"", nullable = false)
    private BigDecimal idEncomenda;

    @Column(name = "\"material\"", nullable = false, length = 200)
    private String material;

    @Column(name = "\"quantidade\"", nullable = false)
    private BigDecimal quantidade;

    @Column(name = "\"observacoes\"", length = 1000)
    private String observacoes;

    public GastoMaterial() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}