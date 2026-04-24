package com.teuprojeto.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EncomendaDto {

    private Long num;
    private String data;
    private String hora;
    private String dataEntrega;
    private String observacoes;
    private BigDecimal valortotal;
    private Long idestado;
    private Integer idcliente;
    private Long idfuncionario;
    private Boolean design;
    private String descricaoDesign;

    public EncomendaDto() {
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
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