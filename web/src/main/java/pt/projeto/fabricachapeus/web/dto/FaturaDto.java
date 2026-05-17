package pt.projeto.fabricachapeus.web.dto;

import java.math.BigDecimal;

public class FaturaDto {

    private Long id;
    private BigDecimal idEncomenda;
    private String data;
    private BigDecimal valor;
    private String observacoes;

    public Long getId() {
        return id;
    }

    public BigDecimal getIdEncomenda() {
        return idEncomenda;
    }

    public String getData() {
        return data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdEncomenda(BigDecimal idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}