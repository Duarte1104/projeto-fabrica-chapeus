package com.teuprojeto.desktop.view.gestor;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorNovaDespesaPage {

    private final GestorShellView shell;

    public GestorNovaDespesaPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Adicionar Despesa");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        ComboBox<String> produto = new ComboBox<>();
        MockGestorData.getMateriais().forEach(m -> produto.getItems().add(m.getNome()));

        TextField descricao = new TextField();
        TextField fornecedor = new TextField();
        TextField valor = new TextField();

        form.add(new Label("Produto"), 0, 0);
        form.add(produto, 0, 1);

        form.add(new Label("Descrição"), 0, 2);
        form.add(descricao, 0, 3);

        form.add(new Label("Fornecedor"), 0, 4);
        form.add(fornecedor, 0, 5);

        form.add(new Label("Valor"), 0, 6);
        form.add(valor, 0, 7);

        Button confirmar = GestorUiFactory.primaryButton("Confirmar Despesa");
        confirmar.setOnAction(e -> mostrarIndisponivel());

        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.DESPESAS));

        HBox buttons = new HBox(10, confirmar, cancelar);

        VBox card = GestorUiFactory.createCard();
        card.setMaxWidth(420);
        card.getChildren().addAll(form, buttons);

        root.getChildren().add(card);
        return root;
    }

    private void mostrarIndisponivel() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Função ainda não disponível");
        alert.setContentText("O registo de despesas só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}