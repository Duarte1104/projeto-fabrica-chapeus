package com.teuprojeto.desktop.dto;

import java.math.BigDecimal;

public class CriarPagamentoRequestDto {

    private Long idFatura;
    private BigDecimal valorPago;
    private String metodoPagamento;
    private String observacoes;

    public CriarPagamentoRequestDto() {
    }

    public Long getIdFatura() {
        return idFatura;
    }

    public void setIdFatura(Long idFatura) {
        this.idFatura = idFatura;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}