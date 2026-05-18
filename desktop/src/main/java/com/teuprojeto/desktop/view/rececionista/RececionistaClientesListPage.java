package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class RececionistaClientesListPage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaClientesListPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Clientes");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte, pesquise e crie clientes da fábrica.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar cliente...");
        search.setPrefWidth(380);
        search.setStyle(
                "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-padding: 12;" +
                        "-fx-background-color: white;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button novo = RececionistaUiFactory.primaryButton("Novo Cliente");
        novo.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_CRIAR));

        Button atualizar = outlineButton("Atualizar");

        topBar.getChildren().addAll(search, spacer, novo, atualizar);

        Label statusLabel = new Label("A carregar clientes...");
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        List<ClienteRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) ->
                atualizarLista(lista, cache, newValue)
        );

        root.getChildren().addAll(header, topBar, statusLabel, lista);

        Runnable carregar = () -> carregarClientes(cache, lista, statusLabel, search.getText());

        atualizar.setOnAction(e -> carregar.run());
        carregar.run();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarClientes(
            List<ClienteRow> cache,
            VBox lista,
            Label statusLabel,
            String termo
    ) {
        statusLabel.setText("A carregar clientes...");

        Task<List<ClienteDto>> task = new Task<>() {
            @Override
            protected List<ClienteDto> call() {
                return clienteApiService.listarTodos();
            }
        };

        task.setOnSucceeded(event -> {
            cache.clear();

            cache.addAll(
                    task.getValue().stream()
                            .map(this::toRow)
                            .toList()
            );

            atualizarLista(lista, cache, termo);
            statusLabel.setText("Clientes carregados: " + cache.size());
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar clientes.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter clientes");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private ClienteRow toRow(ClienteDto dto) {
        return new ClienteRow(
                dto.getCod(),
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getTipo()
        );
    }

    private void atualizarLista(
            VBox lista,
            List<ClienteRow> clientes,
            String termo
    ) {
        lista.getChildren().clear();

        List<ClienteRow> filtrados = clientes.stream()
                .filter(c -> matches(termo, c))
                .toList();

        if (filtrados.isEmpty()) {
            lista.getChildren().add(emptyCard("Nenhum cliente encontrado."));
            return;
        }

        for (ClienteRow cliente : filtrados) {
            lista.getChildren().add(buildCard(cliente));
        }
    }

    private boolean matches(String termo, ClienteRow cliente) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return texto(cliente.getNome()).contains(t)
                || texto(cliente.getEmail()).contains(t)
                || texto(cliente.getTelefone()).contains(t)
                || texto(cliente.getTipo()).contains(t);
    }

    private String texto(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private VBox buildCard(ClienteRow cliente) {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setMinSize(58, 58);
        avatar.setPrefSize(58, 58);
        avatar.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label avatarText = new Label(obterIniciais(cliente.getNome()));
        avatarText.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
        avatar.getChildren().add(avatarText);

        VBox left = new VBox(4);

        Label nome = new Label(valor(cliente.getNome()));
        nome.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label email = new Label(valor(cliente.getEmail()));
        email.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(nome, email);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tipo = badge(valor(cliente.getTipo()), "#dbeafe", "#1d4ed8");

        Button ver = RececionistaUiFactory.primaryButton("Ver Cliente");
        ver.setOnAction(e -> {
            shell.setClienteSelecionado(cliente);
            shell.navigateTo(RececionistaPage.CLIENTES_VER);
        });

        top.getChildren().addAll(avatar, left, spacer, tipo, ver);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Telefone", valor(cliente.getTelefone())),
                infoBlock("Código", cliente.getCod() == null ? "-" : String.valueOf(cliente.getCod())),
                infoBlock("Tipo de cliente", valor(cliente.getTipo()))
        );

        card.getChildren().addAll(top, infoGrid);

        return card;
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "CL";
        }

        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
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

    private String valor(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}