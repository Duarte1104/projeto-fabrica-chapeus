package com.teuprojeto.projeto.dto.encomenda;

import java.math.BigDecimal;

public class LinhaEncomendaRequest {

    private Long codChapeu;
    private Long quantidade;
    private BigDecimal precoUnitario;

    public LinhaEncomendaRequest() {
    }

    public Long getCodChapeu() {
        return codChapeu;
    }

    public void setCodChapeu(Long codChapeu) {
        this.codChapeu = codChapeu;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}