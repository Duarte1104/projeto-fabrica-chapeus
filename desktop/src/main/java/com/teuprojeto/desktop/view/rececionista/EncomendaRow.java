package com.teuprojeto.desktop.view.rececionista;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EncomendaRow {

    private final StringProperty numero;
    private final StringProperty cliente;
    private final StringProperty estado;
    private final StringProperty design;

    public EncomendaRow(String numero, String cliente, String estado, String design) {
        this.numero = new SimpleStringProperty(numero);
        this.cliente = new SimpleStringProperty(cliente);
        this.estado = new SimpleStringProperty(estado);
        this.design = new SimpleStringProperty(design);
    }

    public StringProperty numeroProperty() {
        return numero;
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty designProperty() {
        return design;
    }
}