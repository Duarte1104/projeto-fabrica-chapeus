package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.service.FuncionarioDataService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioHistoricoPage {

    private final FuncionarioShellView shell;
    private final FuncionarioDataService funcionarioDataService =
            new FuncionarioDataService();

    public FuncionarioHistoricoPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {

        VBox root =
                FuncionarioUiFactory.createPageContainer("Histórico");

        Label estado =
                new Label("A carregar histórico...");

        estado.setStyle("-fx-text-fill: #666666;");

        VBox lista = FuncionarioUiFactory.createCard();

        lista.getChildren().add(
                new Label("Encomendas concluídas")
        );

        root.getChildren().addAll(estado, lista);

        Task<List<FuncionarioEncomendaRow>> task =
                new Task<>() {
                    @Override
                    protected List<FuncionarioEncomendaRow> call() {
                        return funcionarioDataService
                                .carregarMinhasEncomendas(
                                        shell.getFuncionarioId()
                                );
                    }
                };

        task.setOnSucceeded(event -> {

            List<FuncionarioEncomendaRow> historico =
                    new ArrayList<>();

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

            lista.getChildren().add(
                    new Label("Encomendas concluídas")
            );

            if (historico.isEmpty()) {

                lista.getChildren().add(
                        new Label("Ainda não existem encomendas concluídas.")
                );

            } else {

                for (FuncionarioEncomendaRow encomenda : historico) {
                    lista.getChildren().add(buildCard(encomenda));
                }
            }

            estado.setText(
                    "Histórico carregado: "
                            + historico.size()
                            + " encomendas."
            );
        });

        task.setOnFailed(event -> {

            estado.setText("Erro ao carregar histórico.");

            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

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

        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private VBox buildCard(FuncionarioEncomendaRow encomenda) {

        VBox card = new VBox(12);

        card.setPadding(new javafx.geometry.Insets(14));

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label produto =
                new Label(encomenda.getProduto());

        produto.setStyle(
                "-fx-font-size: 20;" +
                        "-fx-font-weight: bold;"
        );

        Label estado =
                badge(
                        encomenda.getEstado(),
                        "#dcfce7",
                        "#15803d"
                );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(
                produto,
                estado,
                spacer
        );

        Label encomendaLabel =
                new Label("Encomenda: "
                        + encomenda.getCodigoEncomenda());

        Label cliente =
                new Label("Cliente: "
                        + encomenda.getCliente());

        Label quantidade =
                new Label("Quantidade total: "
                        + encomenda.getQuantidadeTotal());

        Label data =
                new Label("Data limite: "
                        + encomenda.getDataLimite());

        data.setStyle(
                "-fx-text-fill: #64748b;" +
                        "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                top,
                encomendaLabel,
                cliente,
                quantidade,
                data
        );

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