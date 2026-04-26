package com.teuprojeto.desktop.view.gestor;

import javafx.beans.property.*;

public class MaterialRow {

    private final Long id;
    private final StringProperty nome;
    private final DoubleProperty stockAtual;
    private final DoubleProperty stockMinimo;
    private final StringProperty unidade;
    private final DoubleProperty custoUnitario;

    public MaterialRow(Long id, String nome, double stockAtual, double stockMinimo, String unidade, double custoUnitario) {
        this.id = id;
        this.nome = new SimpleStringProperty(nome);
        this.stockAtual = new SimpleDoubleProperty(stockAtual);
        this.stockMinimo = new SimpleDoubleProperty(stockMinimo);
        this.unidade = new SimpleStringProperty(unidade);
        this.custoUnitario = new SimpleDoubleProperty(custoUnitario);
    }

    public MaterialRow(long id, String nome, double stockAtual, double stockMinimo, String unidade, double custoUnitario) {
        this(Long.valueOf(id), nome, stockAtual, stockMinimo, unidade, custoUnitario);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome.get();
    }

    public double getStockAtual() {
        return stockAtual.get();
    }

    public double getStockMinimo() {
        return stockMinimo.get();
    }

    public String getUnidade() {
        return unidade.get();
    }

    public double getCustoUnitario() {
        return custoUnitario.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public DoubleProperty stockAtualProperty() {
        return stockAtual;
    }

    public DoubleProperty stockMinimoProperty() {
        return stockMinimo;
    }

    public StringProperty unidadeProperty() {
        return unidade;
    }

    public DoubleProperty custoUnitarioProperty() {
        return custoUnitario;
    }
}