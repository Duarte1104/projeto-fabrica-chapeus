package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.service.EncomendaApiService;
import com.teuprojeto.desktop.service.FuncionarioDataService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class FuncionarioDashboardPage {

    private final FuncionarioShellView shell;
    private final FuncionarioDataService funcionarioDataService = new FuncionarioDataService();
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();

    public FuncionarioDashboardPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(26);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label disponiveisValue = statNumber("-");
        Label minhasValue = statNumber("-");
        Label historicoValue = statNumber("-");

        stats.getChildren().addAll(
                statCard("Disponíveis", disponiveisValue, "Prontas para aceitar", "#2563eb", "📦"),
                statCard("Minhas Encomendas", minhasValue, "Em produção", "#16a34a", "📋"),
                statCard("Histórico", historicoValue, "Prontas ou pagas", "#f97316", "✅")
        );

        HBox mainGrid = new HBox(22);

        VBox painelPrincipal = card();
        painelPrincipal.setPrefWidth(820);
        HBox.setHgrow(painelPrincipal, Priority.ALWAYS);

        HBox painelHeader = new HBox();
        painelHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                sectionTitle("Encomendas Disponíveis"),
                smallText("Escolha uma encomenda para atribuir a si.")
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button atualizar = smallOutlineButton("Atualizar");

        painelHeader.getChildren().addAll(titleBox, headerSpacer, atualizar);

        VBox disponiveisLista = new VBox(12);
        disponiveisLista.getChildren().add(new Label("A carregar encomendas disponíveis..."));

        painelPrincipal.getChildren().addAll(
                painelHeader,
                separator(),
                disponiveisLista
        );

        VBox painelDireito = new VBox(22);
        painelDireito.setPrefWidth(420);

        VBox prazosCard = card();
        VBox prazosLista = new VBox(12);
        prazosLista.getChildren().add(new Label("A carregar prazos..."));

        prazosCard.getChildren().addAll(
                sectionTitle("Prazos Próximos"),
                prazosLista
        );



        painelDireito.getChildren().addAll(prazosCard);

        mainGrid.getChildren().addAll(painelPrincipal, painelDireito);

        root.getChildren().addAll(estado, stats, mainGrid);

        atualizar.setOnAction(e -> carregarDados(
                estado,
                disponiveisValue,
                minhasValue,
                historicoValue,
                disponiveisLista,
                prazosLista
        ));

        carregarDados(
                estado,
                disponiveisValue,
                minhasValue,
                historicoValue,
                disponiveisLista,
                prazosLista
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private void carregarDados(
            Label estado,
            Label disponiveisValue,
            Label minhasValue,
            Label historicoValue,
            VBox disponiveisLista,
            VBox prazosLista
    ) {
        estado.setText("A carregar dashboard...");

        Task<FuncionarioDataService.FuncionarioDashboardData> task = new Task<>() {
            @Override
            protected FuncionarioDataService.FuncionarioDashboardData call() {
                return funcionarioDataService.carregarDashboard(shell.getFuncionarioId());
            }
        };

        task.setOnSucceeded(event -> {
            var data = task.getValue();

            List<FuncionarioEncomendaRow> minhasAtivas = data.minhasEncomendas().stream()
                    .filter(e -> e.getEstado() != null && e.getEstado().equalsIgnoreCase("Em preparação"))
                    .toList();

            List<FuncionarioEncomendaRow> historico = data.minhasEncomendas().stream()
                    .filter(e -> e.getEstado() != null &&
                            (e.getEstado().equalsIgnoreCase("Pronta") ||
                                    e.getEstado().equalsIgnoreCase("Paga")))
                    .toList();

            List<FuncionarioEncomendaRow> disponiveis = data.encomendasDisponiveis();

            disponiveisValue.setText(String.valueOf(disponiveis.size()));
            minhasValue.setText(String.valueOf(minhasAtivas.size()));
            historicoValue.setText(String.valueOf(historico.size()));

            disponiveisLista.getChildren().clear();

            if (disponiveis.isEmpty()) {
                disponiveisLista.getChildren().add(emptyText("Não existem encomendas disponíveis neste momento."));
            } else {
                for (FuncionarioEncomendaRow row : disponiveis) {
                    disponiveisLista.getChildren().add(buildDisponivelRow(
                            row,
                            estado,
                            disponiveisValue,
                            minhasValue,
                            historicoValue,
                            disponiveisLista,
                            prazosLista
                    ));
                }
            }

            prazosLista.getChildren().clear();

            if (disponiveis.isEmpty()) {
                prazosLista.getChildren().add(emptyText("Sem prazos próximos."));
            } else {
                List<FuncionarioEncomendaRow> prazosOrdenados = disponiveis.stream()
                        .filter(e -> e.getDataLimite() != null && !e.getDataLimite().isBlank())
                        .sorted(Comparator.comparing(e -> LocalDate.parse(e.getDataLimite())))
                        .limit(3)
                        .toList();

                for (FuncionarioEncomendaRow row : prazosOrdenados) {
                    LocalDate dataLimite = LocalDate.parse(row.getDataLimite());
                    long dias = ChronoUnit.DAYS.between(LocalDate.now(), dataLimite);

                    String badge;
                    String bg;
                    String fg;

                    if (dias < 0) {
                        badge = "Atrasada";
                        bg = "#fee2e2";
                        fg = "#dc2626";
                    } else if (dias == 0) {
                        badge = "Hoje";
                        bg = "#fee2e2";
                        fg = "#dc2626";
                    } else if (dias == 1) {
                        badge = "1 dia";
                        bg = "#ffedd5";
                        fg = "#ea580c";
                    } else {
                        badge = dias + " dias";
                        bg = "#dbeafe";
                        fg = "#2563eb";
                    }

                    prazosLista.getChildren().add(prazoRow(
                            badge,
                            row.getCodigoEncomenda(),
                            row.getDataLimite(),
                            bg,
                            fg
                    ));
                }
            }

            estado.setText("Dashboard carregado.");
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

    private HBox buildDisponivelRow(
            FuncionarioEncomendaRow encomenda,
            Label estado,
            Label disponiveisValue,
            Label minhasValue,
            Label historicoValue,
            VBox disponiveisLista,
            VBox prazosLista
    ) {
        HBox row = new HBox(18);
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
                "-fx-background-color: #eff6ff;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #dbeafe;" +
                        "-fx-border-radius: 16;"
        );

        Label iconText = new Label("🎩");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox encomendaBox = new VBox(4);
        Label codigo = new Label(encomenda.getCodigoEncomenda());
        codigo.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        Label produto = new Label(encomenda.getProduto());
        produto.setStyle("-fx-font-size: 13; -fx-text-fill: #0f172a;");

        encomendaBox.getChildren().addAll(codigo, produto);
        encomendaBox.setPrefWidth(210);

        VBox clienteBox = infoBlock("Cliente", encomenda.getCliente());
        VBox quantidadeBox = infoBlock("Quantidade", String.valueOf(encomenda.getQuantidadeTotal()));
        VBox dataBox = infoBlock("Data limite", encomenda.getDataLimite());

        Button aceitar = FuncionarioUiFactory.primaryButton("Aceitar");
        aceitar.setOnAction(e -> aceitarEncomenda(
                encomenda,
                estado,
                disponiveisValue,
                minhasValue,
                historicoValue,
                disponiveisLista,
                prazosLista
        ));

        row.getChildren().addAll(
                icon,
                encomendaBox,
                clienteBox,
                quantidadeBox,
                dataBox,
                aceitar
        );

        HBox.setHgrow(encomendaBox, Priority.ALWAYS);

        return row;
    }

    private void aceitarEncomenda(
            FuncionarioEncomendaRow encomenda,
            Label estado,
            Label disponiveisValue,
            Label minhasValue,
            Label historicoValue,
            VBox disponiveisLista,
            VBox prazosLista
    ) {
        estado.setText("A aceitar encomenda " + encomenda.getCodigoEncomenda() + "...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                encomendaApiService.aceitarEncomenda(encomenda.getIdEncomenda(), shell.getFuncionarioId());
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Encomenda aceite");
            alert.setContentText("A encomenda foi atribuída ao funcionário.");
            alert.showAndWait();

            carregarDados(
                    estado,
                    disponiveisValue,
                    minhasValue,
                    historicoValue,
                    disponiveisLista,
                    prazosLista
            );
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao aceitar encomenda.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox statCard(String title, Label value, String subtitle, String color, String iconText) {
        VBox card = card();
        card.setPrefWidth(300);
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

    private VBox infoBlock(String title, String value) {
        VBox box = new VBox(5);
        box.setPrefWidth(130);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label valueLabel = new Label(value == null ? "-" : value);
        valueLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
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

    private HBox productionRow(String title, String value, String color) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #334155;");

        row.getChildren().addAll(dot, titleLabel, spacer, valueLabel);
        return row;
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
}