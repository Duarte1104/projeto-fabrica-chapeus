package com.teuprojeto.projeto.dto;

public class LinhaEncomendaRequest {

    private Long codChapeu;
    private Long quantidade;

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
}