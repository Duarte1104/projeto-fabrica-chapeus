package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.service.ClienteApiService;
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

public class RececionistaClientesListPage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

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

        Button atualizar = RececionistaUiFactory.secondaryButton("Atualizar");

        actions.getChildren().addAll(search, novo, atualizar);

        Label statusLabel = new Label("A carregar clientes...");
        statusLabel.setStyle("-fx-text-fill: #666666;");

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

        TableColumn<ClienteRow, Void> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button verBtn = new Button("Ver");
            private final HBox box = new HBox(verBtn);

            {
                box.setAlignment(Pos.CENTER);
                verBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
                verBtn.setOnAction(e -> {
                    ClienteRow cliente = getTableView().getItems().get(getIndex());
                    shell.setClienteSelecionado(cliente);
                    shell.navigateTo(RececionistaPage.CLIENTES_VER);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(nome, email, telefone, tipo, acao);

        ObservableList<ClienteRow> masterData = FXCollections.observableArrayList();
        FilteredList<ClienteRow> filtrados = new FilteredList<>(masterData, cliente -> true);

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String termo = newValue == null ? "" : newValue.trim().toLowerCase();

            filtrados.setPredicate(cliente -> {
                if (termo.isBlank()) {
                    return true;
                }

                return cliente.getNome().toLowerCase().contains(termo)
                        || cliente.getEmail().toLowerCase().contains(termo)
                        || cliente.getTelefone().toLowerCase().contains(termo)
                        || cliente.getTipo().toLowerCase().contains(termo);
            });
        });

        SortedList<ClienteRow> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);

        atualizar.setOnAction(e -> carregarClientes(masterData, statusLabel, table));
        carregarClientes(masterData, statusLabel, table);

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(actions, statusLabel, table);

        root.getChildren().add(card);
        return root;
    }

    private void carregarClientes(ObservableList<ClienteRow> masterData, Label statusLabel, TableView<ClienteRow> table) {
        statusLabel.setText("A carregar clientes...");
        table.setDisable(true);

        Task<List<ClienteDto>> task = new Task<>() {
            @Override
            protected List<ClienteDto> call() {
                return clienteApiService.listarTodos();
            }
        };

        task.setOnSucceeded(event -> {
            masterData.setAll(
                    task.getValue().stream()
                            .map(this::toRow)
                            .toList()
            );

            statusLabel.setText("Clientes carregados: " + masterData.size());
            table.setDisable(false);
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar clientes.");
            table.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter clientes");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private ClienteRow toRow(ClienteDto dto) {
        return new ClienteRow(
                dto.getCod(),
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getTipo()
        );
    }
}