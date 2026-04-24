package com.teuprojeto.desktop.view.rececionista;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EncomendaRow {

    private final Long id;
    private final StringProperty numero;
    private final StringProperty cliente;
    private final StringProperty estado;
    private final StringProperty design;

    public EncomendaRow(Long id, String numero, String cliente, String estado, String design) {
        this.id = id;
        this.numero = new SimpleStringProperty(numero);
        this.cliente = new SimpleStringProperty(cliente);
        this.estado = new SimpleStringProperty(estado);
        this.design = new SimpleStringProperty(design);
    }

    public Long getId() {
        return id;
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

    public String getNumero() {
        return numero.get();
    }

    public String getCliente() {
        return cliente.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public String getDesign() {
        return design.get();
    }
}