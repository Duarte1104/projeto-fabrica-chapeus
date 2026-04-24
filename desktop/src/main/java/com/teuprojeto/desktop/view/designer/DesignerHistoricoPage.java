package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.DesignApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region; //region aqui

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DesignerHistoricoPage {

    private final DesignerShellView shell;
    private final DesignApiService designApiService = new DesignApiService();
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public DesignerHistoricoPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = pageContainer("Histórico de Designs");

        HBox actions = new HBox(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button atualizar = secondaryButton("Atualizar");
        actions.getChildren().addAll(spacer, atualizar);

        Label estado = new Label("A carregar histórico...");
        estado.setStyle("-fx-text-fill: #666666;");

        TableView<HistoricoRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<HistoricoRow, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignId()));

        TableColumn<HistoricoRow, String> encomendaCol = new TableColumn<>("Encomenda");
        encomendaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEncomenda()));

        TableColumn<HistoricoRow, String> clienteCol = new TableColumn<>("Cliente");
        clienteCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente()));

        TableColumn<HistoricoRow, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        TableColumn<HistoricoRow, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataCriacao()));

        ObservableList<HistoricoRow> rows = FXCollections.observableArrayList();

        TableColumn<HistoricoRow, Void> acaoCol = new TableColumn<>("Ações");
        acaoCol.setCellFactory(col -> new TableCell<>() {
            private final Button aprovarBtn = new Button("Aprovar");
            private final Button rejeitarBtn = new Button("Rejeitar");
            private final HBox box = new HBox(8, aprovarBtn, rejeitarBtn);

            {
                box.setAlignment(Pos.CENTER);
                aprovarBtn.setStyle("-fx-background-color: #e8fff0; -fx-text-fill: #0a7a35; -fx-border-color: #9dd5b0; -fx-background-radius: 8; -fx-border-radius: 8;");
                rejeitarBtn.setStyle("-fx-background-color: #fff0f0; -fx-text-fill: #b13232; -fx-border-color: #e3aaaa; -fx-background-radius: 8; -fx-border-radius: 8;");

                aprovarBtn.setOnAction(e -> mudarEstado(getTableView().getItems().get(getIndex()), "APROVADO_CLIENTE", rows, estado));
                rejeitarBtn.setOnAction(e -> mudarEstado(getTableView().getItems().get(getIndex()), "REJEITADO_CLIENTE", rows, estado));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                HistoricoRow row = getTableView().getItems().get(getIndex());
                boolean podeDecidir = "ENVIADO_CLIENTE".equalsIgnoreCase(row.getEstado());

                aprovarBtn.setVisible(podeDecidir);
                aprovarBtn.setManaged(podeDecidir);
                rejeitarBtn.setVisible(podeDecidir);
                rejeitarBtn.setManaged(podeDecidir);

                setGraphic(podeDecidir ? box : null);
            }
        });

        table.getColumns().addAll(idCol, encomendaCol, clienteCol, estadoCol, dataCol, acaoCol);
        table.setItems(rows);

        atualizar.setOnAction(e -> carregarHistorico(rows, estado));
        carregarHistorico(rows, estado);

        VBox card = card();
        card.getChildren().addAll(actions, estado, table);
        root.getChildren().add(card);

        return root;
    }

    private void mudarEstado(HistoricoRow row, String novoEstado, ObservableList<HistoricoRow> rows, Label estado) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirmar ação");
        confirm.setContentText("Pretendes marcar o design " + row.getDesignId() + " como " + novoEstado + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        estado.setText("A atualizar design...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                designApiService.mudarEstado(row.getId(), novoEstado);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText("Estado atualizado");
            ok.setContentText("O estado do design foi atualizado.");
            ok.showAndWait();

            carregarHistorico(rows, estado);
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao atualizar design.");
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setHeaderText("Erro");
            erro.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            erro.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void carregarHistorico(ObservableList<HistoricoRow> rows, Label estado) {
        estado.setText("A carregar histórico...");

        Task<List<HistoricoRow>> task = new Task<>() {
            @Override
            protected List<HistoricoRow> call() {
                List<DesignEncomendaDto> designs = designApiService.listarTodos();
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();

                Map<Long, EncomendaDto> mapaEncomendas = encomendas.stream()
                        .collect(Collectors.toMap(
                                EncomendaDto::getNum,
                                e -> e,
                                (a, b) -> a
                        ));

                Map<Integer, String> mapaClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(
                                ClienteDto::getCod,
                                c -> c.getNome() == null ? "Cliente sem nome" : c.getNome(),
                                (a, b) -> a
                        ));

                return designs.stream()
                        .sorted((a, b) -> {
                            String da = a.getDataCriacao() == null ? "" : a.getDataCriacao();
                            String db = b.getDataCriacao() == null ? "" : b.getDataCriacao();
                            return db.compareTo(da);
                        })
                        .map(d -> {
                            Long encomendaId = d.getIdEncomenda() == null ? null : d.getIdEncomenda().longValue();
                            EncomendaDto encomenda = encomendaId == null ? null : mapaEncomendas.get(encomendaId);

                            String numero = encomendaId == null ? "-" : "ENC-" + encomendaId;
                            String cliente = "-";

                            if (encomenda != null) {
                                numero = "ENC-" + encomenda.getNum();
                                cliente = mapaClientes.getOrDefault(encomenda.getIdcliente(), "Cliente #" + encomenda.getIdcliente());
                            }

                            return new HistoricoRow(
                                    d.getId(),
                                    d.getId() == null ? "-" : String.valueOf(d.getId()),
                                    numero,
                                    cliente,
                                    d.getEstadoDesign() == null ? "-" : d.getEstadoDesign(),
                                    d.getDataCriacao() == null ? "-" : d.getDataCriacao()
                            );
                        })
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            rows.setAll(task.getValue());
            estado.setText("Histórico carregado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar histórico.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private static class HistoricoRow {
        private final Long id;
        private final String designId;
        private final String encomenda;
        private final String cliente;
        private final String estado;
        private final String dataCriacao;

        public HistoricoRow(Long id, String designId, String encomenda, String cliente, String estado, String dataCriacao) {
            this.id = id;
            this.designId = designId;
            this.encomenda = encomenda;
            this.cliente = cliente;
            this.estado = estado;
            this.dataCriacao = dataCriacao;
        }

        public Long getId() {
            return id;
        }

        public String getDesignId() {
            return designId;
        }

        public String getEncomenda() {
            return encomenda;
        }

        public String getCliente() {
            return cliente;
        }

        public String getEstado() {
            return estado;
        }

        public String getDataCriacao() {
            return dataCriacao;
        }
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