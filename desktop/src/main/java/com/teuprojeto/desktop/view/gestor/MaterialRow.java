package com.teuprojeto.desktop.view.gestor;

import javafx.beans.property.*;

public class MaterialRow {

    private final LongProperty id;
    private final StringProperty nome;
    private final DoubleProperty stockAtual;
    private final DoubleProperty stockMinimo;
    private final StringProperty unidade;
    private final DoubleProperty custoUnitario;

    public MaterialRow(long id, String nome, double stockAtual, double stockMinimo, String unidade, double custoUnitario) {
        this.id = new SimpleLongProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.stockAtual = new SimpleDoubleProperty(stockAtual);
        this.stockMinimo = new SimpleDoubleProperty(stockMinimo);
        this.unidade = new SimpleStringProperty(unidade);
        this.custoUnitario = new SimpleDoubleProperty(custoUnitario);
    }

    public LongProperty idProperty() {
        return id;
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

    public long getId() {
        return id.get();
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

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public void setStockAtual(double stockAtual) {
        this.stockAtual.set(stockAtual);
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo.set(stockMinimo);
    }

    public void setUnidade(String unidade) {
        this.unidade.set(unidade);
    }

    public void setCustoUnitario(double custoUnitario) {
        this.custoUnitario.set(custoUnitario);
    }
}