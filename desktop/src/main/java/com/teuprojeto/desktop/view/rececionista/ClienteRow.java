package com.teuprojeto.desktop.view.rececionista;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClienteRow {

    private final StringProperty nome;
    private final StringProperty email;
    private final StringProperty telefone;
    private final StringProperty tipo;

    public ClienteRow(String nome, String email, String telefone, String tipo) {
        this.nome = new SimpleStringProperty(nome);
        this.email = new SimpleStringProperty(email);
        this.telefone = new SimpleStringProperty(telefone);
        this.tipo = new SimpleStringProperty(tipo);
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty telefoneProperty() {
        return telefone;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }
}