package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.DashboardGestorDto;
import com.teuprojeto.desktop.service.DashboardApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;

public class GestorDashboardPage {

    private final GestorShellView shell;
    private final DashboardApiService dashboardApiService = new DashboardApiService();

    public GestorDashboardPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(26);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label saldoAtual = statNumber("-");
        Label totalEncomendas = statNumber("-");
        Label movimentos = statNumber("-");
        Label stockBaixo = statNumber("-");

        stats.getChildren().addAll(
                statCard("Saldo Atual", saldoAtual, "Conta da empresa", "#2563eb", "€"),
                statCard("Encomendas", totalEncomendas, "Total geral", "#16a34a", "📦"),
                statCard("Movimentos", movimentos, "Financeiros", "#f97316", "↔"),
                statCard("Stock Baixo", stockBaixo, "Materiais críticos", "#dc2626", "⚠")
        );

        HBox mainGrid = new HBox(22);

        VBox painelPrincipal = card();
        painelPrincipal.setPrefWidth(820);
        HBox.setHgrow(painelPrincipal, Priority.ALWAYS);

        HBox painelHeader = new HBox();
        painelHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                sectionTitle("Resumo Operacional"),
                smallText("Estado geral da produção, encomendas e faturação.")
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button atualizar = smallOutlineButton("Atualizar");

        painelHeader.getChildren().addAll(titleBox, headerSpacer, atualizar);

        Label aguardaDesign = pillValue("-", "#fee2e2", "#dc2626");
        Label emPreparacao = pillValue("-", "#dbeafe", "#2563eb");
        Label prontas = pillValue("-", "#ffedd5", "#ea580c");
        Label pagas = pillValue("-", "#dcfce7", "#16a34a");

        VBox resumoRows = new VBox(12);
        resumoRows.getChildren().addAll(
                resumoRow("Aguarda Design", "Encomendas ainda dependentes de proposta do designer", aguardaDesign),
                resumoRow("Em Preparação", "Encomendas disponíveis para produção", emPreparacao),
                resumoRow("Prontas", "Encomendas concluídas e prontas para faturar", prontas),
                resumoRow("Pagas", "Encomendas finalizadas financeiramente", pagas)
        );

        VBox actionsBox = new VBox(14);
        actionsBox.setPadding(new Insets(14, 0, 0, 0));

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnStock = GestorUiFactory.primaryButton("Stock");
        btnStock.setOnAction(e -> shell.navigateTo(GestorPage.STOCK));

        Button btnChapeus = GestorUiFactory.secondaryButton("Chapéus");
        btnChapeus.setOnAction(e -> shell.navigateTo(GestorPage.CHAPEUS));

        Button btnFaturacao = GestorUiFactory.secondaryButton("Faturação");
        btnFaturacao.setOnAction(e -> shell.navigateTo(GestorPage.FATURACAO));

        Button btnBalanco = GestorUiFactory.secondaryButton("Balanço");
        btnBalanco.setOnAction(e -> shell.navigateTo(GestorPage.BALANCO));

        actions.getChildren().addAll(btnStock, btnChapeus, btnFaturacao, btnBalanco);
        actionsBox.getChildren().addAll(sectionTitle("Ações rápidas"), actions);

        painelPrincipal.getChildren().addAll(
                painelHeader,
                separator(),
                resumoRows,
                actionsBox
        );

        VBox painelDireito = new VBox(22);
        painelDireito.setPrefWidth(420);

        VBox alertasCard = card();
        alertasCard.getChildren().addAll(
                sectionTitle("Alertas de Gestão"),
                alertBox("Verifique materiais com stock abaixo do mínimo."),
                alertBox("Acompanhe encomendas em preparação para evitar atrasos."),
                alertBox("Consulte o balanço para analisar despesas e faturação.")
        );

        VBox financeiroCard = card();
        financeiroCard.getChildren().addAll(
                sectionTitle("Resumo Financeiro"),
                financeiroRow("Saldo atual", saldoAtual),
                financeiroRow("Movimentos registados", movimentos),
                financeiroRow("Encomendas pagas", pagas)
        );

        painelDireito.getChildren().addAll(alertasCard, financeiroCard);

        mainGrid.getChildren().addAll(painelPrincipal, painelDireito);

        root.getChildren().addAll(estado, stats, mainGrid);

        atualizar.setOnAction(e -> carregarDashboard(
                estado,
                saldoAtual,
                totalEncomendas,
                movimentos,
                stockBaixo,
                aguardaDesign,
                emPreparacao,
                prontas,
                pagas
        ));

        carregarDashboard(
                estado,
                saldoAtual,
                totalEncomendas,
                movimentos,
                stockBaixo,
                aguardaDesign,
                emPreparacao,
                prontas,
                pagas
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
            Label saldoAtual,
            Label totalEncomendas,
            Label movimentos,
            Label stockBaixo,
            Label aguardaDesign,
            Label emPreparacao,
            Label prontas,
            Label pagas
    ) {
        estado.setText("A carregar dashboard...");

        Task<DashboardGestorDto> task = new Task<>() {
            @Override
            protected DashboardGestorDto call() {
                return dashboardApiService.obterDashboardGestor();
            }
        };

        task.setOnSucceeded(event -> {
            DashboardGestorDto dto = task.getValue();

            saldoAtual.setText(formatarMoeda(dto.getSaldoAtual()));
            totalEncomendas.setText(String.valueOf(dto.getTotalEncomendas()));
            movimentos.setText(String.valueOf(dto.getTotalMovimentos()));
            stockBaixo.setText(String.valueOf(dto.getMateriaisAbaixoMinimo()));

            aguardaDesign.setText(String.valueOf(dto.getAguardaDesign()));
            emPreparacao.setText(String.valueOf(dto.getEmPreparacao()));
            prontas.setText(String.valueOf(dto.getProntas()));
            pagas.setText(String.valueOf(dto.getPagas()));

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
        card.setPrefWidth(260);
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
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
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

    private HBox financeiroRow(String title, Label value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label clone = new Label(value.getText());
        clone.textProperty().bind(value.textProperty());
        clone.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        row.getChildren().addAll(titleLabel, spacer, clone);

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

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00 €";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }
}