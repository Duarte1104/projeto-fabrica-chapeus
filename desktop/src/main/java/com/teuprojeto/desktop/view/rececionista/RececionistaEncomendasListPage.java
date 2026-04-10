package com.teuprojeto.desktop.view.rececionista;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RececionistaEncomendasListPage {

    private final RececionistaShellView shell;

    public RececionistaEncomendasListPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Encomendas");

        HBox actions = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Pesquisar encomenda...");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button nova = RececionistaUiFactory.primaryButton("Nova Encomenda");
        nova.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        actions.getChildren().addAll(search, nova);

        TableView<EncomendaRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<EncomendaRow, String> numero = new TableColumn<>("Número");
        numero.setCellValueFactory(c -> c.getValue().numeroProperty());

        TableColumn<EncomendaRow, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> c.getValue().clienteProperty());

        TableColumn<EncomendaRow, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(c -> c.getValue().estadoProperty());

        TableColumn<EncomendaRow, String> design = new TableColumn<>("Design");
        design.setCellValueFactory(c -> c.getValue().designProperty());

        TableColumn<EncomendaRow, String> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button verBtn = new Button("Ver");
            private final HBox box = new HBox(verBtn);

            {
                box.setAlignment(Pos.CENTER);
                verBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(numero, cliente, estado, design, acao);
        table.setItems(FXCollections.observableArrayList(
                new EncomendaRow("ENC-1121", "João Santos", "AGUARDA_DESIGN", "Sim"),
                new EncomendaRow("ENC-1110", "Ana Costa", "PREPARACAO", "Não"),
                new EncomendaRow("ENC-1232", "Pedro Lima", "PRONTA", "Sim")
        ));

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(actions, table);

        root.getChildren().add(card);
        return root;
    }
}