package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class GestorStockPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

    public GestorStockPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Consultar Stock");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte materiais, stock atual e materiais abaixo do mínimo.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar material...");
        search.setPrefWidth(380);
        search.setStyle(inputStyle());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button novoMaterial = GestorUiFactory.primaryButton("Novo Material");
        novoMaterial.setOnAction(e -> shell.navigateTo(GestorPage.NOVO_MATERIAL));

        Button atualizar = outlineButton("Atualizar");

        topBar.getChildren().addAll(search, spacer, novoMaterial, atualizar);

        Label status = new Label("A carregar materiais...");
        status.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        List<MaterialRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) ->
                atualizarLista(lista, cache, newValue)
        );

        root.getChildren().addAll(header, topBar, status, lista);

        Runnable carregar = () -> carregarMateriais(cache, lista, status, search.getText());

        atualizar.setOnAction(e -> carregar.run());
        carregar.run();

        return wrap(root);
    }

    private void carregarMateriais(
            List<MaterialRow> cache,
            VBox lista,
            Label status,
            String termo
    ) {
        status.setText("A carregar materiais...");

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
            cache.clear();
            cache.addAll(task.getValue());

            atualizarLista(lista, cache, termo);
            status.setText("Materiais carregados: " + cache.size());
        });

        task.setOnFailed(event -> {
            status.setText("Erro ao carregar materiais.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void atualizarLista(
            VBox lista,
            List<MaterialRow> materiais,
            String termo
    ) {
        lista.getChildren().clear();

        List<MaterialRow> filtrados = materiais.stream()
                .filter(m -> matches(termo, m))
                .toList();

        if (filtrados.isEmpty()) {
            lista.getChildren().add(emptyCard("Nenhum material encontrado."));
            return;
        }

        for (MaterialRow material : filtrados) {
            lista.getChildren().add(buildCard(material));
        }
    }

    private boolean matches(String termo, MaterialRow material) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return texto(material.getNome()).contains(t)
                || texto(material.getUnidade()).contains(t)
                || String.valueOf(material.getStockAtual()).contains(t)
                || String.valueOf(material.getStockMinimo()).contains(t)
                || String.valueOf(material.getCustoUnitario()).contains(t);
    }

    private VBox buildCard(MaterialRow material) {
        VBox card = card();

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("📦");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label nome = new Label(valor(material.getNome()));
        nome.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label unidade = new Label("Unidade: " + valor(material.getUnidade()));
        unidade.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(nome, unidade);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean stockBaixo = material.getStockAtual() <= material.getStockMinimo();

        Label estado = badge(
                stockBaixo ? "Stock baixo" : "Stock OK",
                stockBaixo ? "#fee2e2" : "#dcfce7",
                stockBaixo ? "#dc2626" : "#15803d"
        );

        Button editar = GestorUiFactory.primaryButton("Editar Stock");
        editar.setOnAction(e -> {
            shell.setMaterialSelecionado(material);
            shell.navigateTo(GestorPage.EDITAR_MATERIAL);
        });

        top.getChildren().addAll(icon, left, spacer, estado, editar);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Stock atual", formatarNumero(material.getStockAtual())),
                infoBlock("Stock mínimo", formatarNumero(material.getStockMinimo())),
                infoBlock("Unidade", valor(material.getUnidade())),
                infoBlock("Custo unitário", formatarMoeda(material.getCustoUnitario()))
        );

        card.getChildren().addAll(top, infoGrid);

        return card;
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
        v.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        box.getChildren().addAll(t, v);
        return box;
    }

    private Label badge(String text, String bg, String fg) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-padding: 7 12 7 12;" +
                        "-fx-background-radius: 14;" +
                        "-fx-font-size: 12;" +
                        "-fx-font-weight: bold;"
        );
        return label;
    }

    private VBox emptyCard(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(22));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 18;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);
        return box;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;" +
                        "-fx-padding: 0 18 0 18;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private String inputStyle() {
        return "-fx-background-color: white;" +
                "-fx-border-color: #dbe2ea;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 11;" +
                "-fx-font-size: 14;";
    }

    private Parent wrap(VBox root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");
        return scrollPane;
    }

    private String texto(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private String valor(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String formatarNumero(double valor) {
        return String.format("%.2f", valor).replace(".", ",");
    }

    private String formatarMoeda(double valor) {
        return String.format("%.2f €", valor).replace(".", ",");
    }
}