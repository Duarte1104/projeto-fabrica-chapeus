package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.DashboardRececionistaResponseDto;
import com.teuprojeto.desktop.service.DashboardApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RececionistaDashboardPage {

    private final RececionistaShellView shell;
    private final DashboardApiService dashboardApiService = new DashboardApiService();

    public RececionistaDashboardPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(26);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label totalClientes = statNumber("-");
        Label totalEncomendas = statNumber("-");
        Label prontas = statNumber("-");
        Label pagas = statNumber("-");
        Label totalFaturas = statNumber("-");

        stats.getChildren().addAll(
                statCard("Clientes", totalClientes, "Total registados", "#2563eb", "👥"),
                statCard("Encomendas", totalEncomendas, "Total geral", "#16a34a", "🛒"),
                statCard("Prontas", prontas, "Para faturar/entregar", "#f97316", "📦"),
                statCard("Pagas", pagas, "Finalizadas", "#7c3aed", "💳"),
                statCard("Faturas", totalFaturas, "Emitidas", "#0891b2", "📄")
        );

        HBox mainGrid = new HBox(22);

        VBox painelPrincipal = card();
        painelPrincipal.setPrefWidth(820);
        HBox.setHgrow(painelPrincipal, Priority.ALWAYS);

        HBox painelHeader = new HBox();
        painelHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                sectionTitle("Resumo de Encomendas"),
                smallText("Visão geral das encomendas atuais da fábrica.")
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button atualizar = smallOutlineButton("Atualizar");
        atualizar.setOnAction(e -> carregarDashboard(
                estado,
                totalClientes,
                totalEncomendas,
                prontas,
                pagas,
                totalFaturas,
                null,
                null,
                null,
                null
        ));

        painelHeader.getChildren().addAll(titleBox, headerSpacer, atualizar);

        Label aguardaDesign = pillValue("-", "#fee2e2", "#dc2626");
        Label emPreparacao = pillValue("-", "#dbeafe", "#2563eb");
        Label prontasEntrega = pillValue("-", "#ffedd5", "#ea580c");
        Label faturasEmitidas = pillValue("-", "#dcfce7", "#16a34a");

        VBox resumoRows = new VBox(12);
        resumoRows.getChildren().addAll(
                resumoRow("Aguarda Design", "Pedidos que ainda precisam de proposta do designer", aguardaDesign),
                resumoRow("Em Preparação", "Encomendas que já podem avançar para produção", emPreparacao),
                resumoRow("Prontas para Entrega", "Encomendas finalizadas e prontas para entrega", prontasEntrega),
                resumoRow("Faturas Emitidas", "Faturas emitidas pela receção", faturasEmitidas)
        );

        VBox actionsBox = new VBox(14);
        actionsBox.setPadding(new Insets(14, 0, 0, 0));

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnClientes = RececionistaUiFactory.primaryButton("Clientes");
        btnClientes.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        Button btnNovoCliente = RececionistaUiFactory.secondaryButton("Novo Cliente");
        btnNovoCliente.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_CRIAR));

        Button btnNovaEncomenda = RececionistaUiFactory.secondaryButton("Nova Encomenda");
        btnNovaEncomenda.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        Button btnFaturas = RececionistaUiFactory.secondaryButton("Faturas");
        btnFaturas.setOnAction(e -> shell.navigateTo(RececionistaPage.FATURAS));

        actions.getChildren().addAll(btnClientes, btnNovoCliente, btnNovaEncomenda, btnFaturas);

        actionsBox.getChildren().addAll(sectionTitle("Ações rápidas"), actions);

        painelPrincipal.getChildren().addAll(
                painelHeader,
                separator(),
                resumoRows,
                actionsBox
        );

        VBox painelDireito = new VBox(22);
        painelDireito.setPrefWidth(420);

        VBox prazosCard = card();
        prazosCard.getChildren().addAll(
                sectionTitle("Prazos Próximos"),
                prazoRow("Hoje", "Verificar encomendas prontas", "Prioridade alta", "#fee2e2", "#dc2626"),
                prazoRow("2 dias", "Confirmar designs pendentes", "Aguardam decisão", "#ffedd5", "#ea580c"),
                prazoRow("Esta semana", "Validar faturas emitidas", "Organização financeira", "#dbeafe", "#2563eb")
        );

        VBox alertasCard = card();
        alertasCard.getChildren().addAll(
                sectionTitle("Alertas"),
                alertBox("As encomendas prontas devem ser verificadas para faturação."),
                alertBox("Os pedidos com design devem ser acompanhados até aprovação do cliente.")
        );

        painelDireito.getChildren().addAll(prazosCard, alertasCard);

        mainGrid.getChildren().addAll(painelPrincipal, painelDireito);

        root.getChildren().addAll(estado, stats, mainGrid);

        carregarDashboard(
                estado,
                totalClientes,
                totalEncomendas,
                prontas,
                pagas,
                totalFaturas,
                aguardaDesign,
                emPreparacao,
                prontasEntrega,
                faturasEmitidas
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
            Label totalClientes,
            Label totalEncomendas,
            Label prontas,
            Label pagas,
            Label totalFaturas,
            Label aguardaDesign,
            Label emPreparacao,
            Label prontasEntrega,
            Label faturasEmitidas
    ) {
        estado.setText("A carregar dashboard...");

        Task<DashboardRececionistaResponseDto> task = new Task<>() {
            @Override
            protected DashboardRececionistaResponseDto call() {
                return dashboardApiService.obterDashboardRececionista();
            }
        };

        task.setOnSucceeded(event -> {
            DashboardRececionistaResponseDto dados = task.getValue();

            totalClientes.setText(String.valueOf(dados.getTotalClientes()));
            totalEncomendas.setText(String.valueOf(dados.getTotalEncomendas()));
            prontas.setText(String.valueOf(dados.getProntas()));
            pagas.setText(String.valueOf(dados.getPagas()));
            totalFaturas.setText(String.valueOf(dados.getTotalFaturas()));

            if (aguardaDesign != null) {
                aguardaDesign.setText(String.valueOf(dados.getAguardaDesign()));
            }

            if (emPreparacao != null) {
                emPreparacao.setText(String.valueOf(dados.getEmPreparacao()));
            }

            if (prontasEntrega != null) {
                prontasEntrega.setText(String.valueOf(dados.getProntas()));
            }

            if (faturasEmitidas != null) {
                faturasEmitidas.setText(String.valueOf(dados.getTotalFaturas()));
            }

            estado.setText("Dashboard atualizado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar dashboard.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter dashboard");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox statCard(String title, Label value, String subtitle, String color, String iconText) {
        VBox card = card();
        card.setPrefWidth(210);
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

    private HBox resumoRow(String title, String subtitle, Label value) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 17;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 17;"
        );

        VBox text = new VBox(4);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = smallText(subtitle);

        text.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(text, spacer, value);

        return row;
    }

    private Label pillValue(String value, String bg, String fg) {
        Label label = new Label(value);
        label.setMinWidth(48);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-font-size: 19;" +
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

        Label badgeLabel = new Label(badge);
        badgeLabel.setMinWidth(78);
        badgeLabel.setAlignment(Pos.CENTER);
        badgeLabel.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 10 8 10;" +
                        "-fx-background-radius: 12;"
        );

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = smallText(subtitle);

        text.getChildren().addAll(titleLabel, subtitleLabel);
        row.getChildren().addAll(badgeLabel, text);

        return row;
    }

    private Label alertBox(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-background-color: #eff6ff;" +
                        "-fx-text-fill: #1d4ed8;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 14;" +
                        "-fx-background-radius: 16;"
        );
        return label;
    }

    private Button smallOutlineButton(String text) {
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
}