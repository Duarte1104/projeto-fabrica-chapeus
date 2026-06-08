package com.teuprojeto.desktop.dto;

public class CriarReciboRequestDto {

    private Long idPagamento;
    private String observacoes;

    public CriarReciboRequestDto() {
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