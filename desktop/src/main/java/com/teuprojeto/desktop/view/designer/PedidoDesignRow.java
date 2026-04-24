package com.teuprojeto.desktop.view.designer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PedidoDesignRow {

    private final Long encomendaId;
    private final StringProperty numero;
    private final StringProperty cliente;
    private final StringProperty dataEntrega;
    private final StringProperty descricaoDesign;

    public PedidoDesignRow(Long encomendaId, String numero, String cliente, String dataEntrega, String descricaoDesign) {
        this.encomendaId = encomendaId;
        this.numero = new SimpleStringProperty(numero);
        this.cliente = new SimpleStringProperty(cliente);
        this.dataEntrega = new SimpleStringProperty(dataEntrega);
        this.descricaoDesign = new SimpleStringProperty(descricaoDesign);
    }

    public Long getEncomendaId() {
        return encomendaId;
    }

    public StringProperty numeroProperty() {
        return numero;
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public StringProperty dataEntregaProperty() {
        return dataEntrega;
    }

    public StringProperty descricaoDesignProperty() {
        return descricaoDesign;
    }

    public String getNumero() {
        return numero.get();
    }

    public String getCliente() {
        return cliente.get();
    }

    public String getDataEntrega() {
        return dataEntrega.get();
    }

    public String getDescricaoDesign() {
        return descricaoDesign.get();
    }
}