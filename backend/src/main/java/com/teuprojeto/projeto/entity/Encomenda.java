package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "\"Encomenda\"", schema = "public")
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"num\"")
    private Long num;

    @Column(name = "\"data\"", nullable = false)
    private LocalDate data;

    @Column(name = "\"hora\"", nullable = false)
    private LocalTime hora;

    @Column(name = "\"dataentrega\"", nullable = false)
    private LocalDate dataEntrega;

    @Column(name = "\"observacoes\"", length = 1000)
    private String observacoes;

    @Column(name = "\"valortotal\"", nullable = false)
    private BigDecimal valortotal;

    @Column(name = "\"idestado\"", nullable = false)
    private Long idestado;

    @Column(name = "\"idcliente\"", nullable = false)
    private Integer idcliente;

    @Column(name = "\"idfuncionario\"")
    private Long idfuncionario;

    @Column(name = "\"design\"")
    private Boolean design;

    @Column(name = "\"descricaodesign\"", length = 1000)
    private String descricaoDesign;

    public Encomenda() {
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getValortotal() {
        return valortotal;
    }

    public void setValortotal(BigDecimal valortotal) {
        this.valortotal = valortotal;
    }

    public Long getIdestado() {
        return idestado;
    }

    public void setIdestado(Long idestado) {
        this.idestado = idestado;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public Long getIdfuncionario() {
        return idfuncionario;
    }

    public void setIdfuncionario(Long idfuncionario) {
        this.idfuncionario = idfuncionario;
    }

    public Boolean getDesign() {
        return design;
    }

    public void setDesign(Boolean design) {
        this.design = design;
    }

    public String getDescricaoDesign() {
        return descricaoDesign;
    }

    public void setDescricaoDesign(String descricaoDesign) {
        this.descricaoDesign = descricaoDesign;
    }
}