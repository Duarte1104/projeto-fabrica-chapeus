package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContaEmpresaDto {

    private Long id;
    private BigDecimal saldoAtual;

    public ContaEmpresaDto() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}