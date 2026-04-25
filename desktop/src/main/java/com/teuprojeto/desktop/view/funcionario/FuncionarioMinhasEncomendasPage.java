package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.service.FuncionarioDataService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioMinhasEncomendasPage {

    private final FuncionarioShellView shell;
    private final FuncionarioDataService funcionarioDataService = new FuncionarioDataService();

    public FuncionarioMinhasEncomendasPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = FuncionarioUiFactory.createPageContainer("Minhas Encomendas");

        TextField search = new TextField();
        search.setPromptText("Pesquisar encomenda...");
        search.setMaxWidth(380);

        Label estado = new Label("A carregar encomendas...");
        estado.setStyle("-fx-text-fill: #666666;");

        VBox lista = FuncionarioUiFactory.createCard();
        lista.getChildren().addAll(new Label("Encomendas atribuídas ao funcionário"));

        List<FuncionarioEncomendaRow> cache = new ArrayList<>();

        search.textProperty().addListener((obs, oldValue, newValue) -> atualizarLista(lista, cache, newValue));

        root.getChildren().addAll(search, estado, lista);

        Task<List<FuncionarioEncomendaRow>> task = new Task<>() {
            @Override
            protected List<FuncionarioEncomendaRow> call() {
                return funcionarioDataService.carregarMinhasEncomendas(shell.getFuncionarioId());
            }
        };

        task.setOnSucceeded(event -> {
            cache.clear();
            cache.addAll(task.getValue());
            atualizarLista(lista, cache, search.getText());
            estado.setText("Encomendas carregadas: " + cache.size());
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar encomendas.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private void atualizarLista(VBox lista, List<FuncionarioEncomendaRow> encomendas, String termo) {
        lista.getChildren().clear();
        lista.getChildren().add(new Label("Encomendas atribuídas ao funcionário"));

        List<FuncionarioEncomendaRow> filtradas = encomendas.stream()
                .filter(e -> matches(termo, e))
                .toList();

        if (filtradas.isEmpty()) {
            lista.getChildren().add(new Label("Nenhuma encomenda encontrada."));
            return;
        }

        for (FuncionarioEncomendaRow encomenda : filtradas) {
            lista.getChildren().add(buildCard(encomenda));
        }
    }

    private boolean matches(String termo, FuncionarioEncomendaRow encomenda) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return encomenda.getCodigoEncomenda().toLowerCase().contains(t)
                || encomenda.getProduto().toLowerCase().contains(t)
                || encomenda.getCliente().toLowerCase().contains(t)
                || encomenda.getEstado().toLowerCase().contains(t);
    }

    private VBox buildCard(FuncionarioEncomendaRow encomenda) {
        VBox card = new VBox(12);
        card.setPadding(new javafx.geometry.Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label produto = new Label(encomenda.getProduto());
        produto.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        Label prioridade = badge(encomenda.getPrioridade(), "#fff7ed", "#ea580c");
        Label estado = badge(encomenda.getEstado(),
                encomenda.isConcluida() ? "#dcfce7" : "#eff6ff",
                encomenda.isConcluida() ? "#15803d" : "#1d4ed8");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button atualizar = FuncionarioUiFactory.primaryButton("Atualizar Progresso");
        atualizar.setOnAction(e -> {
            shell.setEncomendaSelecionada(encomenda);
            shell.navigateTo(FuncionarioPage.ATUALIZAR_PRODUCAO);
        });

        Button materiais = FuncionarioUiFactory.secondaryButton("Gastos Material");
        materiais.setOnAction(e -> {
            shell.setEncomendaSelecionada(encomenda);
            shell.navigateTo(FuncionarioPage.GASTOS_MATERIAL);
        });

        top.getChildren().addAll(produto, prioridade, estado, spacer, atualizar, materiais);

        Label encomendaLabel = new Label("Encomenda: " + encomenda.getCodigoEncomenda());
        Label cliente = new Label("Cliente: " + encomenda.getCliente());
        Label quantidade = new Label("Quantidade total: " + encomenda.getQuantidadeTotal());

        Label progresso = new Label("Progresso: " + encomenda.getResumoEtapas());
        ProgressBar bar = new ProgressBar(encomenda.getProgresso());
        bar.setPrefWidth(1000);
        bar.setStyle("-fx-accent: #111827;");

        HBox info = new HBox();
        info.setAlignment(Pos.CENTER_LEFT);

        Label prazo = new Label("Data limite: " + encomenda.getDataLimite());
        prazo.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");

        info.getChildren().addAll(prazo);

        card.getChildren().addAll(top, encomendaLabel, cliente, quantidade, progresso, bar, info);
        return card;
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