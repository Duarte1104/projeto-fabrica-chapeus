package com.teuprojeto.desktop.view.designer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class PedidoDesignRow {

    private final StringProperty codigoEncomenda;
    private final StringProperty cliente;
    private final StringProperty produto;
    private final IntegerProperty quantidade;
    private final StringProperty data;
    private final StringProperty descricaoPedido;
    private final StringProperty estadoDesign;
    private final StringProperty observacoes;

    public PedidoDesignRow(String codigoEncomenda,
                           String cliente,
                           String produto,
                           int quantidade,
                           String data,
                           String descricaoPedido,
                           String estadoDesign,
                           String observacoes) {
        this.codigoEncomenda = new SimpleStringProperty(codigoEncomenda);
        this.cliente = new SimpleStringProperty(cliente);
        this.produto = new SimpleStringProperty(produto);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.data = new SimpleStringProperty(data);
        this.descricaoPedido = new SimpleStringProperty(descricaoPedido);
        this.estadoDesign = new SimpleStringProperty(estadoDesign);
        this.observacoes = new SimpleStringProperty(observacoes);
    }

    public StringProperty codigoEncomendaProperty() {
        return codigoEncomenda;
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public StringProperty produtoProperty() {
        return produto;
    }

    public IntegerProperty quantidadeProperty() {
        return quantidade;
    }

    public StringProperty dataProperty() {
        return data;
    }

    public StringProperty descricaoPedidoProperty() {
        return descricaoPedido;
    }

    public StringProperty estadoDesignProperty() {
        return estadoDesign;
    }

    public StringProperty observacoesProperty() {
        return observacoes;
    }

    public String getCodigoEncomenda() {
        return codigoEncomenda.get();
    }

    public String getCliente() {
        return cliente.get();
    }

    public String getProduto() {
        return produto.get();
    }

    public int getQuantidade() {
        return quantidade.get();
    }

    public String getData() {
        return data.get();
    }

    public String getDescricaoPedido() {
        return descricaoPedido.get();
    }

    public String getEstadoDesign() {
        return estadoDesign.get();
    }

    public String getObservacoes() {
        return observacoes.get();
    }
}