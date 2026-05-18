package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;

public class GestorNovoMaterialPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

    public GestorNovoMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Novo Material");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Registe um novo material para controlo de stock.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        VBox card = card();

        card.getChildren().addAll(
                sectionHeader("📦", "Dados do Material", "Preencha os dados iniciais do material."),
                separator()
        );

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);

        TextField nome = input("Nome do material");
        TextField stockAtual = input("Ex: 100");
        TextField stockMinimo = input("Ex: 20");

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");
        unidade.setPromptText("Selecionar unidade");
        unidade.setMaxWidth(Double.MAX_VALUE);
        unidade.setStyle(inputStyle());

        TextField custoUnitario = input("Ex: 2.50");

        addField(form, "Nome", nome, 0, 0);
        addField(form, "Stock Atual", stockAtual, 1, 0);

        addField(form, "Stock Mínimo", stockMinimo, 0, 2);
        addField(form, "Unidade", unidade, 1, 2);

        addField(form, "Custo Unitário", custoUnitario, 0, 4);

        Label estado = new Label();
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        Button guardar = GestorUiFactory.primaryButton("Guardar Material");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        guardar.setOnAction(e -> guardarMaterial(
                nome,
                stockAtual,
                stockMinimo,
                unidade,
                custoUnitario,
                estado,
                guardar,
                cancelar
        ));

        HBox buttons = new HBox(12, guardar, cancelar);
        buttons.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(form, estado, buttons);

        root.getChildren().addAll(header, card);

        return wrap(root);
    }

    private void guardarMaterial(TextField nome,
                                 TextField stockAtual,
                                 TextField stockMinimo,
                                 ComboBox<String> unidade,
                                 TextField custoUnitario,
                                 Label estado,
                                 Button guardar,
                                 Button cancelar) {
        try {
            if (isBlank(nome.getText()) ||
                    isBlank(stockAtual.getText()) ||
                    isBlank(stockMinimo.getText()) ||
                    unidade.getValue() == null ||
                    isBlank(custoUnitario.getText())) {

                mostrarErro("Preenche todos os campos obrigatórios.");
                return;
            }

            MaterialDto dto = new MaterialDto();
            dto.setNome(nome.getText().trim());
            dto.setStockAtual(new BigDecimal(stockAtual.getText().trim()));
            dto.setStockMinimo(new BigDecimal(stockMinimo.getText().trim()));
            dto.setUnidade(unidade.getValue());
            dto.setCustoUnitario(new BigDecimal(custoUnitario.getText().trim()));

            guardar.setDisable(true);
            cancelar.setDisable(true);
            estado.setText("A guardar material...");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    materialApiService.criar(dto);
                    return null;
                }
            };

            task.setOnSucceeded(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Material criado");
                alert.setContentText("O material foi criado com sucesso.");
                alert.showAndWait();

                shell.navigateTo(GestorPage.STOCK);
            });

            task.setOnFailed(event -> {
                guardar.setDisable(false);
                cancelar.setDisable(false);
                estado.setText("Erro ao guardar material.");
                mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            mostrarErro("Valores inválidos. Confirma stock e custo.");
        }
    }

    private void addField(GridPane form, String label, Control input, int col, int row) {
        form.add(fieldLabel(label), col, row);
        form.add(input, col, row + 1);

        GridPane.setHgrow(input, Priority.ALWAYS);
        input.setMaxWidth(Double.MAX_VALUE);
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

    private TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private String inputStyle() {
        return "-fx-background-color: white;" +
                "-fx-border-color: #dbe2ea;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 11;" +
                "-fx-font-size: 14;";
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return label;
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}