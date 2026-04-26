package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.ContaEmpresaDto;
import com.teuprojeto.desktop.dto.MovimentoFinanceiroDto;
import com.teuprojeto.desktop.service.FinanceiroApiService;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        VBox root = GestorUiFactory.createPageContainer("Balanço Financeiro");

        Label estado = new Label("A carregar balanço...");
        estado.setStyle("-fx-text-fill: #666666;");

        HBox stats = new HBox(18);
        VBox receitasCard = statCard("Receitas Totais", "-", "Entradas registadas", "#16a34a");
        VBox despesasCard = statCard("Despesas Totais", "-", "Saídas registadas", "#dc2626");
        VBox saldoCard = statCard("Saldo Atual", "-", "Conta da empresa", "#2563eb");
        VBox margemCard = statCard("Margem", "-", "Receitas vs despesas", "#16a34a");
        stats.getChildren().addAll(receitasCard, despesasCard, saldoCard, margemCard);

        VBox resumoCard = GestorUiFactory.createCard();
        resumoCard.getChildren().addAll(
                title("Resumo Financeiro"),
                new Label("A carregar...")
        );

        VBox entradasCard = GestorUiFactory.createCard();
        entradasCard.getChildren().add(title("Entradas Recentes"));

        VBox saidasCard = GestorUiFactory.createCard();
        saidasCard.getChildren().add(title("Saídas Recentes"));

        root.getChildren().addAll(estado, stats, resumoCard, entradasCard, saidasCard);

        carregarBalanco(estado, receitasCard, despesasCard, saldoCard, margemCard, resumoCard, entradasCard, saidasCard);

        return root;
    }

    private void carregarBalanco(Label estado,
                                 VBox receitasCard,
                                 VBox despesasCard,
                                 VBox saldoCard,
                                 VBox margemCard,
                                 VBox resumoCard,
                                 VBox entradasCard,
                                 VBox saidasCard) {

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

            BigDecimal saldoAtual = data.conta() != null && data.conta().getSaldoAtual() != null
                    ? data.conta().getSaldoAtual()
                    : BigDecimal.ZERO;

            BigDecimal margem = BigDecimal.ZERO;
            if (totalEntradas.compareTo(BigDecimal.ZERO) > 0) {
                margem = totalEntradas.subtract(totalSaidas)
                        .multiply(new BigDecimal("100"))
                        .divide(totalEntradas, 2, RoundingMode.HALF_UP);
            }

            atualizarStatCard(receitasCard, "Receitas Totais", formatarMoeda(totalEntradas), "Entradas registadas", "#16a34a");
            atualizarStatCard(despesasCard, "Despesas Totais", formatarMoeda(totalSaidas), "Saídas registadas", "#dc2626");
            atualizarStatCard(saldoCard, "Saldo Atual", formatarMoeda(saldoAtual), "Conta da empresa", "#2563eb");
            atualizarStatCard(margemCard, "Margem", margem.toPlainString() + "%", "Receitas vs despesas", "#16a34a");

            resumoCard.getChildren().clear();
            resumoCard.getChildren().addAll(
                    title("Resumo Financeiro"),
                    new Label("Total de movimentos: " + movimentos.size()),
                    new Label("Entradas: " + formatarMoeda(totalEntradas)),
                    new Label("Saídas: " + formatarMoeda(totalSaidas)),
                    new Label("Saldo atual: " + formatarMoeda(saldoAtual))
            );

            entradasCard.getChildren().clear();
            entradasCard.getChildren().add(title("Entradas Recentes"));

            List<MovimentoFinanceiroDto> entradas = movimentos.stream()
                    .filter(m -> "ENTRADA".equalsIgnoreCase(m.getTipo()))
                    .sorted(Comparator.comparing(MovimentoFinanceiroDto::getId).reversed())
                    .limit(10)
                    .toList();

            if (entradas.isEmpty()) {
                entradasCard.getChildren().add(new Label("Ainda não existem entradas registadas."));
            } else {
                for (MovimentoFinanceiroDto m : entradas) {
                    entradasCard.getChildren().add(new Label(
                            formatarData(m.getData())
                                    + " | "
                                    + formatarMoeda(m.getValor())
                                    + " | "
                                    + texto(m.getDescricao())
                                    + " | Origem: " + texto(m.getOrigem())
                    ));
                }
            }

            saidasCard.getChildren().clear();
            saidasCard.getChildren().add(title("Saídas Recentes"));

            List<MovimentoFinanceiroDto> saidas = movimentos.stream()
                    .filter(m -> "SAIDA".equalsIgnoreCase(m.getTipo()))
                    .sorted(Comparator.comparing(MovimentoFinanceiroDto::getId).reversed())
                    .limit(10)
                    .toList();

            if (saidas.isEmpty()) {
                saidasCard.getChildren().add(new Label("Ainda não existem saídas registadas."));
            } else {
                for (MovimentoFinanceiroDto m : saidas) {
                    saidasCard.getChildren().add(new Label(
                            formatarData(m.getData())
                                    + " | "
                                    + formatarMoeda(m.getValor())
                                    + " | "
                                    + texto(m.getDescricao())
                                    + " | Origem: " + texto(m.getOrigem())
                    ));
                }
            }

            estado.setText("Balanço carregado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar balanço.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox statCard(String titulo, String valor, String subtitulo, String corValor) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(250);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: " + corValor + ";");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private void atualizarStatCard(VBox card, String titulo, String valor, String subtitulo, String corValor) {
        card.getChildren().clear();

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: " + corValor + ";");

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

    private String texto(String valor) {
        return (valor == null || valor.isBlank()) ? "-" : valor;
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

    private record BalancoData(ContaEmpresaDto conta, List<MovimentoFinanceiroDto> movimentos) {
    }
}