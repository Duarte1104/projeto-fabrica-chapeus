package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"DesignEncomenda\"", schema = "public")
public class DesignEncomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"idencomenda\"", nullable = false)
    private java.math.BigDecimal idEncomenda;

    @Column(name = "\"descricaodesigner\"", length = 1000)
    private String descricaoDesigner;

    @Column(name = "\"ficheirodesign\"", length = 500)
    private String ficheiroDesign;

    @Column(name = "\"estadodesign\"", nullable = false, length = 50)
    private String estadoDesign;

    @Column(name = "\"datacriacao\"", nullable = false)
    private LocalDateTime dataCriacao;

    public DesignEncomenda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.math.BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(java.math.BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getDescricaoDesigner() {
        return descricaoDesigner;
    }

    public void setDescricaoDesigner(String descricaoDesigner) {
        this.descricaoDesigner = descricaoDesigner;
    }

    public String getFicheiroDesign() {
        return ficheiroDesign;
    }

    public void setFicheiroDesign(String ficheiroDesign) {
        this.ficheiroDesign = ficheiroDesign;
    }

    public String getEstadoDesign() {
        return estadoDesign;
    }

    public void setEstadoDesign(String estadoDesign) {
        this.estadoDesign = estadoDesign;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}