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

public class FuncionarioMinhasEncomendasPage {

    private final FuncionarioShellView shell;
    private final FuncionarioDataService funcionarioDataService =
            new FuncionarioDataService();

    public FuncionarioMinhasEncomendasPage(
            FuncionarioShellView shell
    ) {
        this.shell = shell;
    }

    public Parent getView() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Minhas Encomendas");
        title.setStyle(
                "-fx-font-size: 30;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Encomendas atualmente atribuídas ao funcionário."
        );

        subtitle.setStyle(
                "-fx-font-size: 14;" +
                        "-fx-text-fill: #64748b;"
        );

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Pesquisar encomenda...");
        search.setPrefWidth(380);

        search.setStyle(
                "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-padding: 12;" +
                        "-fx-background-color: white;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button atualizar =
                outlineButton("Atualizar");

        topBar.getChildren().addAll(
                search,
                spacer,
                atualizar
        );

        Label estado = new Label("A carregar encomendas...");

        estado.setStyle(
                "-fx-text-fill: #64748b;" +
                        "-fx-font-weight: bold;"
        );

        VBox lista = new VBox(16);

        List<FuncionarioEncomendaRow> cache =
                new ArrayList<>();

        search.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        atualizarLista(lista, cache, newValue)
        );

        root.getChildren().addAll(
                header,
                topBar,
                estado,
                lista
        );

        Runnable carregar = () -> {

            estado.setText("A carregar encomendas...");

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

                cache.clear();

                cache.addAll(
                        task.getValue().stream()
                                .filter(e ->
                                        e.getEstado() != null &&
                                                !e.getEstado().equalsIgnoreCase("Pronta") &&
                                                !e.getEstado().equalsIgnoreCase("Paga"))
                                .toList()
                );

                atualizarLista(
                        lista,
                        cache,
                        search.getText()
                );

                estado.setText(
                        "Encomendas carregadas: "
                                + cache.size()
                );
            });

            task.setOnFailed(event -> {

                estado.setText(
                        "Erro ao carregar encomendas."
                );

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
        };

        atualizar.setOnAction(e -> carregar.run());

        carregar.run();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background: #f4f7fb;" +
                        "-fx-background-color: #f4f7fb;"
        );

        return scrollPane;
    }

    private void atualizarLista(
            VBox lista,
            List<FuncionarioEncomendaRow> encomendas,
            String termo
    ) {

        lista.getChildren().clear();

        List<FuncionarioEncomendaRow> filtradas =
                encomendas.stream()
                        .filter(e -> matches(termo, e))
                        .toList();

        if (filtradas.isEmpty()) {

            lista.getChildren().add(
                    emptyCard(
                            "Nenhuma encomenda encontrada."
                    )
            );

            return;
        }

        for (FuncionarioEncomendaRow encomenda :
                filtradas) {

            lista.getChildren().add(
                    buildCard(encomenda)
            );
        }
    }

    private boolean matches(
            String termo,
            FuncionarioEncomendaRow encomenda
    ) {

        if (termo == null || termo.isBlank()) {
            return true;
        }

        String t =
                termo.toLowerCase().trim();

        return encomenda.getCodigoEncomenda()
                .toLowerCase()
                .contains(t)

                || encomenda.getProduto()
                .toLowerCase()
                .contains(t)

                || encomenda.getCliente()
                .toLowerCase()
                .contains(t)

                || encomenda.getEstado()
                .toLowerCase()
                .contains(t);
    }

    private VBox buildCard(
            FuncionarioEncomendaRow encomenda
    ) {

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

        icon.setStyle(
                "-fx-background-color: #eff6ff;" +
                        "-fx-background-radius: 18;"
        );

        Label iconText = new Label("🎩");
        iconText.setStyle("-fx-font-size: 24;");

        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label produto =
                new Label(encomenda.getProduto());

        produto.setStyle(
                "-fx-font-size: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;"
        );

        Label codigo =
                new Label(
                        encomenda.getCodigoEncomenda()
                );

        codigo.setStyle(
                "-fx-text-fill: #2563eb;" +
                        "-fx-font-weight: bold;"
        );

        left.getChildren().addAll(
                produto,
                codigo
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label prioridade =
                badge(
                        encomenda.getPrioridade(),
                        "#fff7ed",
                        "#ea580c"
                );

        Label estado =
                badge(
                        encomenda.getEstado(),
                        "#dbeafe",
                        "#1d4ed8"
                );

        Button atualizar =
                FuncionarioUiFactory
                        .primaryButton(
                                "Atualizar Produção"
                        );

        atualizar.setOnAction(e -> {

            shell.setEncomendaSelecionada(
                    encomenda
            );

            shell.navigateTo(
                    FuncionarioPage
                            .ATUALIZAR_PRODUCAO
            );
        });

        top.getChildren().addAll(
                icon,
                left,
                spacer,
                prioridade,
                estado,
                atualizar
        );

        HBox infoGrid = new HBox(26);

        infoGrid.getChildren().addAll(
                infoBlock(
                        "Cliente",
                        encomenda.getCliente()
                ),

                infoBlock(
                        "Quantidade",
                        String.valueOf(
                                encomenda
                                        .getQuantidadeTotal()
                        )
                ),

                infoBlock(
                        "Data limite",
                        encomenda.getDataLimite()
                )
        );

        VBox progressoBox =
                new VBox(10);

        Label progresso =
                new Label(
                        "Progresso: "
                                + encomenda
                                .getResumoEtapas()
                );

        progresso.setStyle(
                "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        ProgressBar bar =
                new ProgressBar(
                        encomenda.getProgresso()
                );

        bar.setPrefWidth(1200);

        bar.setStyle(
                "-fx-accent: #2563eb;"
        );

        progressoBox.getChildren().addAll(
                progresso,
                bar
        );

        Label aviso =
                new Label(
                        "Materiais descontados automaticamente no momento da criação da encomenda."
                );

        aviso.setStyle(
                "-fx-font-size: 12;" +
                        "-fx-text-fill: #64748b;"
        );

        card.getChildren().addAll(
                top,
                infoGrid,
                progressoBox,
                aviso
        );

        return card;
    }

    private VBox infoBlock(
            String title,
            String value
    ) {

        VBox box = new VBox(4);

        Label t = new Label(title);

        t.setStyle(
                "-fx-font-size: 12;" +
                        "-fx-text-fill: #64748b;"
        );

        Label v = new Label(value);

        v.setStyle(
                "-fx-font-size: 15;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;"
        );

        box.getChildren().addAll(t, v);

        return box;
    }

    private Label badge(
            String text,
            String bg,
            String fg
    ) {

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

        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;"
        );

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                        "-fx-font-weight: bold;"
        );

        box.getChildren().add(label);

        return box;
    }

    private Button outlineButton(
            String text
    ) {

        Button button = new Button(text);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;" +
                        "-fx-padding: 0 18 0 18;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }
}