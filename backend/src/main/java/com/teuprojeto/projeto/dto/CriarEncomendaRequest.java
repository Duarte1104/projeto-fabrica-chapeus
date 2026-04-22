package com.teuprojeto.projeto.dto;

import java.util.List;

public class CriarEncomendaRequest {

    private Long idCliente;
    private Boolean temPersonalizacao;
    private List<LinhaEncomendaRequest> linhas;

    public CriarEncomendaRequest() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Boolean getTemPersonalizacao() {
        return temPersonalizacao;
    }

    public void setTemPersonalizacao(Boolean temPersonalizacao) {
        this.temPersonalizacao = temPersonalizacao;
    }

    public List<LinhaEncomendaRequest> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<LinhaEncomendaRequest> linhas) {
        this.linhas = linhas;
    }
}