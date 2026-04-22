package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "\"Pagamento\"", schema = "public")
public class Pagamento {

    @Id
    @Column(name = "\"cod\"")
    private Long cod;

    @Column(name = "\"nome\"")
    private String nome;

    @Column(name = "\"valorpago\"")
    private BigDecimal valorpago;

    @Column(name = "\"idencomenda\"")
    private BigDecimal idencomenda;

    @Column(name = "\"numfatura\"")
    private Long numfatura;

    public Pagamento() {
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorpago() {
        return valorpago;
    }

    public void setValorpago(BigDecimal valorpago) {
        this.valorpago = valorpago;
    }

    public BigDecimal getIdencomenda() {
        return idencomenda;
    }

    public void setIdencomenda(BigDecimal idencomenda) {
        this.idencomenda = idencomenda;
    }

    public Long getNumfatura() {
        return numfatura;
    }

    public void setNumfatura(Long numfatura) {
        this.numfatura = numfatura;
    }
}