package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.CompraMaterialDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.CompraMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestorDespesasPage {

    private final GestorShellView shell;
    private final CompraMaterialApiService compraMaterialApiService = new CompraMaterialApiService();
    private final MaterialApiService materialApiService = new MaterialApiService();

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

        Label estado = new Label("A carregar despesas...");
        estado.setStyle("-fx-text-fill: #666666;");

        TableView<DespesaRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DespesaRow, String> codigo = new TableColumn<>("ID");
        codigo.setCellValueFactory(c -> c.getValue().codigoProperty());

        TableColumn<DespesaRow, String> data = new TableColumn<>("Data");
        data.setCellValueFactory(c -> c.getValue().dataProperty());

        TableColumn<DespesaRow, String> produto = new TableColumn<>("Material");
        produto.setCellValueFactory(c -> c.getValue().produtoProperty());

        TableColumn<DespesaRow, String> quantidade = new TableColumn<>("Quantidade");
        quantidade.setCellValueFactory(c -> c.getValue().quantidadeProperty());

        TableColumn<DespesaRow, String> descricao = new TableColumn<>("Observações");
        descricao.setCellValueFactory(c -> c.getValue().descricaoProperty());

        TableColumn<DespesaRow, String> valor = new TableColumn<>("Valor");
        valor.setCellValueFactory(c -> c.getValue().valorProperty());

        table.getColumns().addAll(codigo, data, produto, quantidade, descricao, valor);

        ObservableList<DespesaRow> rows = FXCollections.observableArrayList();
        table.setItems(rows);

        VBox cardTabela = GestorUiFactory.createCard();
        cardTabela.getChildren().addAll(actions, estado, table);

        VBox totalCard = GestorUiFactory.createCard();
        totalCard.setMaxWidth(220);

        Label t1 = new Label("Total Despesas");
        t1.setStyle("-fx-text-fill: #666;");

        Label t2 = new Label("0.00 €");
        t2.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #d11a2a;");

        Label t3 = new Label("Compras de material");
        totalCard.getChildren().addAll(t1, t2, t3);

        root.getChildren().addAll(cardTabela, totalCard);

        carregarDespesas(rows, estado, t2);

        return root;
    }

    private void carregarDespesas(ObservableList<DespesaRow> rows, Label estado, Label totalLabel) {
        Task<List<DespesaRow>> task = new Task<>() {
            private BigDecimal total = BigDecimal.ZERO;

            @Override
            protected List<DespesaRow> call() {
                List<CompraMaterialDto> compras = compraMaterialApiService.listarTodas();
                List<MaterialDto> materiais = materialApiService.listarTodos();

                Map<Long, String> nomes = materiais.stream()
                        .filter(m -> m.getId() != null)
                        .collect(Collectors.toMap(MaterialDto::getId, MaterialDto::getNome, (a, b) -> a));

                total = compras.stream()
                        .map(CompraMaterialDto::getCustoTotal)
                        .filter(v -> v != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                updateMessage(String.format("%.2f €", total.doubleValue()));

                return compras.stream()
                        .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                        .map(compra -> new DespesaRow(
                                "DESP-" + compra.getId(),
                                compra.getData() == null ? "-" : compra.getData().replace("T", " "),
                                nomes.getOrDefault(compra.getIdMaterial(), "Material #" + compra.getIdMaterial()),
                                compra.getQuantidade() == null ? "-" : compra.getQuantidade().toPlainString(),
                                compra.getObservacoes() == null ? "-" : compra.getObservacoes(),
                                compra.getCustoTotal() == null ? "0.00 €" : String.format("%.2f €", compra.getCustoTotal().doubleValue())
                        ))
                        .toList();
            }
        };

        totalLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(event -> {
            rows.setAll(task.getValue());
            estado.setText("Despesas carregadas: " + rows.size());
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar despesas.");

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