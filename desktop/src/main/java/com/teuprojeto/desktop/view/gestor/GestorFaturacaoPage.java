package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.FaturaDto;
import com.teuprojeto.desktop.service.FaturaApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class GestorFaturacaoPage {

    private final GestorShellView shell;
    private final FaturaApiService faturaApiService = new FaturaApiService();

    public GestorFaturacaoPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Consultar Faturação");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Resumo das faturas emitidas e valores faturados.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Label estado = new Label("A carregar faturação...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label faturacaoTotal = statNumber("-");
        Label ivaTotal = statNumber("-");
        Label faturacaoMedia = statNumber("-");

        stats.getChildren().addAll(
                statCard("Faturação Total", faturacaoTotal, "Total registado", "#16a34a", "€"),
                statCard("IVA Estimado", ivaTotal, "Estimativa a 23%", "#2563eb", "%"),
                statCard("Faturação Média", faturacaoMedia, "Por fatura", "#f97316", "📊")
        );

        VBox resumoCard = card();
        VBox resumoLista = new VBox(12);

        resumoCard.getChildren().addAll(
                sectionHeader("📈", "Resumo da Faturação", "Indicadores principais da faturação."),
                separator(),
                resumoLista
        );

        VBox listaCard = card();
        VBox listaFaturas = new VBox(14);

        listaCard.getChildren().addAll(
                sectionHeader("📄", "Faturas Recentes", "Últimas faturas registadas no sistema."),
                separator(),
                listaFaturas
        );

        root.getChildren().addAll(
                header,
                estado,
                stats,
                resumoCard,
                listaCard
        );

        carregarFaturas(
                estado,
                faturacaoTotal,
                ivaTotal,
                faturacaoMedia,
                resumoLista,
                listaFaturas
        );

        return wrap(root);
    }

    private void carregarFaturas(
            Label estado,
            Label faturacaoTotal,
            Label ivaTotal,
            Label faturacaoMedia,
            VBox resumoLista,
            VBox listaFaturas
    ) {
        estado.setText("A carregar faturação...");

        Task<List<FaturaDto>> task = new Task<>() {
            @Override
            protected List<FaturaDto> call() {
                return faturaApiService.listarFaturas();
            }
        };

        task.setOnSucceeded(event -> {
            List<FaturaDto> faturas = task.getValue();
            ObservableList<FaturaDto> lista = FXCollections.observableArrayList(faturas);

            BigDecimal total = lista.stream()
                    .map(FaturaDto::getValor)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal ivaEstimado = total.multiply(new BigDecimal("0.23"));

            BigDecimal media = lista.isEmpty()
                    ? BigDecimal.ZERO
                    : total.divide(
                    BigDecimal.valueOf(lista.size()),
                    2,
                    java.math.RoundingMode.HALF_UP
            );

            faturacaoTotal.setText(formatarMoeda(total));
            ivaTotal.setText(formatarMoeda(ivaEstimado));
            faturacaoMedia.setText(formatarMoeda(media));

            resumoLista.getChildren().clear();
            resumoLista.getChildren().addAll(
                    resumoRow("Total de faturas", String.valueOf(lista.size()), "#dbeafe", "#2563eb"),
                    resumoRow("Valor total faturado", formatarMoeda(total), "#dcfce7", "#15803d"),
                    resumoRow("IVA estimado", formatarMoeda(ivaEstimado), "#ffedd5", "#ea580c"),
                    resumoRow("Média por fatura", formatarMoeda(media), "#f3e8ff", "#7c3aed")
            );

            listaFaturas.getChildren().clear();

            if (lista.isEmpty()) {
                listaFaturas.getChildren().add(emptyBox("Ainda não existem faturas registadas."));
            } else {
                lista.stream()
                        .sorted(Comparator.comparing(FaturaDto::getId).reversed())
                        .limit(10)
                        .forEach(fatura -> listaFaturas.getChildren().add(faturaCard(fatura)));
            }

            estado.setText("Faturação carregada: " + lista.size() + " faturas.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar faturação.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox faturaCard(FaturaDto fatura) {
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
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 16;");

        Label iconText = new Label("📄");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        VBox main = new VBox(4);

        Label numero = new Label("FT-" + fatura.getId());
        numero.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label encomenda = new Label(formatarEncomenda(fatura.getIdEncomenda()));
        encomenda.setStyle("-fx-font-size: 13; -fx-text-fill: #2563eb; -fx-font-weight: bold;");

        main.getChildren().addAll(numero, encomenda);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(
                icon,
                main,
                spacer,
                infoBlock("Data", formatarData(fatura.getData())),
                infoBlock("Valor", formatarMoeda(fatura.getValor()))
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
        card.setPrefWidth(300);
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

    private String formatarEncomenda(BigDecimal idEncomenda) {
        if (idEncomenda == null) {
            return "-";
        }

        return "ENC-" + idEncomenda.stripTrailingZeros().toPlainString();
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
}