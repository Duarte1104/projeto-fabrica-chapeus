package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.DashboardGestorDto;
import com.teuprojeto.desktop.service.DashboardApiService;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class GestorDashboardPage {

    private final GestorShellView shell;
    private final DashboardApiService dashboardApiService = new DashboardApiService();

    public GestorDashboardPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Dashboard");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #666666;");

        HBox stats = new HBox(18);

        VBox cardSaldo = statCard("Saldo Atual", "-", "Conta da empresa");
        VBox cardEncomendas = statCard("Total Encomendas", "-", "Global");
        VBox cardMovimentos = statCard("Movimentos", "-", "Financeiros");
        VBox cardStockBaixo = statCard("Stock Baixo", "-", "Materiais");

        stats.getChildren().addAll(cardSaldo, cardEncomendas, cardMovimentos, cardStockBaixo);

        VBox resumo = GestorUiFactory.createCard();
        resumo.getChildren().addAll(
                sectionTitle("Resumo Operacional"),
                new Label("A carregar...")
        );

        root.getChildren().addAll(estado, stats, resumo);

        carregarDashboard(estado, cardSaldo, cardEncomendas, cardMovimentos, cardStockBaixo, resumo);

        return root;
    }

    private void carregarDashboard(Label estado,
                                   VBox cardSaldo,
                                   VBox cardEncomendas,
                                   VBox cardMovimentos,
                                   VBox cardStockBaixo,
                                   VBox resumo) {

        Task<DashboardGestorDto> task = new Task<>() {
            @Override
            protected DashboardGestorDto call() {
                return dashboardApiService.obterDashboardGestor();
            }
        };

        task.setOnSucceeded(event -> {
            DashboardGestorDto dto = task.getValue();

            atualizarStatCard(cardSaldo, "Saldo Atual", formatarMoeda(dto.getSaldoAtual()), "Conta da empresa");
            atualizarStatCard(cardEncomendas, "Total Encomendas", String.valueOf(dto.getTotalEncomendas()), "Global");
            atualizarStatCard(cardMovimentos, "Movimentos", String.valueOf(dto.getTotalMovimentos()), "Financeiros");
            atualizarStatCard(cardStockBaixo, "Stock Baixo", String.valueOf(dto.getMateriaisAbaixoMinimo()), "Materiais");

            resumo.getChildren().clear();
            resumo.getChildren().addAll(
                    sectionTitle("Resumo Operacional"),
                    new Label("Aguarda Design: " + dto.getAguardaDesign()),
                    new Label("Em Preparação: " + dto.getEmPreparacao()),
                    new Label("Prontas: " + dto.getProntas()),
                    new Label("Pagas: " + dto.getPagas()),
                    new Label("Saldo Atual: " + formatarMoeda(dto.getSaldoAtual()))
            );

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

    private VBox statCard(String titulo, String valor, String subtitulo) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(240);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12; -fx-font-weight: bold;");

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
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12; -fx-font-weight: bold;");

        card.getChildren().addAll(l1, l2, l3);
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0.00 €";
        }
        return String.format("%.2f €", valor.doubleValue());
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }
}