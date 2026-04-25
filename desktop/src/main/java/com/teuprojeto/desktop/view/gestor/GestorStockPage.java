package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class GestorStockPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

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

        Button atualizar = GestorUiFactory.secondaryButton("Atualizar");
        actions.getChildren().addAll(search, novoMaterial, atualizar);

        Label status = new Label("A carregar materiais...");
        status.setStyle("-fx-text-fill: #666666;");

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

        ObservableList<MaterialRow> masterData = FXCollections.observableArrayList();
        FilteredList<MaterialRow> filtrados = new FilteredList<>(masterData, material -> true);

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

        atualizar.setOnAction(e -> carregarMateriais(masterData, status, table));
        carregarMateriais(masterData, status, table);

        VBox card = GestorUiFactory.createCard();
        card.getChildren().addAll(actions, status, table);

        root.getChildren().add(card);
        return root;
    }

    private void carregarMateriais(ObservableList<MaterialRow> masterData, Label status, TableView<MaterialRow> table) {
        status.setText("A carregar materiais...");
        table.setDisable(true);

        Task<List<MaterialRow>> task = new Task<>() {
            @Override
            protected List<MaterialRow> call() {
                return materialApiService.listarTodos().stream()
                        .map(this::toRow)
                        .toList();
            }

            private MaterialRow toRow(MaterialDto dto) {
                return new MaterialRow(
                        dto.getId(),
                        dto.getNome(),
                        dto.getStockAtual() == null ? 0 : dto.getStockAtual().doubleValue(),
                        dto.getStockMinimo() == null ? 0 : dto.getStockMinimo().doubleValue(),
                        dto.getUnidade() == null ? "" : dto.getUnidade(),
                        dto.getCustoUnitario() == null ? 0 : dto.getCustoUnitario().doubleValue()
                );
            }
        };

        task.setOnSucceeded(event -> {
            masterData.setAll(task.getValue());
            status.setText("Materiais carregados: " + masterData.size());
            table.setDisable(false);
        });

        task.setOnFailed(event -> {
            status.setText("Erro ao carregar materiais.");
            table.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}