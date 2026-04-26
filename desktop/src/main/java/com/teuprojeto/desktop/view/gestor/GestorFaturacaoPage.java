package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.FaturaDto;
import com.teuprojeto.desktop.service.FaturaApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        VBox root = GestorUiFactory.createPageContainer("Consultar Faturação");

        Label estado = new Label("A carregar faturação...");
        estado.setStyle("-fx-text-fill: #666666;");

        HBox stats = new HBox(18);
        VBox faturacaoTotalCard = statCard("Faturação Total", "-", "Total registado");
        VBox ivaTotalCard = statCard("IVA Estimado", "-", "Estimativa a 23%");
        VBox faturacaoMediaCard = statCard("Faturação Média", "-", "Por fatura");
        stats.getChildren().addAll(faturacaoTotalCard, ivaTotalCard, faturacaoMediaCard);

        VBox resumoCard = GestorUiFactory.createCard();
        resumoCard.getChildren().addAll(
                title("Resumo da Faturação"),
                new Label("A carregar...")
        );

        VBox listaCard = GestorUiFactory.createCard();
        listaCard.getChildren().add(title("Faturas Recentes"));

        root.getChildren().addAll(estado, stats, resumoCard, listaCard);

        carregarFaturas(estado, faturacaoTotalCard, ivaTotalCard, faturacaoMediaCard, resumoCard, listaCard);

        return root;
    }

    private void carregarFaturas(Label estado,
                                 VBox faturacaoTotalCard,
                                 VBox ivaTotalCard,
                                 VBox faturacaoMediaCard,
                                 VBox resumoCard,
                                 VBox listaCard) {

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
                    : total.divide(BigDecimal.valueOf(lista.size()), 2, java.math.RoundingMode.HALF_UP);

            atualizarStatCard(faturacaoTotalCard, "Faturação Total", formatarMoeda(total), "Total registado");
            atualizarStatCard(ivaTotalCard, "IVA Estimado", formatarMoeda(ivaEstimado), "Estimativa a 23%");
            atualizarStatCard(faturacaoMediaCard, "Faturação Média", formatarMoeda(media), "Por fatura");

            resumoCard.getChildren().clear();
            resumoCard.getChildren().addAll(
                    title("Resumo da Faturação"),
                    new Label("Total de faturas: " + lista.size()),
                    new Label("Valor total faturado: " + formatarMoeda(total)),
                    new Label("IVA estimado: " + formatarMoeda(ivaEstimado)),
                    new Label("Média por fatura: " + formatarMoeda(media))
            );

            listaCard.getChildren().clear();
            listaCard.getChildren().add(title("Faturas Recentes"));

            if (lista.isEmpty()) {
                listaCard.getChildren().add(new Label("Ainda não existem faturas registadas."));
            } else {
                lista.stream()
                        .sorted(Comparator.comparing(FaturaDto::getId).reversed())
                        .limit(10)
                        .forEach(fatura -> {
                            String linha =
                                    "FT-" + fatura.getId()
                                            + " | Encomenda " + formatarEncomenda(fatura.getIdEncomenda())
                                            + " | " + formatarData(fatura.getData())
                                            + " | " + formatarMoeda(fatura.getValor());

                            if (fatura.getObservacoes() != null && !fatura.getObservacoes().isBlank()) {
                                linha += " | " + fatura.getObservacoes();
                            }

                            listaCard.getChildren().add(new Label(linha));
                        });
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

    private VBox statCard(String titulo, String valor, String subtitulo) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(300);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private void atualizarStatCard(VBox card, String titulo, String valor, String subtitulo) {
        card.getChildren().clear();

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
    }

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0.00 €";
        }
        return String.format("%.2f €", valor.doubleValue());
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