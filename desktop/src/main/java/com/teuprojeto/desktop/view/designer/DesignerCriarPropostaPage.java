package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.service.DesignApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

public class DesignerCriarPropostaPage {

    private final DesignerShellView shell;
    private final DesignApiService designApiService = new DesignApiService();

    public DesignerCriarPropostaPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = pageContainer("Criar Proposta de Design");

        PedidoDesignRow pedido = shell.getPedidoSelecionado();

        if (pedido == null) {
            VBox card = card();
            Label aviso = new Label("Nenhum pedido foi selecionado.");
            Button voltar = secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));
            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return root;
        }

        Label estado = new Label("Pedido selecionado.");
        estado.setStyle("-fx-text-fill: #666666;");

        VBox infoCard = card();
        Label infoTitle = sectionTitle("Informações do Pedido");
        Label encomenda = new Label("Encomenda: " + pedido.getNumero());
        Label cliente = new Label("Cliente: " + pedido.getCliente());
        Label entrega = new Label("Entrega: " + pedido.getDataEntrega());
        Label descricaoCliente = new Label("Pedido: " + pedido.getDescricaoDesign());
        descricaoCliente.setWrapText(true);
        infoCard.getChildren().addAll(infoTitle, encomenda, cliente, entrega, descricaoCliente);

        TextArea descricaoDesigner = new TextArea();
        descricaoDesigner.setPromptText("Descreve a proposta do design...");
        descricaoDesigner.setPrefRowCount(8);

        ObservableList<File> imagensSelecionadas = FXCollections.observableArrayList();

        ListView<File> imagensListView = new ListView<>(imagensSelecionadas);
        imagensListView.setPrefHeight(130);
        imagensListView.setPlaceholder(new Label("Nenhuma imagem selecionada."));
        imagensListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                setText(empty || file == null ? null : file.getName());
            }
        });

        Button escolherImagens = secondaryButton("Escolher Imagens");
        escolherImagens.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Selecionar imagens do design");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Imagens",
                            "*.png",
                            "*.jpg",
                            "*.jpeg",
                            "*.webp"
                    )
            );

            List<File> files = chooser.showOpenMultipleDialog(null);

            if (files != null && !files.isEmpty()) {
                imagensSelecionadas.addAll(files);
            }
        });

        Button removerImagem = secondaryButton("Remover Imagem");
        removerImagem.setOnAction(e -> {
            File selecionado = imagensListView.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                imagensSelecionadas.remove(selecionado);
            }
        });

        HBox botoesImagens = new HBox(10, escolherImagens, removerImagem);

        VBox formCard = card();
        Label formTitle = sectionTitle("Proposta");
        formCard.getChildren().addAll(
                formTitle,
                new Label("Descrição do designer"),
                descricaoDesigner,
                new Label("Imagens da proposta"),
                imagensListView,
                botoesImagens
        );

        Button guardar = primaryButton("Guardar Proposta");
        Button cancelar = secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

        guardar.setOnAction(e -> {
            if (descricaoDesigner.getText() == null || descricaoDesigner.getText().isBlank()) {
                mostrarErro("Preenche a descrição da proposta.");
                return;
            }

            if (imagensSelecionadas.isEmpty()) {
                mostrarErro("Seleciona pelo menos uma imagem da proposta.");
                return;
            }

            List<Path> imagens = imagensSelecionadas.stream()
                    .map(File::toPath)
                    .toList();

            guardar.setDisable(true);
            cancelar.setDisable(true);
            escolherImagens.setDisable(true);
            removerImagem.setDisable(true);
            estado.setText("A guardar proposta...");

            Task<DesignEncomendaDto> task = new Task<>() {
                @Override
                protected DesignEncomendaDto call() {
                    return designApiService.criar(
                            BigDecimal.valueOf(pedido.getEncomendaId()),
                            descricaoDesigner.getText().trim(),
                            imagens
                    );
                }
            };

            task.setOnSucceeded(event -> {
                estado.setText("Proposta criada com sucesso.");
                guardar.setDisable(false);
                cancelar.setDisable(false);
                escolherImagens.setDisable(false);
                removerImagem.setDisable(false);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Proposta criada");
                alert.setContentText("A proposta foi enviada para o cliente.");
                alert.showAndWait();

                shell.navigateTo(DesignerPage.HISTORICO);
            });

            task.setOnFailed(event -> {
                estado.setText("Erro ao guardar proposta.");
                guardar.setDisable(false);
                cancelar.setDisable(false);
                escolherImagens.setDisable(false);
                removerImagem.setDisable(false);
                mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        HBox actions = new HBox(10, guardar, cancelar);

        root.getChildren().addAll(estado, infoCard, formCard, actions);
        return root;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
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

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold;");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-background-radius: 10; -fx-border-radius: 10;");
        return button;
    }
}