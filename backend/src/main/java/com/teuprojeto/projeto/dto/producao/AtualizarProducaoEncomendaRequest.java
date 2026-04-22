package com.teuprojeto.projeto.dto.producao;

import java.math.BigDecimal;

public class AtualizarProducaoEncomendaRequest {

    private BigDecimal idEncomenda;
    private Boolean montagemConcluida;
    private String montagemComentario;
    private Boolean costurasConcluidas;
    private String costurasComentario;
    private Boolean personalizacaoConcluida;
    private String personalizacaoComentario;
    private String observacoes;
    private Boolean concluida;

    public AtualizarProducaoEncomendaRequest() {
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
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