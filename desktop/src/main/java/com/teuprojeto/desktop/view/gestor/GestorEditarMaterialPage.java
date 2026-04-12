package com.teuprojeto.desktop.view.gestor;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorEditarMaterialPage {

    private final GestorShellView shell;

    public GestorEditarMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        MaterialRow material = shell.getMaterialSelecionado();

        VBox root = GestorUiFactory.createPageContainer("Editar Material");

        if (material == null) {
            VBox card = GestorUiFactory.createCard();

            Label aviso = new Label("Nenhum material foi selecionado.");
            Button voltar = GestorUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return root;
        }

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField(material.getNome());

        TextField stockAtual = new TextField(String.valueOf(material.getStockAtual()));
        TextField stockMinimo = new TextField(String.valueOf(material.getStockMinimo()));

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");
        unidade.setValue(material.getUnidade());

        TextField custoUnitario = new TextField(String.valueOf(material.getCustoUnitario()));
        TextField adicionarStock = new TextField();
        adicionarStock.setPromptText("Ex: 20");

        form.add(new Label("Nome"), 0, 0);
        form.add(nome, 0, 1);

        form.add(new Label("Stock Atual"), 1, 0);
        form.add(stockAtual, 1, 1);

        form.add(new Label("Stock Mínimo"), 0, 2);
        form.add(stockMinimo, 0, 3);

        form.add(new Label("Unidade"), 1, 2);
        form.add(unidade, 1, 3);

        form.add(new Label("Custo Unitário"), 0, 4);
        form.add(custoUnitario, 0, 5);

        form.add(new Label("Acrescentar Stock"), 1, 4);
        form.add(adicionarStock, 1, 5);

        Button guardar = GestorUiFactory.primaryButton("Guardar");
        guardar.setOnAction(e -> mostrarIndisponivel());

        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        HBox buttons = new HBox(10, guardar, cancelar);

        VBox card = GestorUiFactory.createCard();
        card.getChildren().addAll(form, buttons);

        root.getChildren().add(card);
        return root;
    }

    private void mostrarIndisponivel() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Função ainda não disponível");
        alert.setContentText("A edição de material só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}