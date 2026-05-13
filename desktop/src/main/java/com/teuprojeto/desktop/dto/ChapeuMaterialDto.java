package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapeuMaterialDto {

    private Long id;
    private Long idChapeu;
    private Long idMaterial;
    private BigDecimal quantidadePorUnidade;

    public ChapeuMaterialDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdChapeu() {
        return idChapeu;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public BigDecimal getQuantidadePorUnidade() {
        return quantidadePorUnidade;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdChapeu(Long idChapeu) {
        this.idChapeu = idChapeu;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public void setQuantidadePorUnidade(BigDecimal quantidadePorUnidade) {
        this.quantidadePorUnidade = quantidadePorUnidade;
    }
}