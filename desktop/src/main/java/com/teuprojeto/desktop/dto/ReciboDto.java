package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReciboDto {

    private Long id;
    private Long idPagamento;
    private Long numFatura;
    private BigDecimal idEncomenda;
    private BigDecimal valor;
    private String data;
    private String observacoes;

    public ReciboDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public Long getNumFatura() {
        return numFatura;
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getData() {
        return data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public void setNumFatura(Long numFatura) {
        this.numFatura = numFatura;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}