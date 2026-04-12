package com.teuprojeto.desktop.view.funcionario;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FuncionarioDashboardPage {

    private final FuncionarioShellView shell;

    public FuncionarioDashboardPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = FuncionarioUiFactory.createPageContainer("Dashboard");

        HBox stats = new HBox(18);

        VBox card1 = statCard("Total Atribuído", String.valueOf(MockFuncionarioData.totalAtribuido()), "Ordens de produção");
        VBox card2 = progressCard("Progresso Total", MockFuncionarioData.progressoMedioPercent());
        VBox card3 = statCard(
                "Unidades Produzidas",
                MockFuncionarioData.totalUnidadesConcluidas() + "/" + MockFuncionarioData.totalUnidades(),
                "Chapéus concluídos"
        );

        stats.getChildren().addAll(card1, card2, card3);

        VBox lista = FuncionarioUiFactory.createCard();
        lista.getChildren().add(sectionTitle("Ordens de Produção Atribuídas"));

        for (FuncionarioEncomendaRow encomenda : MockFuncionarioData.getEncomendas()) {
            lista.getChildren().add(buildOrdemCard(encomenda));
        }

        root.getChildren().addAll(stats, lista);
        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private VBox statCard(String titulo, String valor, String subtitulo) {
        VBox card = FuncionarioUiFactory.createCard();
        card.setPrefWidth(320);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private VBox progressCard(String titulo, int percentagem) {
        VBox card = FuncionarioUiFactory.createCard();
        card.setPrefWidth(320);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(percentagem + "%");
        l2.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        ProgressBar bar = new ProgressBar(percentagem / 100.0);
        bar.setPrefWidth(1000);
        bar.setStyle("-fx-accent: #111827;");

        card.getChildren().addAll(l1, l2, bar);
        return card;
    }

    private VBox buildOrdemCard(FuncionarioEncomendaRow encomenda) {
        VBox card = new VBox(12);
        card.setPadding(new javafx.geometry.Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label produto = new Label(encomenda.getProduto());
        produto.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label prioridade = badge(encomenda.getPrioridade(), "#fff7ed", "#ea580c");
        Label estado = badge(encomenda.isConcluida() ? "Concluído" : "Em produção",
                encomenda.isConcluida() ? "#dcfce7" : "#eff6ff",
                encomenda.isConcluida() ? "#15803d" : "#1d4ed8");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button detalhes = FuncionarioUiFactory.secondaryButton("Atualizar Progresso");
        detalhes.setOnAction(e -> {
            shell.setEncomendaSelecionada(encomenda);
            shell.navigateTo(FuncionarioPage.ATUALIZAR_PRODUCAO);
        });

        top.getChildren().addAll(produto, prioridade, estado, spacer, detalhes);

        Label ordem = new Label("Ordem: " + encomenda.getCodigoOrdem() + " | Encomenda: " + encomenda.getCodigoEncomenda());
        Label cliente = new Label("Cliente: " + encomenda.getCliente());

        Label progressoTexto = new Label("Progresso:");
        ProgressBar progressBar = new ProgressBar(encomenda.getProgresso());
        progressBar.setPrefWidth(1000);
        progressBar.setStyle("-fx-accent: #111827;");

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_LEFT);

        Label unidades = new Label(encomenda.getUnidadesConcluidas() + " / " + encomenda.getQuantidadeTotal() + " unidades");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label dataLimite = new Label(encomenda.getDataLimite());
        dataLimite.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");

        bottom.getChildren().addAll(unidades, spacer2, dataLimite);

        card.getChildren().addAll(top, ordem, cliente, progressoTexto, progressBar, bottom);
        return card;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private Label badge(String text, String bg, String fg) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-padding: 4 8 4 8;" +
                        "-fx-background-radius: 12;" +
                        "-fx-font-size: 11;"
        );
        return label;
    }
}