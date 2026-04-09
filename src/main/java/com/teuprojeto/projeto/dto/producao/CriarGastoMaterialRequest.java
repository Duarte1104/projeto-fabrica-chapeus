package com.teuprojeto.projeto.dto.producao;

import java.math.BigDecimal;

public class CriarGastoMaterialRequest {

    private BigDecimal idEncomenda;
    private String material;
    private BigDecimal quantidade;
    private String observacoes;

    public CriarGastoMaterialRequest() {
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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