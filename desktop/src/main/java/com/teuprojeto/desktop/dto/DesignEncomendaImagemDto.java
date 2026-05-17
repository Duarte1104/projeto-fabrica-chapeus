package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignEncomendaImagemDto {

    private Long id;
    private Long idDesignEncomenda;
    private String urlImagem;

    public DesignEncomendaImagemDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdDesignEncomenda() {
        return idDesignEncomenda;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdDesignEncomenda(Long idDesignEncomenda) {
        this.idDesignEncomenda = idDesignEncomenda;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}