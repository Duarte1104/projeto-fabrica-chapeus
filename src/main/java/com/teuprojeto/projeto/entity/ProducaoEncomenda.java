package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "\"ProducaoEncomenda\"", schema = "public")
public class ProducaoEncomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"idencomenda\"", nullable = false)
    private BigDecimal idEncomenda;

    @Column(name = "\"montagemconcluida\"")
    private Boolean montagemConcluida;

    @Column(name = "\"montagemcomentario\"", length = 1000)
    private String montagemComentario;

    @Column(name = "\"costurasconcluidas\"")
    private Boolean costurasConcluidas;

    @Column(name = "\"costurascomentario\"", length = 1000)
    private String costurasComentario;

    @Column(name = "\"personalizacaoconcluida\"")
    private Boolean personalizacaoConcluida;

    @Column(name = "\"personalizacaocomentario\"", length = 1000)
    private String personalizacaoComentario;

    @Column(name = "\"observacoes\"", length = 1000)
    private String observacoes;

    @Column(name = "\"concluida\"")
    private Boolean concluida;

    public ProducaoEncomenda() {
    }

    public Long getId() {
        return id;
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