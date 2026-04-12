package com.teuprojeto.desktop.view.gestor;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GestorDespesasPage {

    private final GestorShellView shell;

    public GestorDespesasPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Consultar Despesas");

        HBox actions = new HBox(10);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button adicionar = GestorUiFactory.primaryButton("Adicionar Despesa");
        adicionar.setOnAction(e -> shell.navigateTo(GestorPage.NOVA_DESPESA));

        actions.getChildren().addAll(spacer, adicionar);

        TableView<DespesaRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DespesaRow, String> codigo = new TableColumn<>("ID");
        codigo.setCellValueFactory(c -> c.getValue().codigoProperty());

        TableColumn<DespesaRow, String> data = new TableColumn<>("Data");
        data.setCellValueFactory(c -> c.getValue().dataProperty());

        TableColumn<DespesaRow, String> produto = new TableColumn<>("Produto");
        produto.setCellValueFactory(c -> c.getValue().produtoProperty());

        TableColumn<DespesaRow, String> descricao = new TableColumn<>("Descrição");
        descricao.setCellValueFactory(c -> c.getValue().descricaoProperty());

        TableColumn<DespesaRow, String> fornecedor = new TableColumn<>("Fornecedor");
        fornecedor.setCellValueFactory(c -> c.getValue().fornecedorProperty());

        TableColumn<DespesaRow, String> valor = new TableColumn<>("Valor");
        valor.setCellValueFactory(c -> c.getValue().valorProperty());
        valor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item + " €");
                if (!empty && item != null) {
                    setStyle("-fx-text-fill: #d11a2a; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        table.getColumns().addAll(codigo, data, produto, descricao, fornecedor, valor);
        table.setItems(MockGestorData.getDespesas());

        VBox cardTabela = GestorUiFactory.createCard();
        cardTabela.getChildren().addAll(actions, table);

        VBox totalCard = GestorUiFactory.createCard();
        totalCard.setMaxWidth(220);

        Label t1 = new Label("Total Despesas");
        t1.setStyle("-fx-text-fill: #666;");

        Label t2 = new Label(String.format("€%.2f", MockGestorData.totalDespesas()));
        t2.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #d11a2a;");

        Label t3 = new Label("Este mês");

        totalCard.getChildren().addAll(t1, t2, t3);

        root.getChildren().addAll(cardTabela, totalCard);
        return root;
    }
}