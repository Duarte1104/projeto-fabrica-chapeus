package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.CriarCompraMaterialRequestDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.CompraMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;

public class GestorNovaDespesaPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();
    private final CompraMaterialApiService compraMaterialApiService = new CompraMaterialApiService();

    public GestorNovaDespesaPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Adicionar Despesa");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        ComboBox<MaterialDto> produto = new ComboBox<>();
        produto.setConverter(new StringConverter<>() {
            @Override
            public String toString(MaterialDto value) {
                if (value == null) {
                    return "";
                }
                return value.getNome() + " (" + value.getCustoUnitario() + " €/un)";
            }

            @Override
            public MaterialDto fromString(String string) {
                return null;
            }
        });

        TextField quantidade = new TextField();
        TextArea observacoes = new TextArea();
        observacoes.setPrefRowCount(3);

        form.add(new Label("Material"), 0, 0);
        form.add(produto, 0, 1);

        form.add(new Label("Quantidade"), 0, 2);
        form.add(quantidade, 0, 3);

        form.add(new Label("Observações"), 0, 4);
        form.add(observacoes, 0, 5);

        Label estado = new Label("A carregar materiais...");
        estado.setStyle("-fx-text-fill: #666666;");

        Button confirmar = GestorUiFactory.primaryButton("Confirmar Despesa");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.DESPESAS));

        confirmar.setOnAction(e -> {
            try {
                MaterialDto material = produto.getValue();
                if (material == null) {
                    mostrarErro("Seleciona um material.");
                    return;
                }

                BigDecimal qtd = new BigDecimal(quantidade.getText().trim());
                if (qtd.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarErro("A quantidade tem de ser maior que zero.");
                    return;
                }

                CriarCompraMaterialRequestDto dto = new CriarCompraMaterialRequestDto();
                dto.setIdMaterial(material.getId());
                dto.setQuantidade(qtd);
                dto.setObservacoes(observacoes.getText() == null || observacoes.getText().isBlank()
                        ? null
                        : observacoes.getText().trim());

                confirmar.setDisable(true);
                cancelar.setDisable(true);
                estado.setText("A registar despesa...");

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        compraMaterialApiService.criar(dto);
                        return null;
                    }
                };

                task.setOnSucceeded(event -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Despesa registada");
                    alert.setContentText("A compra de material foi registada com sucesso.");
                    alert.showAndWait();
                    shell.navigateTo(GestorPage.DESPESAS);
                });

                task.setOnFailed(event -> {
                    confirmar.setDisable(false);
                    cancelar.setDisable(false);
                    estado.setText("Erro ao registar despesa.");
                    mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
                });

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

            } catch (Exception ex) {
                mostrarErro("Quantidade inválida.");
            }
        });

        HBox buttons = new HBox(10, confirmar, cancelar);

        VBox card = GestorUiFactory.createCard();
        card.setMaxWidth(420);
        card.getChildren().addAll(estado, form, buttons);

        root.getChildren().add(card);

        carregarMateriais(produto, estado);

        return root;
    }

    private void carregarMateriais(ComboBox<MaterialDto> produto, Label estado) {
        Task<List<MaterialDto>> task = new Task<>() {
            @Override
            protected List<MaterialDto> call() {
                return materialApiService.listarTodos();
            }
        };

        task.setOnSucceeded(event -> {
            produto.getItems().setAll(task.getValue());
            estado.setText("Materiais carregados.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar materiais.");
            mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}