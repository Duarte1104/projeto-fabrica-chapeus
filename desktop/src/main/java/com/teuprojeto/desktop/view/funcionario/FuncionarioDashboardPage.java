package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.service.EncomendaApiService;
import com.teuprojeto.desktop.service.FuncionarioDataService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        VBox root = FuncionarioUiFactory.createPageContainer("Dashboard");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #666666;");

        HBox stats = new HBox(18);

        VBox disponiveisCard = statCard("Disponíveis", "-", "Prontas para aceitar");
        VBox minhasCard = statCard("Minhas Encomendas", "-", "Já atribuídas a mim");
        VBox concluidasCard = statCard("Histórico", "-", "Prontas ou pagas");

        stats.getChildren().addAll(disponiveisCard, minhasCard, concluidasCard);

        VBox disponiveisLista = FuncionarioUiFactory.createCard();
        disponiveisLista.getChildren().add(sectionTitle("Encomendas Disponíveis"));

        root.getChildren().addAll(estado, stats, disponiveisLista);

        carregarDados(estado, disponiveisCard, minhasCard, concluidasCard, disponiveisLista);

        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private void carregarDados(
            Label estado,
            VBox disponiveisCard,
            VBox minhasCard,
            VBox concluidasCard,
            VBox disponiveisLista
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

            atualizarStatCard(disponiveisCard, "Disponíveis", String.valueOf(disponiveis.size()), "Prontas para aceitar");
            atualizarStatCard(minhasCard, "Minhas Encomendas", String.valueOf(minhasAtivas.size()), "Já atribuídas a mim");
            atualizarStatCard(concluidasCard, "Histórico", String.valueOf(historico.size()), "Prontas ou pagas");

            disponiveisLista.getChildren().clear();
            disponiveisLista.getChildren().add(sectionTitle("Encomendas Disponíveis"));

            if (disponiveis.isEmpty()) {
                disponiveisLista.getChildren().add(new Label("Não existem encomendas disponíveis neste momento."));
            } else {
                for (FuncionarioEncomendaRow row : disponiveis) {
                    disponiveisLista.getChildren().add(buildDisponivelCard(
                            row,
                            estado,
                            disponiveisCard,
                            minhasCard,
                            concluidasCard,
                            disponiveisLista
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

    private void atualizarStatCard(VBox card, String titulo, String valor, String subtitulo) {
        card.getChildren().clear();

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
    }

    private VBox buildDisponivelCard(
            FuncionarioEncomendaRow encomenda,
            Label estado,
            VBox disponiveisCard,
            VBox minhasCard,
            VBox concluidasCard,
            VBox disponiveisLista
    ) {
        VBox card = baseCard(encomenda);

        Button aceitar = FuncionarioUiFactory.primaryButton("Aceitar Encomenda");
        aceitar.setOnAction(e -> aceitarEncomenda(
                encomenda,
                estado,
                disponiveisCard,
                minhasCard,
                concluidasCard,
                disponiveisLista
        ));

        card.getChildren().add(aceitar);
        return card;
    }

    private VBox baseCard(FuncionarioEncomendaRow encomenda) {
        VBox card = new VBox(12);
        card.setPadding(new javafx.geometry.Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label produto = new Label(encomenda.getProduto());
        produto.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label prioridade = badge(encomenda.getPrioridade(), "#fff7ed", "#ea580c");
        Label estadoBadge = badge(encomenda.getEstado(), "#eff6ff", "#1d4ed8");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(produto, prioridade, estadoBadge, spacer);

        Label encomendaInfo = new Label("Encomenda: " + encomenda.getCodigoEncomenda());
        Label cliente = new Label("Cliente: " + encomenda.getCliente());
        Label quantidade = new Label("Quantidade total: " + encomenda.getQuantidadeTotal());

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_LEFT);

        Label dataLimite = new Label("Data limite: " + encomenda.getDataLimite());
        dataLimite.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");

        bottom.getChildren().addAll(dataLimite);

        card.getChildren().addAll(top, encomendaInfo, cliente, quantidade, bottom);
        return card;
    }

    private void aceitarEncomenda(
            FuncionarioEncomendaRow encomenda,
            Label estado,
            VBox disponiveisCard,
            VBox minhasCard,
            VBox concluidasCard,
            VBox disponiveisLista
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

            carregarDados(estado, disponiveisCard, minhasCard, concluidasCard, disponiveisLista);
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