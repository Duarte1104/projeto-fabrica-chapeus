package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"MovimentoFinanceiro\"", schema = "public")
public class MovimentoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"tipo\"", nullable = false, length = 20)
    private String tipo;

    @Column(name = "\"valor\"", nullable = false)
    private BigDecimal valor;

    @Column(name = "\"descricao\"", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "\"origem\"", length = 100)
    private String origem;

    @Column(name = "\"data\"", nullable = false)
    private LocalDateTime data;

    public MovimentoFinanceiro() {
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}