package com.teuprojeto.desktop.view.gestor;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GestorStockPage {

    private final GestorShellView shell;

    public GestorStockPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Consultar Stock");

        HBox actions = new HBox(10);

        TextField search = new TextField();
        search.setPromptText("Pesquisar material...");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button novoMaterial = GestorUiFactory.primaryButton("Novo Material");
        novoMaterial.setOnAction(e -> shell.navigateTo(GestorPage.NOVO_MATERIAL));

        actions.getChildren().addAll(search, novoMaterial);

        TableView<MaterialRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MaterialRow, String> nome = new TableColumn<>("Material");
        nome.setCellValueFactory(c -> c.getValue().nomeProperty());

        TableColumn<MaterialRow, Number> stockAtual = new TableColumn<>("Stock Atual");
        stockAtual.setCellValueFactory(c -> c.getValue().stockAtualProperty());

        TableColumn<MaterialRow, Number> stockMinimo = new TableColumn<>("Stock Mínimo");
        stockMinimo.setCellValueFactory(c -> c.getValue().stockMinimoProperty());

        TableColumn<MaterialRow, String> unidade = new TableColumn<>("Unidade");
        unidade.setCellValueFactory(c -> c.getValue().unidadeProperty());

        TableColumn<MaterialRow, Number> custo = new TableColumn<>("Custo Unitário");
        custo.setCellValueFactory(c -> c.getValue().custoUnitarioProperty());
        custo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f €", item.doubleValue()));
            }
        });

        TableColumn<MaterialRow, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getStockAtual() <= c.getValue().getStockMinimo() ? "Baixo" : "Alto"
        ));
        estado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setGraphic(null);
                    return;
                }

                Label label = new Label(value);
                label.setStyle(
                        "Baixo".equals(value)
                                ? "-fx-background-color: #ff3b30; -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 12;"
                                : "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 12;"
                );

                HBox box = new HBox(label);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        TableColumn<MaterialRow, Void> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button editarBtn = new Button("Editar");
            private final HBox box = new HBox(editarBtn);

            {
                box.setAlignment(Pos.CENTER);
                editarBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
                editarBtn.setOnAction(e -> {
                    MaterialRow material = getTableView().getItems().get(getIndex());
                    shell.setMaterialSelecionado(material);
                    shell.navigateTo(GestorPage.EDITAR_MATERIAL);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(nome, stockAtual, stockMinimo, unidade, custo, estado, acao);

        FilteredList<MaterialRow> filtrados = new FilteredList<>(MockGestorData.getMateriais(), material -> true);

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String termo = newValue == null ? "" : newValue.trim().toLowerCase();

            filtrados.setPredicate(material -> {
                if (termo.isBlank()) {
                    return true;
                }

                return material.getNome().toLowerCase().contains(termo)
                        || material.getUnidade().toLowerCase().contains(termo)
                        || String.valueOf(material.getStockAtual()).contains(termo)
                        || String.valueOf(material.getStockMinimo()).contains(termo);
            });
        });

        SortedList<MaterialRow> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);

        VBox card = GestorUiFactory.createCard();
        card.getChildren().addAll(actions, table);

        root.getChildren().add(card);
        return root;
    }
}