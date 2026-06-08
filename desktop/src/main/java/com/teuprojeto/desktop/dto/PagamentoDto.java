package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagamentoDto {

    private Long cod;
    private BigDecimal valorpago;
    private BigDecimal idencomenda;
    private Long numfatura;
    private String datapagamento;
    private String metodopagamento;
    private String observacoes;

    public PagamentoDto() {
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public BigDecimal getValorpago() {
        return valorpago;
    }

    public void setValorpago(BigDecimal valorpago) {
        this.valorpago = valorpago;
    }

    public BigDecimal getIdencomenda() {
        return idencomenda;
    }

    public void setIdencomenda(BigDecimal idencomenda) {
        this.idencomenda = idencomenda;
    }

    public Long getNumfatura() {
        return numfatura;
    }

    public void setNumfatura(Long numfatura) {
        this.numfatura = numfatura;
    }

    public String getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(String datapagamento) {
        this.datapagamento = datapagamento;
    }

    public String getMetodopagamento() {
        return metodopagamento;
    }

    public void setMetodopagamento(String metodopagamento) {
        this.metodopagamento = metodopagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}