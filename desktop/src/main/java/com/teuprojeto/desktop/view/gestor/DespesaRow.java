package com.teuprojeto.desktop.view.gestor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DespesaRow {

    private final StringProperty codigo;
    private final StringProperty data;
    private final StringProperty produto;
    private final StringProperty quantidade;
    private final StringProperty descricao;
    private final StringProperty valor;

    public DespesaRow(String codigo, String data, String produto, String quantidade, String descricao, String valor) {
        this.codigo = new SimpleStringProperty(codigo);
        this.data = new SimpleStringProperty(data);
        this.produto = new SimpleStringProperty(produto);
        this.quantidade = new SimpleStringProperty(quantidade);
        this.descricao = new SimpleStringProperty(descricao);
        this.valor = new SimpleStringProperty(valor);
    }

    public String getCodigo() {
        return codigo.get();
    }

    public String getData() {
        return data.get();
    }

    public String getProduto() {
        return produto.get();
    }

    public String getQuantidade() {
        return quantidade.get();
    }

    public String getDescricao() {
        return descricao.get();
    }

    public String getValor() {
        return valor.get();
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public StringProperty dataProperty() {
        return data;
    }

    public StringProperty produtoProperty() {
        return produto;
    }

    public StringProperty quantidadeProperty() {
        return quantidade;
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public StringProperty valorProperty() {
        return valor;
    }
}