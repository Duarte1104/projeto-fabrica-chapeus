package com.teuprojeto.projeto.dto.rececionista;

import java.math.BigDecimal;

public class CriarFaturaRequest {

    private BigDecimal idEncomenda;
    private String observacoes;

    public CriarFaturaRequest() {
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