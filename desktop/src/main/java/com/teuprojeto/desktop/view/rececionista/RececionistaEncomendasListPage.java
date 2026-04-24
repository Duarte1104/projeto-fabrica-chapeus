package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
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
import java.util.Map;
import java.util.stream.Collectors;

public class RececionistaEncomendasListPage {

    private final RececionistaShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

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

        Button atualizar = RececionistaUiFactory.secondaryButton("Atualizar");

        actions.getChildren().addAll(search, nova, atualizar);

        Label statusLabel = new Label("A carregar encomendas...");
        statusLabel.setStyle("-fx-text-fill: #666666;");

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

        TableColumn<EncomendaRow, Void> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button verBtn = new Button("Ver");
            private final HBox box = new HBox(verBtn);

            {
                box.setAlignment(Pos.CENTER);
                verBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
                verBtn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Ainda não disponível");
                    alert.setContentText("A página de detalhe da encomenda será ligada a seguir.");
                    alert.showAndWait();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(numero, cliente, estado, design, acao);

        ObservableList<EncomendaRow> masterData = FXCollections.observableArrayList();
        FilteredList<EncomendaRow> filtrados = new FilteredList<>(masterData, encomenda -> true);

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String termo = newValue == null ? "" : newValue.trim().toLowerCase();

            filtrados.setPredicate(encomenda -> {
                if (termo.isBlank()) {
                    return true;
                }

                return encomenda.getNumero().toLowerCase().contains(termo)
                        || encomenda.getCliente().toLowerCase().contains(termo)
                        || encomenda.getEstado().toLowerCase().contains(termo)
                        || encomenda.getDesign().toLowerCase().contains(termo);
            });
        });

        SortedList<EncomendaRow> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);

        atualizar.setOnAction(e -> carregarEncomendas(masterData, statusLabel, table));
        carregarEncomendas(masterData, statusLabel, table);

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(actions, statusLabel, table);

        root.getChildren().add(card);
        return root;
    }

    private void carregarEncomendas(ObservableList<EncomendaRow> masterData, Label statusLabel, TableView<EncomendaRow> table) {
        statusLabel.setText("A carregar encomendas...");
        table.setDisable(true);

        Task<List<EncomendaRow>> task = new Task<>() {
            @Override
            protected List<EncomendaRow> call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();

                Map<Integer, String> nomesClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(ClienteDto::getCod, ClienteDto::getNome));

                return encomendas.stream()
                        .map(encomenda -> new EncomendaRow(
                                "ENC-" + encomenda.getNum(),
                                nomesClientes.getOrDefault(encomenda.getIdcliente(), "Cliente #" + encomenda.getIdcliente()),
                                mapearEstado(encomenda.getIdestado()),
                                Boolean.TRUE.equals(encomenda.getDesign()) ? "Sim" : "Não"
                        ))
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            masterData.setAll(task.getValue());
            statusLabel.setText("Encomendas carregadas: " + masterData.size());
            table.setDisable(false);
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar encomendas.");
            table.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter encomendas");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String mapearEstado(Long idestado) {
        if (idestado == null) {
            return "SEM_ESTADO";
        }

        return switch (idestado.intValue()) {
            case 1 -> "AGUARDA_DESIGN";
            case 2 -> "PREPARACAO";
            case 3 -> "PRONTA";
            case 4 -> "PAGA";
            default -> "ESTADO_" + idestado;
        };
    }
}