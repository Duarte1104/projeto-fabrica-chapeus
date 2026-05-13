package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tamanho", schema = "public")
public class Tamanho {

    @Id
    @Column(name = "codigo", length = 10)
    private String codigo;

    @Column(name = "multiplicador", nullable = false)
    private BigDecimal multiplicador;

    public Tamanho() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(BigDecimal multiplicador) {
        this.multiplicador = multiplicador;
    }
}