package com.teuprojeto.desktop.view.gestor;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorNovoMaterialPage {

    private final GestorShellView shell;

    public GestorNovoMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Novo Material");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField();

        TextField stockAtual = new TextField();
        TextField stockMinimo = new TextField();

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");

        TextField custoUnitario = new TextField();

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
        alert.setContentText("O guardar material só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}