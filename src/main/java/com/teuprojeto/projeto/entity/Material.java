package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "\"Material\"", schema = "public")
public class Material {

    @Id
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"qtdstock\"")
    private Long qtdstock;

    @Column(name = "\"preco\"")
    private BigDecimal preco;

    @Column(name = "\"numfornecedor\"")
    private Long numfornecedor;

    public Material() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQtdstock() {
        return qtdstock;
    }

    public void setQtdstock(Long qtdstock) {
        this.qtdstock = qtdstock;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Long getNumfornecedor() {
        return numfornecedor;
    }

    public void setNumfornecedor(Long numfornecedor) {
        this.numfornecedor = numfornecedor;
    }
}