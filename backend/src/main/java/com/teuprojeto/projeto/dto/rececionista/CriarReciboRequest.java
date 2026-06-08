package com.teuprojeto.projeto.dto.rececionista;

public class CriarReciboRequest {

    private Long idPagamento;
    private String observacoes;

    public CriarReciboRequest() {
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}