package com.teuprojeto.projeto.dto.design;

import java.math.BigDecimal;

public class CriarDesignEncomendaRequest {

    private BigDecimal idEncomenda;
    private String descricaoDesigner;
    private String ficheiroDesign;

    public CriarDesignEncomendaRequest() {
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
}