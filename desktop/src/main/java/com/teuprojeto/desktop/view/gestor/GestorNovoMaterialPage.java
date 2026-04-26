package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class GestorNovoMaterialPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

    public GestorNovoMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Novo Material");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField();
        TextField stockAtual = new TextField();
        TextField stockMinimo = new TextField();

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");

        TextField custoUnitario = new TextField();

        form.add(new Label("Nome"), 0, 0);
        form.add(nome, 0, 1);
        form.add(new Label("Stock Atual"), 1, 0);
        form.add(stockAtual, 1, 1);

        form.add(new Label("Stock Mínimo"), 0, 2);
        form.add(stockMinimo, 0, 3);
        form.add(new Label("Unidade"), 1, 2);
        form.add(unidade, 1, 3);

        form.add(new Label("Custo Unitário"), 0, 4);
        form.add(custoUnitario, 0, 5);

        Label estado = new Label();
        estado.setStyle("-fx-text-fill: #666666;");

        Button guardar = GestorUiFactory.primaryButton("Guardar");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        guardar.setOnAction(e -> {
            try {
                MaterialDto dto = new MaterialDto();
                dto.setNome(nome.getText().trim());
                dto.setStockAtual(new BigDecimal(stockAtual.getText().trim()));
                dto.setStockMinimo(new BigDecimal(stockMinimo.getText().trim()));
                dto.setUnidade(unidade.getValue());
                dto.setCustoUnitario(new BigDecimal(custoUnitario.getText().trim()));

                if (dto.getNome().isBlank() || dto.getUnidade() == null) {
                    mostrarErro("Preenche todos os campos obrigatórios.");
                    return;
                }

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
        });

        HBox buttons = new HBox(10, guardar, cancelar);

        VBox card = GestorUiFactory.createCard();
        card.getChildren().addAll(form, estado, buttons);

        root.getChildren().add(card);
        return root;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}