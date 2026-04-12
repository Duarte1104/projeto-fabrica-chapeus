package com.teuprojeto.desktop.view.funcionario;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FuncionarioMinhasEncomendasPage {

    private final FuncionarioShellView shell;

    public FuncionarioMinhasEncomendasPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = FuncionarioUiFactory.createPageContainer("Minhas Encomendas");

        TextField search = new TextField();
        search.setPromptText("Pesquisar encomenda...");
        search.setMaxWidth(380);

        VBox lista = FuncionarioUiFactory.createCard();
        lista.getChildren().addAll(new Label("Encomendas atribuídas ao funcionário"));

        for (FuncionarioEncomendaRow encomenda : MockFuncionarioData.getEncomendas()) {
            if (matches(search.getText(), encomenda)) {
                lista.getChildren().add(buildCard(encomenda));
            }
        }

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            lista.getChildren().clear();
            lista.getChildren().add(new Label("Encomendas atribuídas ao funcionário"));

            for (FuncionarioEncomendaRow encomenda : MockFuncionarioData.getEncomendas()) {
                if (matches(newValue, encomenda)) {
                    lista.getChildren().add(buildCard(encomenda));
                }
            }
        });

        root.getChildren().addAll(search, lista);
        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private boolean matches(String termo, FuncionarioEncomendaRow encomenda) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t = termo.toLowerCase().trim();

        return encomenda.getCodigoOrdem().toLowerCase().contains(t)
                || encomenda.getCodigoEncomenda().toLowerCase().contains(t)
                || encomenda.getProduto().toLowerCase().contains(t)
                || encomenda.getCliente().toLowerCase().contains(t);
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button atualizar = FuncionarioUiFactory.primaryButton("Atualizar Progresso");
        atualizar.setOnAction(e -> {
            shell.setEncomendaSelecionada(encomenda);
            shell.navigateTo(FuncionarioPage.ATUALIZAR_PRODUCAO);
        });

        top.getChildren().addAll(produto, prioridade, spacer, atualizar);

        Label ordem = new Label("Ordem: " + encomenda.getCodigoOrdem() + " | Encomenda: " + encomenda.getCodigoEncomenda());
        Label cliente = new Label("Cliente: " + encomenda.getCliente());

        Label progresso = new Label("Progresso:");
        ProgressBar bar = new ProgressBar(encomenda.getProgresso());
        bar.setPrefWidth(1000);
        bar.setStyle("-fx-accent: #111827;");

        HBox info = new HBox();
        info.setAlignment(Pos.CENTER_LEFT);

        Label unidades = new Label(encomenda.getUnidadesConcluidas() + " / " + encomenda.getQuantidadeTotal() + " unidades");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label prazo = new Label(encomenda.getDataLimite());
        prazo.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");

        info.getChildren().addAll(unidades, spacer2, prazo);

        card.getChildren().addAll(top, ordem, cliente, progresso, bar, info);
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