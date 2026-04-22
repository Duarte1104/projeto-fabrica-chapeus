package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "\"ContaEmpresa\"", schema = "public")
public class ContaEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"saldoatual\"", nullable = false)
    private BigDecimal saldoAtual;

    public ContaEmpresa() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}