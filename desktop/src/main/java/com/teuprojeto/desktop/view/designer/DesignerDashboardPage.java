package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.DesignApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Comparator;
import java.util.List;

public class DesignerDashboardPage {

    private final DesignerShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final DesignApiService designApiService = new DesignApiService();

    public DesignerDashboardPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(26);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label pendentes = statNumber("-");
        Label enviados = statNumber("-");
        Label aprovados = statNumber("-");
        Label rejeitados = statNumber("-");

        stats.getChildren().addAll(
                statCard("Pedidos Pendentes", pendentes, "Aguardam proposta", "#dc2626", "🎨"),
                statCard("Enviados ao Cliente", enviados, "Aguardam resposta", "#f97316", "📤"),
                statCard("Aprovados", aprovados, "Aceites pelo cliente", "#16a34a", "✅"),
                statCard("Rejeitados", rejeitados, "Necessitam revisão", "#7c3aed", "↩")
        );

        HBox mainGrid = new HBox(22);

        VBox painelPrincipal = card();
        painelPrincipal.setPrefWidth(820);
        HBox.setHgrow(painelPrincipal, Priority.ALWAYS);

        HBox painelHeader = new HBox();
        painelHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                sectionTitle("Pedidos de Design"),
                smallText("Encomendas que precisam de proposta ou revisão.")
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button atualizar = smallOutlineButton("Atualizar");

        painelHeader.getChildren().addAll(titleBox, headerSpacer, atualizar);

        VBox pedidosLista = new VBox(12);
        pedidosLista.getChildren().add(emptyText("A carregar pedidos..."));

        VBox actionsBox = new VBox(14);
        actionsBox.setPadding(new Insets(14, 0, 0, 0));

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnPedidos = primaryButton("Ver Pedidos");
        btnPedidos.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

        Button btnHistorico = secondaryButton("Histórico");
        btnHistorico.setOnAction(e -> shell.navigateTo(DesignerPage.HISTORICO));

        actions.getChildren().addAll(btnPedidos, btnHistorico);
        actionsBox.getChildren().addAll(sectionTitle("Ações rápidas"), actions);

        painelPrincipal.getChildren().addAll(
                painelHeader,
                separator(),
                pedidosLista,
                actionsBox
        );

        VBox painelDireito = new VBox(22);
        painelDireito.setPrefWidth(420);

        VBox atividadeCard = card();
        VBox atividadeLista = new VBox(12);
        atividadeLista.getChildren().add(emptyText("A carregar atividade..."));

        atividadeCard.getChildren().addAll(
                sectionTitle("Atividade Recente"),
                atividadeLista
        );

        VBox prazosCard = card();
        VBox prazosLista = new VBox(12);
        prazosLista.getChildren().addAll(
                prazoRow("Hoje", "Rever propostas rejeitadas", "Prioridade alta", "#fee2e2", "#dc2626"),
                prazoRow("2 dias", "Enviar propostas pendentes", "Evitar atrasos", "#ffedd5", "#ea580c"),
                prazoRow("Esta semana", "Consultar histórico", "Acompanhar aprovações", "#dbeafe", "#2563eb")
        );

        prazosCard.getChildren().addAll(
                sectionTitle("Prazos de Design"),
                prazosLista
        );

        painelDireito.getChildren().addAll(atividadeCard, prazosCard);
        mainGrid.getChildren().addAll(painelPrincipal, painelDireito);

        root.getChildren().addAll(estado, stats, mainGrid);

        atualizar.setOnAction(e -> carregarDashboard(
                estado,
                pendentes,
                enviados,
                aprovados,
                rejeitados,
                pedidosLista,
                atividadeLista
        ));

