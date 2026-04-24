package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinhaEncomendaDto {

    private Long numencomenda;
    private Long codchapeu;
    private Long quantidade;

    public LinhaEncomendaDto() {
    }

    public Long getNumencomenda() {
        return numencomenda;
    }

    public void setNumencomenda(Long numencomenda) {
        this.numencomenda = numencomenda;
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}