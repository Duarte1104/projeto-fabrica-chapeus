package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RececionistaEncomendasListPage {

    private final RececionistaShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaEncomendasListPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Encomendas");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte, pesquise e acompanhe as encomendas da fábrica.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar encomenda...");
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

        Button nova = RececionistaUiFactory.primaryButton("Nova Encomenda");
        nova.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        Button atualizar = outlineButton("Atualizar");

        topBar.getChildren().addAll(search, spacer, nova, atualizar);

        Label statusLabel = new Label("A carregar encomendas...");
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        List<EncomendaRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) ->
                atualizarLista(lista, cache, newValue, statusLabel)
        );

        root.getChildren().addAll(header, topBar, statusLabel, lista);

        Runnable carregar = () -> carregarEncomendas(cache, lista, statusLabel, search.getText());

        atualizar.setOnAction(e -> carregar.run());
        carregar.run();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarEncomendas(
            List<EncomendaRow> cache,
            VBox lista,
            Label statusLabel,
            String termo
    ) {
        statusLabel.setText("A carregar encomendas...");

        Task<List<EncomendaRow>> task = new Task<>() {
            @Override
            protected List<EncomendaRow> call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();

                Map<Integer, String> nomesClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(
                                ClienteDto::getCod,
                                c -> c.getNome() == null ? "Cliente sem nome" : c.getNome(),
                                (a, b) -> a
                        ));

                return encomendas.stream()
                        .map(encomenda -> new EncomendaRow(
                                encomenda.getNum(),
                                "ENC-" + encomenda.getNum(),
                                nomesClientes.getOrDefault(
                                        encomenda.getIdcliente(),
                                        "Cliente #" + encomenda.getIdcliente()
                                ),
                                mapearEstado(encomenda.getIdestado()),
                                Boolean.TRUE.equals(encomenda.getDesign()) ? "Sim" : "Não"
                        ))
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            cache.clear();
            cache.addAll(task.getValue());

            atualizarLista(lista, cache, termo, statusLabel);
            statusLabel.setText("Encomendas carregadas: " + cache.size());
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar encomendas.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter encomendas");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void atualizarLista(
            VBox lista,
            List<EncomendaRow> encomendas,
            String termo,
            Label statusLabel
    ) {
        lista.getChildren().clear();

        List<EncomendaRow> filtradas = encomendas.stream()
                .filter(e -> matches(termo, e))
                .toList();

        if (filtradas.isEmpty()) {
            lista.getChildren().add(emptyCard("Nenhuma encomenda encontrada."));
            return;
        }

        for (EncomendaRow encomenda : filtradas) {
            lista.getChildren().add(buildCard(encomenda, encomendas, lista, statusLabel));
        }
    }

    private boolean matches(String termo, EncomendaRow encomenda) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return texto(encomenda.getNumero()).contains(t)
                || texto(encomenda.getCliente()).contains(t)
                || texto(encomenda.getEstado()).contains(t)
                || texto(encomenda.getDesign()).contains(t);
    }

    private VBox buildCard(
            EncomendaRow encomenda,
            List<EncomendaRow> cache,
            VBox lista,
            Label statusLabel
    ) {
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

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("📦");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label numero = new Label(encomenda.getNumero());
        numero.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label cliente = new Label(encomenda.getCliente());
        cliente.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(numero, cliente);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label estado = estadoBadge(encomenda.getEstado());
        Label design = badge(
                "Design: " + encomenda.getDesign(),
                encomenda.getDesign().equalsIgnoreCase("Sim") ? "#fef3c7" : "#e5e7eb",
                encomenda.getDesign().equalsIgnoreCase("Sim") ? "#92400e" : "#334155"
        );

        Button ver = RececionistaUiFactory.primaryButton("Ver");
        ver.setOnAction(e -> {
            shell.setEncomendaSelecionadaId(encomenda.getId());
            shell.navigateTo(RececionistaPage.ENCOMENDAS_VER);
        });

        Button apagar = dangerButton("Apagar");
        apagar.setOnAction(e -> confirmarEApagar(encomenda, cache, lista, statusLabel));

        top.getChildren().addAll(icon, left, spacer, estado, design, ver, apagar);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Número", encomenda.getNumero()),
                infoBlock("Cliente", encomenda.getCliente()),
                infoBlock("Estado", formatarEstado(encomenda.getEstado())),
                infoBlock("Design", encomenda.getDesign())
        );

        card.getChildren().addAll(top, infoGrid);

        return card;
    }

    private void confirmarEApagar(
            EncomendaRow row,
            List<EncomendaRow> cache,
            VBox lista,
            Label statusLabel
    ) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setHeaderText("Confirmar eliminação");
        confirmacao.setContentText("Tem a certeza que deseja apagar a encomenda " + row.getNumero() + "?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }

        statusLabel.setText("A apagar encomenda...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                encomendaApiService.apagar(row.getId());
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText("Encomenda apagada");
            ok.setContentText("A encomenda foi apagada com sucesso.");
            ok.showAndWait();

            carregarEncomendas(cache, lista, statusLabel, "");
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao apagar encomenda.");

            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setHeaderText("Erro ao apagar encomenda");
            erro.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            erro.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

    private Label estadoBadge(String estado) {
        if (estado == null) {
            return badge("-", "#e5e7eb", "#334155");
        }

        return switch (estado.toUpperCase()) {
            case "AGUARDA_DESIGN" -> badge("Aguarda design", "#fee2e2", "#dc2626");
            case "PREPARACAO", "EM PREPARAÇÃO", "EM_PREPARACAO" -> badge("Em preparação", "#dbeafe", "#2563eb");
            case "PRONTA" -> badge("Pronta", "#ffedd5", "#ea580c");
            case "PAGA" -> badge("Paga", "#dcfce7", "#15803d");
            default -> badge(estado, "#e5e7eb", "#334155");
        };
    }

    private String formatarEstado(String estado) {
        if (estado == null) {
            return "-";
        }

        return switch (estado.toUpperCase()) {
            case "AGUARDA_DESIGN" -> "Aguarda design";
            case "PREPARACAO", "EM_PREPARACAO" -> "Em preparação";
            case "PRONTA" -> "Pronta";
            case "PAGA" -> "Paga";
            default -> estado;
        };
    }

    private String mapearEstado(Long idestado) {
        if (idestado == null) {
            return "SEM_ESTADO";
        }

        return switch (idestado.intValue()) {
            case 1 -> "AGUARDA_DESIGN";
            case 2 -> "PREPARACAO";
            case 3 -> "PRONTA";
            case 4 -> "PAGA";
            default -> "ESTADO_" + idestado;
        };
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

    private Button dangerButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #fecaca;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #dc2626;" +
                        "-fx-padding: 0 18 0 18;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }

    private String texto(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}