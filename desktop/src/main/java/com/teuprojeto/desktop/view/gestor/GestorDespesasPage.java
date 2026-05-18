package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.CompraMaterialDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.CompraMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Consultar Despesas");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte compras de material e despesas registadas.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button adicionar = GestorUiFactory.primaryButton("Adicionar Despesa");
        adicionar.setOnAction(e -> shell.navigateTo(GestorPage.NOVA_DESPESA));

        topBar.getChildren().addAll(spacer, adicionar);

        Label estado = new Label("A carregar despesas...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label totalValor = statNumber("0,00 €");

        HBox stats = new HBox(18);
        stats.getChildren().addAll(
                statCard("Total Despesas", totalValor, "Compras de material", "#dc2626", "€")
        );

        VBox listaCard = card();
        VBox listaDespesas = new VBox(14);

        listaCard.getChildren().addAll(
                sectionHeader("🧾", "Despesas Registadas", "Últimas compras de material registadas no sistema."),
                separator(),
                listaDespesas
        );

        root.getChildren().addAll(
                header,
                topBar,
                estado,
                stats,
                listaCard
        );

        carregarDespesas(listaDespesas, estado, totalValor);

        return wrap(root);
    }

    private void carregarDespesas(
            VBox listaDespesas,
            Label estado,
            Label totalLabel
    ) {
        estado.setText("A carregar despesas...");

        Task<List<DespesaRow>> task = new Task<>() {
            private BigDecimal total = BigDecimal.ZERO;

            @Override
            protected List<DespesaRow> call() {
                List<CompraMaterialDto> compras = compraMaterialApiService.listarTodas();
                List<MaterialDto> materiais = materialApiService.listarTodos();

                Map<Long, String> nomes = materiais.stream()
                        .filter(m -> m.getId() != null)
                        .collect(Collectors.toMap(
                                MaterialDto::getId,
                                MaterialDto::getNome,
                                (a, b) -> a
                        ));

                total = compras.stream()
                        .map(CompraMaterialDto::getCustoTotal)
                        .filter(v -> v != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                updateMessage(formatarMoeda(total));

                return compras.stream()
                        .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                        .map(compra -> new DespesaRow(
                                "DESP-" + compra.getId(),
                                compra.getData() == null ? "-" : compra.getData().replace("T", " "),
                                nomes.getOrDefault(compra.getIdMaterial(), "Material #" + compra.getIdMaterial()),
                                compra.getQuantidade() == null ? "-" : compra.getQuantidade().toPlainString(),
                                compra.getObservacoes() == null ? "-" : compra.getObservacoes(),
                                compra.getCustoTotal() == null ? "0,00 €" : formatarMoeda(compra.getCustoTotal())
                        ))
                        .toList();
            }
        };

        totalLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(event -> {
            ObservableList<DespesaRow> rows = FXCollections.observableArrayList(task.getValue());

            listaDespesas.getChildren().clear();

            if (rows.isEmpty()) {
                listaDespesas.getChildren().add(emptyBox("Ainda não existem despesas registadas."));
            } else {
                for (DespesaRow row : rows) {
                    listaDespesas.getChildren().add(despesaCard(row));
                }
            }

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

    private HBox despesaCard(DespesaRow row) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        StackPane icon = new StackPane();
        icon.setMinSize(52, 52);
        icon.setPrefSize(52, 52);
        icon.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 16;");

        Label iconText = new Label("🧾");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        VBox main = new VBox(4);

        Label codigo = new Label(row.codigoProperty().get());
        codigo.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label material = new Label(row.produtoProperty().get());
        material.setStyle("-fx-font-size: 13; -fx-text-fill: #2563eb; -fx-font-weight: bold;");

        main.getChildren().addAll(codigo, material);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(
                icon,
                main,
                spacer,
                infoBlock("Data", row.dataProperty().get()),
                infoBlock("Quantidade", row.quantidadeProperty().get()),
                infoBlock("Valor", row.valorProperty().get())
        );

        return card;
    }

    private VBox statCard(String title, Label value, String subtitle, String color, String iconText) {
        VBox card = card();
        card.setPrefWidth(320);
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(54, 54);
        icon.setPrefSize(54, 54);
        icon.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 14, 0, 0, 5);"
        );

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 14; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        text.getChildren().addAll(titleLabel, value, subtitleLabel);
        box.getChildren().addAll(icon, text);

        card.getChildren().add(box);
        return card;
    }

    private Label statNumber(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private HBox sectionHeader(String iconText, String title, String subtitle) {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(4);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        text.getChildren().addAll(titleLabel, subtitleLabel);
        box.getChildren().addAll(icon, text);

        return box;
    }

    private VBox card() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );
        return card;
    }

    private VBox infoBlock(String title, String value) {
        VBox box = new VBox(4);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label v = new Label(value == null ? "-" : value);
        v.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        box.getChildren().addAll(t, v);
        return box;
    }

    private VBox emptyBox(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);
        return box;
    }

    private Region separator() {
        Region region = new Region();
        region.setPrefHeight(1);
        region.setStyle("-fx-background-color: #e5e7eb;");
        return region;
    }

    private Parent wrap(VBox root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");
        return scrollPane;
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00 €";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }
}