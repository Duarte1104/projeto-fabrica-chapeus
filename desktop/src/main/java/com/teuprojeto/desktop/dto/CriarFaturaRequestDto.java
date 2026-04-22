package com.teuprojeto.desktop.dto;

import java.math.BigDecimal;

public class CriarFaturaRequestDto {

    private BigDecimal idEncomenda;
    private String observacoes;

    public CriarFaturaRequestDto() {
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}