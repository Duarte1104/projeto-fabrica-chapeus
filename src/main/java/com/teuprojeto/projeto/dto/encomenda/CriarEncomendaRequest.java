package com.teuprojeto.projeto.dto.encomenda;

import java.time.LocalDate;
import java.util.List;

public class CriarEncomendaRequest {

    private Integer idCliente;
    private LocalDate dataEntrega;
    private String observacoes;
    private List<LinhaEncomendaRequest> linhas;
    private Boolean design;
    private String descricaoDesign;

    public CriarEncomendaRequest() {
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<LinhaEncomendaRequest> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<LinhaEncomendaRequest> linhas) {
        this.linhas = linhas;
    }

    public Boolean getDesign() {
        return design;
    }

    public void setDesign(Boolean design) {
        this.design = design;
    }

    public String getDescricaoDesign() {
        return descricaoDesign;
    }

    public void setDescricaoDesign(String descricaoDesign) {
        this.descricaoDesign = descricaoDesign;
    }


}