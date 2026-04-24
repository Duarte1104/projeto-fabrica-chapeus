package com.teuprojeto.desktop.view.designer;

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

public class DesignerPedidosDesignPage {

    private final DesignerShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public DesignerPedidosDesignPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = pageContainer("Pedidos de Design");

        HBox actions = new HBox(10);
        TextField search = new TextField();
        search.setPromptText("Pesquisar pedido...");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button atualizar = secondaryButton("Atualizar");
        actions.getChildren().addAll(search, atualizar);

        Label estado = new Label("A carregar pedidos...");
        estado.setStyle("-fx-text-fill: #666666;");

        TableView<PedidoDesignRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<PedidoDesignRow, String> numero = new TableColumn<>("Encomenda");
        numero.setCellValueFactory(c -> c.getValue().numeroProperty());

        TableColumn<PedidoDesignRow, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> c.getValue().clienteProperty());

        TableColumn<PedidoDesignRow, String> entrega = new TableColumn<>("Data Entrega");
        entrega.setCellValueFactory(c -> c.getValue().dataEntregaProperty());

        TableColumn<PedidoDesignRow, String> descricao = new TableColumn<>("Pedido do Cliente");
        descricao.setCellValueFactory(c -> c.getValue().descricaoDesignProperty());

        ObservableList<PedidoDesignRow> masterData = FXCollections.observableArrayList();

        TableColumn<PedidoDesignRow, Void> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button criarBtn = new Button("Criar Proposta");
            private final HBox box = new HBox(criarBtn);

            {
                box.setAlignment(Pos.CENTER);
                criarBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 8;");
                criarBtn.setOnAction(e -> {
                    PedidoDesignRow row = getTableView().getItems().get(getIndex());
                    shell.setPedidoSelecionado(row);
                    shell.navigateTo(DesignerPage.CRIAR_PROPOSTA);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(numero, cliente, entrega, descricao, acao);

        FilteredList<PedidoDesignRow> filtrados = new FilteredList<>(masterData, pedido -> true);

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String termo = newValue == null ? "" : newValue.trim().toLowerCase();

            filtrados.setPredicate(pedido -> {
                if (termo.isBlank()) {
                    return true;
                }

                return pedido.getNumero().toLowerCase().contains(termo)
                        || pedido.getCliente().toLowerCase().contains(termo)
                        || pedido.getDataEntrega().toLowerCase().contains(termo)
                        || pedido.getDescricaoDesign().toLowerCase().contains(termo);
            });
        });

        SortedList<PedidoDesignRow> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);

        atualizar.setOnAction(e -> carregarPedidos(masterData, estado));
        carregarPedidos(masterData, estado);

        VBox card = card();
        card.getChildren().addAll(actions, estado, table);
        root.getChildren().add(card);

        return root;
    }

    private void carregarPedidos(ObservableList<PedidoDesignRow> masterData, Label estado) {
        estado.setText("A carregar pedidos...");

        Task<List<PedidoDesignRow>> task = new Task<>() {
            @Override
            protected List<PedidoDesignRow> call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();

                Map<Integer, String> nomesClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(
                                ClienteDto::getCod,
                                c -> c.getNome() == null ? "Cliente sem nome" : c.getNome(),
                                (a, b) -> a
                        ));

                return encomendas.stream()
                        .filter(e -> Boolean.TRUE.equals(e.getDesign()) && Long.valueOf(1L).equals(e.getIdestado()))
                        .map(e -> new PedidoDesignRow(
                                e.getNum(),
                                "ENC-" + e.getNum(),
                                nomesClientes.getOrDefault(e.getIdcliente(), "Cliente #" + e.getIdcliente()),
                                e.getDataEntrega() == null ? "-" : e.getDataEntrega(),
                                e.getDescricaoDesign() == null || e.getDescricaoDesign().isBlank() ? "-" : e.getDescricaoDesign()
                        ))
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            masterData.setAll(task.getValue());
            estado.setText("Pedidos pendentes: " + masterData.size());
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar pedidos.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox pageContainer(String titleText) {
        VBox root = new VBox(18);
        root.setStyle("-fx-padding: 28; -fx-background-color: #efefef;");
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");
        root.getChildren().add(title);
        return root;
    }

    private VBox card() {
        VBox box = new VBox(12);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 22; -fx-border-color: #e0e0e0; -fx-border-radius: 12;");
        return box;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-background-radius: 10; -fx-border-radius: 10;");
        return button;
    }
}