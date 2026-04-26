package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class GestorEditarMaterialPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();

    public GestorEditarMaterialPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        MaterialRow material = shell.getMaterialSelecionado();

        VBox root = GestorUiFactory.createPageContainer("Editar Material");

        if (material == null) {
            VBox card = GestorUiFactory.createCard();
            Label aviso = new Label("Nenhum material foi selecionado.");
            Button voltar = GestorUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));
            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return root;
        }

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField(material.getNome());
        nome.setDisable(true);

        TextField stockAtual = new TextField(String.valueOf(material.getStockAtual()));
        stockAtual.setDisable(true);

        TextField stockMinimo = new TextField(String.valueOf(material.getStockMinimo()));
        stockMinimo.setDisable(true);

        ComboBox<String> unidade = new ComboBox<>();
        unidade.getItems().addAll("un", "m", "m²", "kg", "cx");
        unidade.setValue(material.getUnidade());
        unidade.setDisable(true);

        TextField custoUnitario = new TextField(String.valueOf(material.getCustoUnitario()));
        custoUnitario.setDisable(true);

        TextField adicionarStock = new TextField();
        adicionarStock.setPromptText("Ex: 20");

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
        form.add(new Label("Acrescentar Stock"), 1, 4);
        form.add(adicionarStock, 1, 5);

        Label estado = new Label();
        estado.setStyle("-fx-text-fill: #666666;");

        Button guardar = GestorUiFactory.primaryButton("Guardar");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        guardar.setOnAction(e -> {
            try {
                BigDecimal atual = BigDecimal.valueOf(material.getStockAtual());
                BigDecimal acrescimo = new BigDecimal(adicionarStock.getText().trim());
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