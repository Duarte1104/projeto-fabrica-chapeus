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

public class DesignerPedidosDesignPage {

    private final DesignerShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();
    private final DesignApiService designApiService = new DesignApiService();

    public DesignerPedidosDesignPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Pedidos de Design");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Pedidos que ainda precisam de proposta ou revisão.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar pedido...");
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

        Label estado = new Label("A carregar pedidos...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        List<PedidoDesignRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) ->
                atualizarLista(lista, cache, newValue)
        );

        root.getChildren().addAll(header, topBar, estado, lista);

        Runnable carregar = () -> carregarPedidos(cache, lista, estado, search.getText());

        atualizar.setOnAction(e -> carregar.run());
        carregar.run();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarPedidos(
            List<PedidoDesignRow> cache,
            VBox lista,
            Label estado,
            String termo
    ) {
        estado.setText("A carregar pedidos...");

        Task<List<PedidoDesignRow>> task = new Task<>() {
            @Override
            protected List<PedidoDesignRow> call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<ClienteDto> clientes = clienteApiService.listarTodos();
                List<DesignEncomendaDto> designs = designApiService.listarTodos();

                Map<Integer, String> nomesClientes = clientes.stream()
                        .filter(c -> c.getCod() != null)
                        .collect(Collectors.toMap(
                                ClienteDto::getCod,
                                c -> c.getNome() == null ? "Cliente sem nome" : c.getNome(),
                                (a, b) -> a
                        ));

                Map<Long, DesignEncomendaDto> ultimoDesignPorEncomenda =
                        designs.stream()
                                .filter(d -> d.getIdEncomenda() != null)
                                .collect(Collectors.toMap(
                                        d -> d.getIdEncomenda().longValue(),
                                        d -> d,
                                        (a, b) -> {
                                            if (a.getDataCriacao() == null) return b;
                                            if (b.getDataCriacao() == null) return a;

                                            return b.getDataCriacao()
                                                    .compareTo(a.getDataCriacao()) > 0 ? b : a;
                                        }
                                ));

                return encomendas.stream()
                        .filter(e -> Boolean.TRUE.equals(e.getDesign()))
                        .filter(e -> {
                            DesignEncomendaDto design = ultimoDesignPorEncomenda.get(e.getNum());

                            if (design == null) {
                                return true;
                            }

                            return "REJEITADO_CLIENTE".equalsIgnoreCase(design.getEstadoDesign());
                        })
                        .map(e -> new PedidoDesignRow(
                                e.getNum(),
                                "ENC-" + e.getNum(),
                                nomesClientes.getOrDefault(e.getIdcliente(), "Cliente #" + e.getIdcliente()),
                                e.getDataEntrega() == null ? "-" : e.getDataEntrega(),
                                e.getDescricaoDesign() == null || e.getDescricaoDesign().isBlank()
                                        ? "-"
                                        : e.getDescricaoDesign()
                        ))
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            cache.clear();
            cache.addAll(task.getValue());
            atualizarLista(lista, cache, termo);
            estado.setText("Pedidos pendentes: " + cache.size());
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar pedidos.");

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
            List<PedidoDesignRow> pedidos,
            String termo
    ) {
        lista.getChildren().clear();

        List<PedidoDesignRow> filtrados = pedidos.stream()
                .filter(p -> matches(termo, p))
                .toList();

        if (filtrados.isEmpty()) {
            lista.getChildren().add(emptyCard("Não existem pedidos de design pendentes."));
            return;
        }

        for (PedidoDesignRow pedido : filtrados) {
            lista.getChildren().add(buildCard(pedido));
        }
    }

    private boolean matches(String termo, PedidoDesignRow pedido) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return pedido.getNumero().toLowerCase().contains(t)
                || pedido.getCliente().toLowerCase().contains(t)
                || pedido.getDataEntrega().toLowerCase().contains(t)
                || pedido.getDescricaoDesign().toLowerCase().contains(t);
    }

    private VBox buildCard(PedidoDesignRow pedido) {
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
        icon.setStyle("-fx-background-color: #fef2f2; -fx-background-radius: 18;");

        Label iconText = new Label("🎨");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label numero = new Label(pedido.getNumero());
        numero.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label cliente = new Label(pedido.getCliente());
        cliente.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(numero, cliente);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label estado = badge("Pendente", "#fee2e2", "#dc2626");

        Button criar = primaryButton("Criar Proposta");
        criar.setOnAction(e -> {
            shell.setPedidoSelecionado(pedido);
            shell.navigateTo(DesignerPage.CRIAR_PROPOSTA);
        });

        top.getChildren().addAll(icon, left, spacer, estado, criar);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Data entrega", pedido.getDataEntrega()),
                infoBlock("Tipo", "Design personalizado")
        );

        VBox descricaoBox = new VBox(6);

        Label descricaoTitle = new Label("Pedido do cliente");
        descricaoTitle.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label descricao = new Label(pedido.getDescricaoDesign());
        descricao.setWrapText(true);
        descricao.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        descricaoBox.getChildren().addAll(descricaoTitle, descricao);

        card.getChildren().addAll(top, infoGrid, descricaoBox);

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
}