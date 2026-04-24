package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
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