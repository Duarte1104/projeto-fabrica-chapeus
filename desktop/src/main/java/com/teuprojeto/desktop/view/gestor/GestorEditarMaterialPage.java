package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;

public class GestorEditarMaterialPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

    public GestorEditarMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        MaterialRow material = shell.getMaterialSelecionado();

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Editar Material");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte os dados do material e acrescente stock.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        if (material == null) {
            VBox card = card();

            Label aviso = new Label("Nenhum material foi selecionado.");
            aviso.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

            Button voltar = GestorUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().addAll(header, card);

            return wrap(root);
        }

        VBox card = card();

        card.getChildren().addAll(
                sectionHeader("📦", "Dados do Material", "Apenas é possível acrescentar stock ao material selecionado."),
                separator()
        );

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);

        TextField nome = input(material.getNome());
        nome.setDisable(true);

        TextField stockAtual = input(String.valueOf(material.getStockAtual()));
        stockAtual.setDisable(true);

        TextField stockMinimo = input(String.valueOf(material.getStockMinimo()));
        stockMinimo.setDisable(true);

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");
        unidade.setValue(material.getUnidade());
        unidade.setDisable(true);
        unidade.setMaxWidth(Double.MAX_VALUE);
        unidade.setStyle(inputStyle());

        TextField custoUnitario = input(String.valueOf(material.getCustoUnitario()));
        custoUnitario.setDisable(true);

        TextField adicionarStock = input("Ex: 20");

        addField(form, "Nome", nome, 0, 0);
        addField(form, "Stock Atual", stockAtual, 1, 0);

        addField(form, "Stock Mínimo", stockMinimo, 0, 2);
        addField(form, "Unidade", unidade, 1, 2);

        addField(form, "Custo Unitário", custoUnitario, 0, 4);
        addField(form, "Acrescentar Stock", adicionarStock, 1, 4);

        Label estado = new Label();
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        Button guardar = GestorUiFactory.primaryButton("Guardar Stock");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        guardar.setOnAction(e -> guardarStock(
                material,
                adicionarStock,
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

    private void guardarStock(MaterialRow material,
                              TextField adicionarStock,
                              Label estado,
                              Button guardar,
                              Button cancelar) {
        try {
            if (adicionarStock.getText() == null || adicionarStock.getText().isBlank()) {
                mostrarErro("Indica a quantidade de stock a acrescentar.");
                return;
            }

            BigDecimal atual = BigDecimal.valueOf(material.getStockAtual());
            BigDecimal acrescimo = new BigDecimal(adicionarStock.getText().trim());

            if (acrescimo.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarErro("O valor a acrescentar tem de ser maior que zero.");
                return;
            }

            BigDecimal novoStock = atual.add(acrescimo);

            guardar.setDisable(true);
            cancelar.setDisable(true);
            estado.setText("A atualizar stock...");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    materialApiService.atualizarStock(material.getId(), novoStock);
                    return null;
                }
            };

            task.setOnSucceeded(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Stock atualizado");
                alert.setContentText("O stock foi atualizado com sucesso.");
                alert.showAndWait();

                shell.navigateTo(GestorPage.STOCK);
            });

            task.setOnFailed(event -> {
                guardar.setDisable(false);
                cancelar.setDisable(false);
                estado.setText("Erro ao atualizar stock.");
                mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            mostrarErro("Valor inválido para acrescentar stock.");
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

    private TextField input(String value) {
        TextField field = new TextField(value);
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

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}