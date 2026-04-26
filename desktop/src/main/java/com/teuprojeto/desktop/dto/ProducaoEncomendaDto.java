package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProducaoEncomendaDto {

    private Long id;
    private Long idEncomenda;
    private Boolean montagemConcluida;
    private String montagemComentario;
    private Boolean costurasConcluidas;
    private String costurasComentario;
    private Boolean personalizacaoConcluida;
    private String personalizacaoComentario;
    private String observacoes;
    private Boolean concluida;

    public ProducaoEncomendaDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(Long idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public Boolean getMontagemConcluida() {
        return montagemConcluida;
    }

    public void setMontagemConcluida(Boolean montagemConcluida) {
        this.montagemConcluida = montagemConcluida;
    }

    public String getMontagemComentario() {
        return montagemComentario;
    }

    public void setMontagemComentario(String montagemComentario) {
        this.montagemComentario = montagemComentario;
    }

    public Boolean getCosturasConcluidas() {
        return costurasConcluidas;
    }

    public void setCosturasConcluidas(Boolean costurasConcluidas) {
        this.costurasConcluidas = costurasConcluidas;
    }

    public String getCosturasComentario() {
        return costurasComentario;
    }

    public void setCosturasComentario(String costurasComentario) {
        this.costurasComentario = costurasComentario;
    }

    public Boolean getPersonalizacaoConcluida() {
        return personalizacaoConcluida;
    }

    public void setPersonalizacaoConcluida(Boolean personalizacaoConcluida) {
        this.personalizacaoConcluida = personalizacaoConcluida;
    }

    public String getPersonalizacaoComentario() {
        return personalizacaoComentario;
    }

    public void setPersonalizacaoComentario(String personalizacaoComentario) {
        this.personalizacaoComentario = personalizacaoComentario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Boolean getConcluida() {
        return concluida;
    }

    public void setConcluida(Boolean concluida) {
        this.concluida = concluida;
    }
}