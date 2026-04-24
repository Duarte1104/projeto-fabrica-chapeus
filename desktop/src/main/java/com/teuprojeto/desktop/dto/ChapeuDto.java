package com.teuprojeto.desktop.dto;

import java.math.BigDecimal;

public class ChapeuDto {

    private Long cod;
    private String nome;
    private BigDecimal precoactvenda;

    public ChapeuDto() {
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