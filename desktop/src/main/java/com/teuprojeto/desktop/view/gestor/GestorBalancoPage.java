package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.ContaEmpresaDto;
import com.teuprojeto.desktop.dto.MovimentoFinanceiroDto;
import com.teuprojeto.desktop.service.FinanceiroApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class GestorBalancoPage {

    private final GestorShellView shell;
    private final FinanceiroApiService financeiroApiService = new FinanceiroApiService();

    public GestorBalancoPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Balanço Financeiro");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Resumo das entradas, saídas, saldo e margem financeira.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Label estado = new Label("A carregar balanço...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label receitasValor = statNumber("-");
        Label despesasValor = statNumber("-");
        Label saldoValor = statNumber("-");
        Label margemValor = statNumber("-");

        stats.getChildren().addAll(
                statCard("Receitas Totais", receitasValor, "Entradas registadas", "#16a34a", "€"),
                statCard("Despesas Totais", despesasValor, "Saídas registadas", "#dc2626", "€"),
                statCard("Saldo Atual", saldoValor, "Conta da empresa", "#2563eb", "€"),
                statCard("Margem", margemValor, "Receitas vs despesas", "#7c3aed", "%")
        );

        VBox resumoCard = card();
        VBox resumoLista = new VBox(12);

        resumoCard.getChildren().addAll(
                sectionHeader("📊", "Resumo Financeiro", "Indicadores principais da situação financeira."),
                separator(),
                resumoLista
        );

        HBox movimentosGrid = new HBox(22);

        VBox entradasCard = card();
        HBox.setHgrow(entradasCard, Priority.ALWAYS);

        VBox entradasLista = new VBox(14);

        entradasCard.getChildren().addAll(
                sectionHeader("📈", "Entradas Recentes", "Últimos movimentos de receita registados."),
                separator(),
                entradasLista
        );

        VBox saidasCard = card();
        HBox.setHgrow(saidasCard, Priority.ALWAYS);

        VBox saidasLista = new VBox(14);

        saidasCard.getChildren().addAll(
                sectionHeader("📉", "Saídas Recentes", "Últimos movimentos de despesa registados."),
                separator(),
                saidasLista
        );

        movimentosGrid.getChildren().addAll(entradasCard, saidasCard);

        root.getChildren().addAll(
                header,
                estado,
                stats,
                resumoCard,
                movimentosGrid
        );

        carregarBalanco(
                estado,
                receitasValor,
                despesasValor,
                saldoValor,
                margemValor,
                resumoLista,
                entradasLista,
                saidasLista
        );

        return wrap(root);
    }

    private void carregarBalanco(
            Label estado,
            Label receitasValor,
            Label despesasValor,
            Label saldoValor,
            Label margemValor,
            VBox resumoLista,
            VBox entradasLista,
            VBox saidasLista
    ) {
        estado.setText("A carregar balanço...");

        Task<BalancoData> task = new Task<>() {
            @Override
            protected BalancoData call() {
                ContaEmpresaDto conta = financeiroApiService.obterConta();
                List<MovimentoFinanceiroDto> movimentos = financeiroApiService.listarMovimentos();

                return new BalancoData(conta, movimentos);
            }
        };

        task.setOnSucceeded(event -> {
            BalancoData data = task.getValue();
            List<MovimentoFinanceiroDto> movimentos = data.movimentos();

            BigDecimal totalEntradas = movimentos.stream()
                    .filter(m -> "ENTRADA".equalsIgnoreCase(m.getTipo()))
                    .map(MovimentoFinanceiroDto::getValor)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalSaidas = movimentos.stream()
                    .filter(m -> "SAIDA".equalsIgnoreCase(m.getTipo()))
                    .map(MovimentoFinanceiroDto::getValor)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoAtual =
                    data.conta() != null && data.conta().getSaldoAtual() != null
                            ? data.conta().getSaldoAtual()
                            : BigDecimal.ZERO;

            BigDecimal margem = BigDecimal.ZERO;

            if (totalEntradas.compareTo(BigDecimal.ZERO) > 0) {
                margem = totalEntradas.subtract(totalSaidas)
                        .multiply(new BigDecimal("100"))
                        .divide(totalEntradas, 2, RoundingMode.HALF_UP);
            }

            receitasValor.setText(formatarMoeda(totalEntradas));
            despesasValor.setText(formatarMoeda(totalSaidas));
            saldoValor.setText(formatarMoeda(saldoAtual));
            margemValor.setText(margem.toPlainString() + "%");

            resumoLista.getChildren().clear();
            resumoLista.getChildren().addAll(
                    resumoRow("Total de movimentos", String.valueOf(movimentos.size()), "#dbeafe", "#2563eb"),
                    resumoRow("Entradas", formatarMoeda(totalEntradas), "#dcfce7", "#15803d"),
                    resumoRow("Saídas", formatarMoeda(totalSaidas), "#fee2e2", "#dc2626"),
                    resumoRow("Saldo atual", formatarMoeda(saldoAtual), "#f3e8ff", "#7c3aed")
            );

            entradasLista.getChildren().clear();

            List<MovimentoFinanceiroDto> entradas = movimentos.stream()
                    .filter(m -> "ENTRADA".equalsIgnoreCase(m.getTipo()))
                    .sorted(Comparator.comparing(MovimentoFinanceiroDto::getId).reversed())
                    .limit(10)
                    .toList();

            if (entradas.isEmpty()) {
                entradasLista.getChildren().add(emptyBox("Ainda não existem entradas registadas."));
            } else {
                for (MovimentoFinanceiroDto movimento : entradas) {
                    entradasLista.getChildren().add(movimentoRow(movimento, true));
                }
            }

            saidasLista.getChildren().clear();

            List<MovimentoFinanceiroDto> saidas = movimentos.stream()
                    .filter(m -> "SAIDA".equalsIgnoreCase(m.getTipo()))
                    .sorted(Comparator.comparing(MovimentoFinanceiroDto::getId).reversed())
                    .limit(10)
                    .toList();

            if (saidas.isEmpty()) {
                saidasLista.getChildren().add(emptyBox("Ainda não existem saídas registadas."));
            } else {
                for (MovimentoFinanceiroDto movimento : saidas) {
                    saidasLista.getChildren().add(movimentoRow(movimento, false));
                }
            }

            estado.setText("Balanço carregado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar balanço.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(
                    task.getException() == null
                            ? "Erro desconhecido."
                            : task.getException().getMessage()
            );
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox movimentoRow(MovimentoFinanceiroDto movimento, boolean entrada) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        StackPane icon = new StackPane();
        icon.setMinSize(52, 52);
        icon.setPrefSize(52, 52);

        icon.setStyle(
                "-fx-background-color: " + (entrada ? "#dcfce7" : "#fee2e2") + ";" +
                        "-fx-background-radius: 16;"
        );

        Label iconText = new Label(entrada ? "📈" : "📉");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        VBox main = new VBox(4);

        Label valor = new Label(formatarMoeda(movimento.getValor()));
        valor.setStyle(
                "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + (entrada ? "#15803d" : "#dc2626") + ";"
        );

        Label descricao = new Label(texto(movimento.getDescricao()));
        descricao.setWrapText(true);
        descricao.setStyle("-fx-font-size: 13; -fx-text-fill: #0f172a; -fx-font-weight: bold;");

        main.getChildren().addAll(valor, descricao);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(
                icon,
                main,
                spacer,
                infoBlock("Data", formatarData(movimento.getData())),
                infoBlock("Origem", texto(movimento.getOrigem()))
        );

        return row;
    }

    private HBox resumoRow(String title, String value, String bg, String fg) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = badge(value, bg, fg);

        row.getChildren().addAll(titleLabel, spacer, valueLabel);

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

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        text.getChildren().addAll(titleLabel, value, subtitleLabel);
        box.getChildren().addAll(icon, text);

        card.getChildren().add(box);

        return card;
    }

    private Label statNumber(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private HBox sectionHeader(String iconText, String title, String subtitle) {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(4);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        text.getChildren().addAll(titleLabel, subtitleLabel);
        box.getChildren().addAll(icon, text);

        return box;
    }

    private VBox card() {
        VBox card = new VBox(18);
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
        v.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

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

    private VBox emptyBox(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);

        return box;
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

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00 €";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String formatarData(String data) {
        if (data == null || data.isBlank()) {
            return "-";
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(data);
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return data;
        }
    }

    private record BalancoData(
            ContaEmpresaDto conta,
            List<MovimentoFinanceiroDto> movimentos
    ) {
    }
}