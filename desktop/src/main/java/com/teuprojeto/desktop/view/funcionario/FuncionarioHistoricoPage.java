package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.service.FuncionarioDataService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioHistoricoPage {

    private final FuncionarioShellView shell;
    private final FuncionarioDataService funcionarioDataService = new FuncionarioDataService();

    public FuncionarioHistoricoPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Histórico");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Encomendas prontas ou pagas atribuídas ao funcionário.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Label estado = new Label("A carregar histórico...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        root.getChildren().addAll(header, estado, lista);

        Task<List<FuncionarioEncomendaRow>> task = new Task<>() {
            @Override
            protected List<FuncionarioEncomendaRow> call() {
                return funcionarioDataService.carregarMinhasEncomendas(shell.getFuncionarioId());
            }
        };

        task.setOnSucceeded(event -> {
            List<FuncionarioEncomendaRow> historico = new ArrayList<>();

            for (FuncionarioEncomendaRow row : task.getValue()) {
                if (row.getEstado() == null) {
                    continue;
                }

                if (row.getEstado().equalsIgnoreCase("Pronta")
                        || row.getEstado().equalsIgnoreCase("Paga")) {
                    historico.add(row);
                }
            }

            lista.getChildren().clear();

            if (historico.isEmpty()) {
                lista.getChildren().add(emptyCard("Ainda não existem encomendas concluídas."));
            } else {
                for (FuncionarioEncomendaRow encomenda : historico) {
                    lista.getChildren().add(buildCard(encomenda));
                }
            }

            estado.setText("Histórico carregado: " + historico.size() + " encomendas.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar histórico.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private VBox buildCard(FuncionarioEncomendaRow encomenda) {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #dcfce7; -fx-background-radius: 18;");

        Label iconText = new Label("✅");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label produto = new Label(encomenda.getProduto());
        produto.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label codigo = new Label(encomenda.getCodigoEncomenda());
        codigo.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(produto, codigo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label estado = badge(
                encomenda.getEstado(),
                encomenda.getEstado().equalsIgnoreCase("Paga") ? "#dbeafe" : "#dcfce7",
                encomenda.getEstado().equalsIgnoreCase("Paga") ? "#1d4ed8" : "#15803d"
        );

        top.getChildren().addAll(icon, left, spacer, estado);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Cliente", encomenda.getCliente()),
                infoBlock("Quantidade", String.valueOf(encomenda.getQuantidadeTotal())),
                infoBlock("Data limite", encomenda.getDataLimite())
        );

        Label finalizada = new Label("Esta encomenda já saiu da lista de produção ativa.");
        finalizada.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        card.getChildren().addAll(top, infoGrid, finalizada);

        return card;
    }

    private VBox infoBlock(String title, String value) {
        VBox box = new VBox(4);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label v = new Label(value == null ? "-" : value);
        v.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

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

    private VBox emptyCard(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(22));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 18;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);
        return box;
    }
}