        carregarDashboard(
                estado,
                pendentes,
                enviados,
                aprovados,
                rejeitados,
                pedidosLista,
                atividadeLista
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarDashboard(
            Label estado,
            Label pendentes,
            Label enviados,
            Label aprovados,
            Label rejeitados,
            VBox pedidosLista,
            VBox atividadeLista
    ) {
        estado.setText("A carregar dashboard...");

        Task<DashboardData> task = new Task<>() {
            @Override
            protected DashboardData call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<DesignEncomendaDto> designs = designApiService.listarTodos();
                return new DashboardData(encomendas, designs);
            }
        };

        task.setOnSucceeded(event -> {
            DashboardData data = task.getValue();

            long totalPendentes = data.encomendas().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getDesign()))
                    .filter(e -> Long.valueOf(1L).equals(e.getIdestado()))
                    .count();

            long totalEnviados = data.designs().stream()
                    .filter(d -> "ENVIADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            long totalAprovados = data.designs().stream()
                    .filter(d -> "APROVADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            long totalRejeitados = data.designs().stream()
                    .filter(d -> "REJEITADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            pendentes.setText(String.valueOf(totalPendentes));
            enviados.setText(String.valueOf(totalEnviados));
            aprovados.setText(String.valueOf(totalAprovados));
            rejeitados.setText(String.valueOf(totalRejeitados));

            pedidosLista.getChildren().clear();

            List<EncomendaDto> pedidos = data.encomendas().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getDesign()))
                    .filter(e -> Long.valueOf(1L).equals(e.getIdestado()))
                    .limit(5)
                    .toList();

            if (pedidos.isEmpty()) {
                pedidosLista.getChildren().add(emptyText("Não existem pedidos de design pendentes."));
            } else {
                for (EncomendaDto encomenda : pedidos) {
                    pedidosLista.getChildren().add(pedidoRow(encomenda));
                }
            }

            atividadeLista.getChildren().clear();

            List<DesignEncomendaDto> recentes = data.designs().stream()
                    .sorted(Comparator.comparing(
                            d -> d.getDataCriacao() == null ? "" : d.getDataCriacao(),
                            Comparator.reverseOrder()
                    ))
                    .limit(4)
                    .toList();

            if (recentes.isEmpty()) {
                atividadeLista.getChildren().add(emptyText("Ainda não existem propostas enviadas."));
            } else {
                for (DesignEncomendaDto design : recentes) {
                    atividadeLista.getChildren().add(atividadeRow(design));
                }
            }

            estado.setText("Dashboard atualizado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar dashboard.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox pedidoRow(EncomendaDto encomenda) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setMaxSize(58, 58);
        icon.setStyle(
                "-fx-background-color: #fef2f2;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #fecaca;" +
                        "-fx-border-radius: 16;"
        );

        Label iconText = new Label("🎨");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox info = new VBox(4);

        Label codigo = new Label("ENC-" + encomenda.getNum());
        codigo.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        Label descricao = new Label(encomenda.getDescricaoDesign() == null || encomenda.getDescricaoDesign().isBlank()
                ? "Pedido de personalização/design"
                : encomenda.getDescricaoDesign());

        descricao.setStyle("-fx-font-size: 13; -fx-text-fill: #0f172a;");
        descricao.setWrapText(true);

        info.getChildren().addAll(codigo, descricao);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = pill("Pendente", "#fee2e2", "#dc2626");

        Button abrir = primaryButton("Criar Proposta");
        abrir.setOnAction(e -> {
            PedidoDesignRow rowSelecionada = new PedidoDesignRow(
                    encomenda.getNum(),
                    "ENC-" + encomenda.getNum(),
                    "Cliente #" + encomenda.getIdcliente(),
                    encomenda.getDescricaoDesign(),
                    encomenda.getDataEntrega()
            );

            shell.setPedidoSelecionado(rowSelecionada);
            shell.navigateTo(DesignerPage.CRIAR_PROPOSTA);
        });

        row.getChildren().addAll(icon, info, spacer, badge, abrir);

        return row;
    }

    private HBox atividadeRow(DesignEncomendaDto design) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;"
        );

        String estado = design.getEstadoDesign() == null ? "SEM_ESTADO" : design.getEstadoDesign();

        String texto;
        String bg;
        String fg;

        switch (estado.toUpperCase()) {
            case "ENVIADO_CLIENTE" -> {
                texto = "Enviado";
                bg = "#ffedd5";
                fg = "#ea580c";
            }
            case "APROVADO_CLIENTE" -> {
                texto = "Aprovado";
                bg = "#dcfce7";
                fg = "#16a34a";
            }
            case "REJEITADO_CLIENTE" -> {
                texto = "Rejeitado";
                bg = "#fee2e2";
                fg = "#dc2626";
            }
            default -> {
                texto = estado;
                bg = "#e5e7eb";
                fg = "#334155";
            }
        }

        Label badge = pill(texto, bg, fg);

        VBox text = new VBox(3);

        Label title = new Label("Design #" + design.getId());
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Encomenda #" + design.getIdEncomenda());
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        text.getChildren().addAll(title, subtitle);

        row.getChildren().addAll(badge, text);

        return row;
    }

    private VBox statCard(String title, Label value, String subtitle, String color, String iconText) {
        VBox card = card();
        card.setPrefWidth(250);
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(54, 54);
        icon.setPrefSize(54, 54);
        icon.setMaxSize(54, 54);
        icon.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 14, 0, 0, 5);"
        );

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 14; -fx-font-weight: bold;");

        Label subtitleLabel = smallText(subtitle);

        text.getChildren().addAll(titleLabel, value, subtitleLabel);
        box.getChildren().addAll(icon, text);

        card.getChildren().add(box);
        return card;
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

    private Label statNumber(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private Label smallText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        return label;
    }

    private Label emptyText(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-text-fill: #64748b;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 18;" +
                        "-fx-background-radius: 16;"
        );
        return label;
    }

    private Label pill(String text, String bg, String fg) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-background-radius: 14;"
        );
        return label;
    }

    private HBox prazoRow(String badge, String title, String subtitle, String bg, String fg) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;"
        );

        Label badgeLabel = pill(badge, bg, fg);
        badgeLabel.setMinWidth(92);

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = smallText(subtitle);

        text.getChildren().addAll(titleLabel, subtitleLabel);
        row.getChildren().addAll(badgeLabel, text);

        return row;
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

    private Button smallOutlineButton(String text) {
        return secondaryButton(text);
    }

    private Region separator() {
        Region region = new Region();
        region.setPrefHeight(1);
        region.setStyle("-fx-background-color: #e5e7eb;");
        return region;
    }

    private record DashboardData(List<EncomendaDto> encomendas, List<DesignEncomendaDto> designs) {
    }
}