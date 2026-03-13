package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "\"Encomenda\"", schema = "public")
public class Encomenda {

    @Id
    @Column(name = "\"num\"")
    private BigDecimal num;

    @Column(name = "\"data\"")
    private LocalDate data;

    @Column(name = "\"hora\"")
    private LocalTime hora;

    @Column(name = "\"valortotal\"")
    private BigDecimal valortotal;

    @Column(name = "\"idestado\"")
    private Long idestado;

    @Column(name = "\"idcliente\"")
    private Long idcliente;

    @Column(name = "\"idfornecedor\"")
    private Long idfornecedor;

    public Encomenda() {
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
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

    public Long getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Long idcliente) {
        this.idcliente = idcliente;
    }

    public Long getIdfornecedor() {
        return idfornecedor;
    }

    public void setIdfornecedor(Long idfornecedor) {
        this.idfornecedor = idfornecedor;
    }
}