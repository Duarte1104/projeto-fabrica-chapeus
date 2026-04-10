package com.teuprojeto.desktop.view.rececionista;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FaturaRow {

    private final StringProperty numero;
    private final StringProperty encomenda;
    private final StringProperty valor;
    private final StringProperty data;

    public FaturaRow(String numero, String encomenda, String valor, String data) {
        this.numero = new SimpleStringProperty(numero);
        this.encomenda = new SimpleStringProperty(encomenda);
        this.valor = new SimpleStringProperty(valor);
        this.data = new SimpleStringProperty(data);
    }

    public StringProperty numeroProperty() {
        return numero;
    }

    public StringProperty encomendaProperty() {
        return encomenda;
    }

    public StringProperty valorProperty() {
        return valor;
    }

    public StringProperty dataProperty() {
        return data;
    }
}