package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.service.DesignApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        PedidoDesignRow pedido = shell.getPedidoSelecionado();

        VBox header = new VBox(6);

        Label title = new Label("Criar Proposta de Design");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Crie uma proposta visual para aprovação do cliente.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        if (pedido == null) {
            VBox card = card();

            Label aviso = new Label("Nenhum pedido foi selecionado.");
            aviso.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

            Button voltar = secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().addAll(header, card);

            return wrap(root);
        }

        Label estado = new Label("Pedido selecionado.");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox infoCard = card();

        HBox infoTop = new HBox(14);
        infoTop.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("🎨");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox infoText = new VBox(4);

        Label infoTitle = new Label("Informações do Pedido");
        infoTitle.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label infoSub = new Label("Dados principais da encomenda selecionada.");
        infoSub.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        infoText.getChildren().addAll(infoTitle, infoSub);
        infoTop.getChildren().addAll(icon, infoText);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Encomenda", pedido.getNumero()),
                infoBlock("Cliente", pedido.getCliente()),
                infoBlock("Entrega", pedido.getDataEntrega())
        );

        VBox descricaoClienteBox = new VBox(6);

        Label descTitle = new Label("Pedido do cliente");
        descTitle.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label descricaoCliente = new Label(pedido.getDescricaoDesign());
        descricaoCliente.setWrapText(true);
        descricaoCliente.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        descricaoClienteBox.getChildren().addAll(descTitle, descricaoCliente);

        infoCard.getChildren().addAll(infoTop, separator(), infoGrid, descricaoClienteBox);

        VBox formCard = card();

        Label formTitle = new Label("Proposta");
        formTitle.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label formSub = new Label("Adicione a descrição técnica e as imagens da proposta.");
        formSub.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        VBox descricaoBox = new VBox(8);

        Label descricaoLabel = fieldLabel("Descrição do designer");

        TextArea descricaoDesigner = new TextArea();
        descricaoDesigner.setPromptText("Descreve a proposta do design, materiais visuais, cores, posição do logótipo, etc.");
        descricaoDesigner.setPrefRowCount(7);
        descricaoDesigner.setWrapText(true);
        descricaoDesigner.setStyle(
                "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-background-color: white;" +
                        "-fx-padding: 10;"
        );

        descricaoBox.getChildren().addAll(descricaoLabel, descricaoDesigner);

        ObservableList<File> imagensSelecionadas = FXCollections.observableArrayList();

        ListView<File> imagensListView = new ListView<>(imagensSelecionadas);
        imagensListView.setPrefHeight(150);
        imagensListView.setPlaceholder(new Label("Nenhuma imagem selecionada."));
        imagensListView.setStyle(
                "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #dbe2ea;"
        );

        imagensListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);

                if (empty || file == null) {
                    setText(null);
                    return;
                }

                setText(file.getName());
                setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");
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

        HBox botoesImagens = new HBox(12, escolherImagens, removerImagem);

        VBox imagensBox = new VBox(8);
        imagensBox.getChildren().addAll(
                fieldLabel("Imagens da proposta"),
                imagensListView,
                botoesImagens
        );

        formCard.getChildren().addAll(
                formTitle,
                formSub,
                separator(),
                descricaoBox,
                imagensBox
        );

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

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

        actions.getChildren().addAll(guardar, cancelar);

        root.getChildren().addAll(header, estado, infoCard, formCard, actions);

        return wrap(root);
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private VBox card() {
        VBox card = new VBox(16);
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

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 18 0 18;"
        );
        return button;
    }

    private Button secondaryButton(String text) {
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
}