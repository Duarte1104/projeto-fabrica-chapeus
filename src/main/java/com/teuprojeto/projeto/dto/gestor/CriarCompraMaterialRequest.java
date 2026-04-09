package com.teuprojeto.projeto.dto.gestor;

import java.math.BigDecimal;

public class CriarCompraMaterialRequest {

    private Long idMaterial;
    private BigDecimal quantidade;
    private String observacoes;

    public CriarCompraMaterialRequest() {
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}