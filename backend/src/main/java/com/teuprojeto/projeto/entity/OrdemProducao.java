package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "\"OrdemProducao\"", schema = "public")
public class OrdemProducao {

    @Id
    @Column(name = "\"id\"")
    private Long id;

    @Column(name = "\"datainicio\"")
    private LocalDate datainicio;

    @Column(name = "\"datafim\"")
    private LocalDate datafim;

    @Column(name = "\"quantidade\"")
    private Long quantidade;

    @Column(name = "\"idetapa\"")
    private Long idetapa;

    @Column(name = "\"codchapeu\"")
    private Long codchapeu;

    public OrdemProducao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatainicio() {
        return datainicio;
    }

    public void setDatainicio(LocalDate datainicio) {
        this.datainicio = datainicio;
    }

    public LocalDate getDatafim() {
        return datafim;
    }

    public void setDatafim(LocalDate datafim) {
        this.datafim = datafim;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Long getIdetapa() {
        return idetapa;
    }

    public void setIdetapa(Long idetapa) {
        this.idetapa = idetapa;
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }
}