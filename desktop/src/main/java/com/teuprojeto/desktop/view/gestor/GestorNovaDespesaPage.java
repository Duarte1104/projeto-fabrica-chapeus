package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.CriarCompraMaterialRequestDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.CompraMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;

public class GestorNovaDespesaPage {

    private final GestorShellView shell;
    private final MaterialApiService materialApiService = new MaterialApiService();
    private final CompraMaterialApiService compraMaterialApiService = new CompraMaterialApiService();

    public GestorNovaDespesaPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Adicionar Despesa");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Registe uma nova compra de material e atualize os movimentos financeiros.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        VBox card = card();

        card.getChildren().addAll(
                sectionHeader("🧾", "Dados da Despesa", "Selecione o material comprado e indique a quantidade."),
                separator()
        );

        ComboBox<MaterialDto> produto = new ComboBox<>();
        produto.setMaxWidth(Double.MAX_VALUE);
        produto.setPromptText("Selecionar material");
        produto.setStyle(inputStyle());

        produto.setConverter(new StringConverter<>() {
            @Override
            public String toString(MaterialDto value) {
                if (value == null) {
                    return "";
                }

                return value.getNome()
                        + " ("
                        + formatarMoeda(value.getCustoUnitario())
                        + "/"
                        + (value.getUnidade() == null ? "un" : value.getUnidade())
                        + ")";
            }

            @Override
            public MaterialDto fromString(String string) {
                return null;
            }
        });

        TextField quantidade = input("Ex: 20");

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da despesa...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        VBox form = new VBox(14);
        form.getChildren().addAll(
                criarCampoBox("Material", produto),
                criarCampoBox("Quantidade", quantidade),
                criarCampoBox("Observações", observacoes)
        );

        Label estado = new Label("A carregar materiais...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        Button confirmar = GestorUiFactory.primaryButton("Confirmar Despesa");
        Button cancelar = GestorUiFactory.secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(GestorPage.DESPESAS));

        confirmar.setOnAction(e -> confirmarDespesa(
                produto,
                quantidade,
                observacoes,
                estado,
                confirmar,
                cancelar
        ));

        HBox buttons = new HBox(12, confirmar, cancelar);
        buttons.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(form, estado, buttons);

        root.getChildren().addAll(header, card);

        carregarMateriais(produto, estado);

        return wrap(root);
    }

    private void confirmarDespesa(ComboBox<MaterialDto> produto,
                                  TextField quantidade,
                                  TextArea observacoes,
                                  Label estado,
                                  Button confirmar,
                                  Button cancelar) {
        try {
            MaterialDto material = produto.getValue();

            if (material == null) {
                mostrarErro("Seleciona um material.");
                return;
            }

            if (quantidade.getText() == null || quantidade.getText().isBlank()) {
                mostrarErro("Indica a quantidade.");
                return;
            }

            BigDecimal qtd = new BigDecimal(quantidade.getText().trim());

            if (qtd.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarErro("A quantidade tem de ser maior que zero.");
                return;
            }

            CriarCompraMaterialRequestDto dto = new CriarCompraMaterialRequestDto();
            dto.setIdMaterial(material.getId());
            dto.setQuantidade(qtd);
            dto.setObservacoes(
                    observacoes.getText() == null || observacoes.getText().isBlank()
                            ? null
                            : observacoes.getText().trim()
            );

            confirmar.setDisable(true);
            cancelar.setDisable(true);
            estado.setText("A registar despesa...");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    compraMaterialApiService.criar(dto);
                    return null;
                }
            };

            task.setOnSucceeded(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Despesa registada");
                alert.setContentText("A compra de material foi registada com sucesso.");
                alert.showAndWait();

                shell.navigateTo(GestorPage.DESPESAS);
            });

            task.setOnFailed(event -> {
                confirmar.setDisable(false);
                cancelar.setDisable(false);
                estado.setText("Erro ao registar despesa.");
                mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            mostrarErro("Quantidade inválida.");
        }
    }

    private void carregarMateriais(ComboBox<MaterialDto> produto, Label estado) {
        estado.setText("A carregar materiais...");

        Task<List<MaterialDto>> task = new Task<>() {
            @Override
            protected List<MaterialDto> call() {
                return materialApiService.listarTodos();
            }
        };

        task.setOnSucceeded(event -> {
            produto.getItems().setAll(task.getValue());
            estado.setText("Materiais carregados.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar materiais.");
            mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox criarCampoBox(String labelText, Control control) {
        VBox box = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");

        control.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(label, control);

        return box;
    }

    private HBox sectionHeader(String iconText, String title, String subtitle) {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(4);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        text.getChildren().addAll(titleLabel, subtitleLabel);
        box.getChildren().addAll(icon, text);

        return box;
    }

    private VBox card() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setMaxWidth(720);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

        return card;
    }

    private TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private String inputStyle() {
        return "-fx-background-color: white;" +
                "-fx-border-color: #dbe2ea;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 11;" +
                "-fx-font-size: 14;";
    }

    private Region separator() {
        Region region = new Region();
        region.setPrefHeight(1);
        region.setStyle("-fx-background-color: #e5e7eb;");
        return region;
    }

    private Parent wrap(VBox root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");
        return scrollPane;
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00 €";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}