package com.teuprojeto.desktop.view.rececionista;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RececionistaClientesListPage {

    private final RececionistaShellView shell;

    public RececionistaClientesListPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Clientes");

        HBox actions = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Pesquisar cliente...");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button novo = RececionistaUiFactory.primaryButton("Novo Cliente");
        novo.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_CRIAR));

        actions.getChildren().addAll(search, novo);

        TableView<ClienteRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<ClienteRow, String> nome = new TableColumn<>("Nome");
        nome.setCellValueFactory(c -> c.getValue().nomeProperty());

        TableColumn<ClienteRow, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(c -> c.getValue().emailProperty());

        TableColumn<ClienteRow, String> telefone = new TableColumn<>("Telefone");
        telefone.setCellValueFactory(c -> c.getValue().telefoneProperty());

        TableColumn<ClienteRow, String> tipo = new TableColumn<>("Tipo");
        tipo.setCellValueFactory(c -> c.getValue().tipoProperty());

        TableColumn<ClienteRow, String> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button verBtn = new Button("Ver");
            private final HBox box = new HBox(verBtn);

            {
                box.setAlignment(Pos.CENTER);
                verBtn.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_VER));
                verBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(nome, email, telefone, tipo, acao);
        table.setItems(FXCollections.observableArrayList(
                new ClienteRow("João Santos", "joao@email.com", "912345678", "Particular"),
                new ClienteRow("Ana Costa", "ana@email.com", "913222111", "Empresa"),
                new ClienteRow("Pedro Lima", "pedro@email.com", "914111222", "Particular")
        ));

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(actions, table);

        root.getChildren().add(card);
        return root;
    }
}