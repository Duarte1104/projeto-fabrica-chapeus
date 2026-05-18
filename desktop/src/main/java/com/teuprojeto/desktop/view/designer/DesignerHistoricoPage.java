package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.DesignApiService;
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
import java.util.stream.Collectors;

public class DesignerHistoricoPage {

    private final DesignerShellView shell;
    private final DesignApiService designApiService = new DesignApiService();
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public DesignerHistoricoPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Histórico de Designs");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Propostas enviadas ao cliente e respetivo estado.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar histórico...");
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

        Button atualizar = outlineButton("Atualizar");

        topBar.getChildren().addAll(search, spacer, atualizar);

        Label estado = new Label("A carregar histórico...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        List<HistoricoRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) ->
                atualizarLista(lista, cache, newValue)
        );

        root.getChildren().addAll(header, topBar, estado, lista);

        Runnable carregar = () -> carregarHistorico(cache, lista, estado, search.getText());

        atualizar.setOnAction(e -> carregar.run());
        carregar.run();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarHistorico(
            List<HistoricoRow> cache,
            VBox lista,
            Label estado,
            String termo
    ) {
        estado.setText("A carregar histórico...");

        Task<List<HistoricoRow>> task = new Task<>() {
            @Override
            protected List<HistoricoRow> call() {
                List<DesignEncomendaDto> designs = designApiService.listarTodos();
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();

                Map<Long, EncomendaDto> mapaEncomendas = encomendas.stream()
                        .collect(Collectors.toMap(
                                EncomendaDto::getNum,
                                e -> e,
                                (a, b) -> a
                        ));

                Map<Integer, String> mapaClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(
                                ClienteDto::getCod,
                                c -> c.getNome() == null ? "Cliente sem nome" : c.getNome(),
                                (a, b) -> a
                        ));

                return designs.stream()
                        .sorted((a, b) -> {
                            String da = a.getDataCriacao() == null ? "" : a.getDataCriacao();
                            String db = b.getDataCriacao() == null ? "" : b.getDataCriacao();
                            return db.compareTo(da);
                        })
                        .map(d -> {
                            Long encomendaId = d.getIdEncomenda() == null
                                    ? null
                                    : d.getIdEncomenda().longValue();

                            EncomendaDto encomenda = encomendaId == null
                                    ? null
                                    : mapaEncomendas.get(encomendaId);

                            String numero = encomendaId == null ? "-" : "ENC-" + encomendaId;
                            String cliente = "-";

                            if (encomenda != null) {
                                numero = "ENC-" + encomenda.getNum();
                                cliente = mapaClientes.getOrDefault(
                                        encomenda.getIdcliente(),
                                        "Cliente #" + encomenda.getIdcliente()
                                );
                            }

                            return new HistoricoRow(
                                    d.getId() == null ? "-" : String.valueOf(d.getId()),
                                    numero,
                                    cliente,
                                    d.getEstadoDesign() == null ? "-" : d.getEstadoDesign(),
                                    d.getDataCriacao() == null ? "-" : d.getDataCriacao()
                            );
                        })
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            cache.clear();
            cache.addAll(task.getValue());

            atualizarLista(lista, cache, termo);

            estado.setText("Histórico carregado: " + cache.size() + " propostas.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar histórico.");

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
            List<HistoricoRow> historico,
            String termo
    ) {
        lista.getChildren().clear();

        List<HistoricoRow> filtrados = historico.stream()
                .filter(h -> matches(termo, h))
                .toList();

        if (filtrados.isEmpty()) {
            lista.getChildren().add(emptyCard("Não existem propostas no histórico."));
            return;
        }

        for (HistoricoRow row : filtrados) {
            lista.getChildren().add(buildCard(row));
        }
    }

    private boolean matches(String termo, HistoricoRow row) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return row.designId.toLowerCase().contains(t)
                || row.encomenda.toLowerCase().contains(t)
                || row.cliente.toLowerCase().contains(t)
                || row.estado.toLowerCase().contains(t)
                || row.dataCriacao.toLowerCase().contains(t);
    }

    private VBox buildCard(HistoricoRow row) {
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

        Label iconText = new Label("🎨");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label design = new Label("Design #" + row.designId);
        design.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label encomenda = new Label(row.encomenda);
        encomenda.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(design, encomenda);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label estado = estadoBadge(row.estado);

        top.getChildren().addAll(icon, left, spacer, estado);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Cliente", row.cliente),
                infoBlock("Data", row.dataCriacao),
                infoBlock("Decisão", formatarEstado(row.estado))
        );

        Label nota = new Label("A aprovação ou rejeição é feita pelo cliente na plataforma web.");
        nota.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        card.getChildren().addAll(top, infoGrid, nota);

        return card;
    }

    private Label estadoBadge(String estado) {
        if (estado == null) {
            return badge("-", "#e5e7eb", "#334155");
        }

        return switch (estado.toUpperCase()) {
            case "ENVIADO_CLIENTE" -> badge("Enviado", "#ffedd5", "#ea580c");
            case "APROVADO_CLIENTE" -> badge("Aprovado", "#dcfce7", "#15803d");
            case "REJEITADO_CLIENTE" -> badge("Rejeitado", "#fee2e2", "#dc2626");
            default -> badge(estado, "#e5e7eb", "#334155");
        };
    }

    private String formatarEstado(String estado) {
        if (estado == null) {
            return "-";
        }

        return switch (estado.toUpperCase()) {
            case "ENVIADO_CLIENTE" -> "Enviado ao cliente";
            case "APROVADO_CLIENTE" -> "Aprovado pelo cliente";
            case "REJEITADO_CLIENTE" -> "Rejeitado pelo cliente";
            default -> estado;
        };
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

    private record HistoricoRow(
            String designId,
            String encomenda,
            String cliente,
            String estado,
            String dataCriacao
    ) {
    }
}