package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignEncomendaDto {

    private Long id;
    private BigDecimal idEncomenda;
    private String descricaoDesigner;
    private String ficheiroDesign;
    private String estadoDesign;
    private String dataCriacao;

    public DesignEncomendaDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getDescricaoDesigner() {
        return descricaoDesigner;
    }

    public void setDescricaoDesigner(String descricaoDesigner) {
        this.descricaoDesigner = descricaoDesigner;
    }

    public String getFicheiroDesign() {
        return ficheiroDesign;
    }

    public void setFicheiroDesign(String ficheiroDesign) {
        this.ficheiroDesign = ficheiroDesign;
    }

    public String getEstadoDesign() {
        return estadoDesign;
    }

    public void setEstadoDesign(String estadoDesign) {
        this.estadoDesign = estadoDesign;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}