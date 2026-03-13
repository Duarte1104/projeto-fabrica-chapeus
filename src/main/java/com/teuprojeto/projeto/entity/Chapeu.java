package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "\"Chapeu\"", schema = "public")
public class Chapeu {

    @Id
    @Column(name = "\"cod\"")
    private Long cod;

    @Column(name = "\"nome\"")
    private String nome;

    @Column(name = "\"precoactvenda\"")
    private BigDecimal precoactvenda;

    public Chapeu() {
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

    public BigDecimal getPrecoactvenda() {
        return precoactvenda;
    }

    public void setPrecoactvenda(BigDecimal precoactvenda) {
        this.precoactvenda = precoactvenda;
    }
}