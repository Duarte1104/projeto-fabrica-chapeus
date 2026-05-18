package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.CriarFaturaRequestDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.FaturaDto;
import com.teuprojeto.desktop.service.FaturaApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RececionistaFaturasPage {

    private final RececionistaShellView shell;
    private final FaturaApiService faturaApiService = new FaturaApiService();

    private final ObservableList<FaturaRow> tableData = FXCollections.observableArrayList();
    private List<EncomendaDto> encomendasFaturaveis = List.of();

    public RececionistaFaturasPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Faturas");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte faturas emitidas e crie faturas para encomendas prontas.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button criarFaturaBtn = RececionistaUiFactory.primaryButton("Criar Fatura");
        Button atualizarBtn = outlineButton("Atualizar");

        topBar.getChildren().addAll(spacer, criarFaturaBtn, atualizarBtn);

        Label statusLabel = new Label("A carregar faturas...");
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox lista = new VBox(16);

        criarFaturaBtn.setOnAction(e -> abrirDialogCriarFatura(statusLabel, lista));
        atualizarBtn.setOnAction(e -> carregarDados(statusLabel, lista));

        root.getChildren().addAll(header, topBar, statusLabel, lista);

        carregarDados(statusLabel, lista);

        return wrap(root);
    }

    private void carregarDados(Label statusLabel, VBox lista) {
        statusLabel.setText("A carregar faturas...");

        Task<DadosFaturasPage> task = new Task<>() {
            @Override
            protected DadosFaturasPage call() {
                List<FaturaDto> faturas = faturaApiService.listarFaturas();
                List<EncomendaDto> encomendas = faturaApiService.listarEncomendas();

                return new DadosFaturasPage(faturas, encomendas);
            }
        };

        task.setOnSucceeded(event -> {
            DadosFaturasPage dados = task.getValue();

            tableData.setAll(
                    dados.faturas().stream()
                            .map(this::toRow)
                            .toList()
            );

            encomendasFaturaveis =
                    filtrarEncomendasFaturaveis(
                            dados.encomendas(),
                            dados.faturas()
                    );

            lista.getChildren().clear();

            if (tableData.isEmpty()) {
                lista.getChildren().add(emptyCard("Ainda não existem faturas emitidas."));
            } else {
                for (FaturaRow row : tableData) {
                    lista.getChildren().add(buildFaturaCard(row));
                }
            }

            statusLabel.setText(
                    "Faturas carregadas: " + tableData.size()
                            + " | Encomendas faturáveis: "
                            + encomendasFaturaveis.size()
            );
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar faturas.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter faturas");
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
    }

    private List<EncomendaDto> filtrarEncomendasFaturaveis(
            List<EncomendaDto> encomendas,
            List<FaturaDto> faturas
    ) {
        Set<BigDecimal> idsJaFaturados = new HashSet<>();

        for (FaturaDto fatura : faturas) {
            if (fatura.getIdEncomenda() != null) {
                idsJaFaturados.add(fatura.getIdEncomenda());
            }
        }

        return encomendas.stream()
                .filter(encomenda -> encomenda.getNum() != null)
                .filter(encomenda -> Long.valueOf(3L).equals(encomenda.getIdestado()))
                .filter(encomenda ->
                        !idsJaFaturados.contains(
                                BigDecimal.valueOf(encomenda.getNum())
                        ))
                .toList();
    }

    private VBox buildFaturaCard(FaturaRow row) {
        VBox card = card();

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("📄");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox left = new VBox(4);

        Label numero = new Label(row.numeroProperty().get());
        numero.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label encomenda = new Label(row.encomendaProperty().get());
        encomenda.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(numero, encomenda);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valor = badge(row.valorProperty().get(), "#dcfce7", "#15803d");

        top.getChildren().addAll(icon, left, spacer, valor);

        HBox infoGrid = new HBox(26);
        infoGrid.getChildren().addAll(
                infoBlock("Fatura", row.numeroProperty().get()),
                infoBlock("Encomenda", row.encomendaProperty().get()),
                infoBlock("Valor", row.valorProperty().get()),
                infoBlock("Data", row.dataProperty().get())
        );

        card.getChildren().addAll(top, infoGrid);

        return card;
    }

    private void abrirDialogCriarFatura(Label statusLabel, VBox lista) {
        if (encomendasFaturaveis == null || encomendasFaturaveis.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sem encomendas faturáveis");
            alert.setContentText("Não existem encomendas prontas sem fatura.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Criar Fatura");

        ButtonType guardarType =
                new ButtonType(
                        "Guardar",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(guardarType, ButtonType.CANCEL);

        ComboBox<EncomendaDto> encomendaBox = new ComboBox<>();
        encomendaBox.getItems().addAll(encomendasFaturaveis);
        encomendaBox.setMaxWidth(Double.MAX_VALUE);
        encomendaBox.setStyle(inputStyle());

        encomendaBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(EncomendaDto encomenda) {
                if (encomenda == null || encomenda.getNum() == null) {
                    return "";
                }

                return "ENC-" + encomenda.getNum()
                        + " | Entrega: "
                        + valorOuTraco(encomenda.getDataEntrega())
                        + " | Valor: "
                        + formatarValor(encomenda.getValortotal());
            }

            @Override
            public EncomendaDto fromString(String string) {
                return null;
            }
        });

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da fatura...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        VBox content = new VBox(12);
        content.setPadding(new Insets(12));

        Label title = new Label("Nova Fatura");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Selecione uma encomenda pronta ainda sem fatura.");
        subtitle.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        content.getChildren().addAll(
                title,
                subtitle,
                separator(),
                fieldLabel("Encomenda"),
                encomendaBox,
                fieldLabel("Observações"),
                observacoes
        );

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(buttonType -> buttonType);

        dialog.showAndWait().ifPresent(result -> {
            if (result == guardarType) {
                if (encomendaBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Dados inválidos");
                    alert.setContentText("Seleciona uma encomenda.");
                    alert.showAndWait();
                    return;
                }

                criarFatura(
                        encomendaBox.getValue(),
                        observacoes.getText(),
                        statusLabel,
                        lista
                );
            }
        });
    }

    private void criarFatura(
            EncomendaDto encomenda,
            String observacoes,
            Label statusLabel,
            VBox lista
    ) {
        statusLabel.setText("A criar fatura...");

        CriarFaturaRequestDto dto = new CriarFaturaRequestDto();
        dto.setIdEncomenda(BigDecimal.valueOf(encomenda.getNum()));
        dto.setObservacoes(isBlank(observacoes) ? null : observacoes.trim());

        Task<FaturaDto> task = new Task<>() {
            @Override
            protected FaturaDto call() {
                return faturaApiService.criarFatura(dto);
            }
        };

        task.setOnSucceeded(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Fatura criada com sucesso");
            alert.setContentText(
                    "A fatura foi criada para a encomenda ENC-"
                            + encomenda.getNum()
                            + "."
            );
            alert.showAndWait();

            carregarDados(statusLabel, lista);
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao criar fatura.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao criar fatura");
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
    }

    private FaturaRow toRow(FaturaDto dto) {
        return new FaturaRow(
                formatarNumeroFatura(dto.getId()),
                formatarNumeroEncomenda(dto.getIdEncomenda()),
                formatarValor(dto.getValor()),
                formatarData(dto.getData())
        );
    }

    private String formatarNumeroFatura(Long id) {
        if (id == null) {
            return "FT-?";
        }

        return String.format("FT-%03d", id);
    }

    private String formatarNumeroEncomenda(BigDecimal idEncomenda) {
        if (idEncomenda == null) {
            return "ENC-?";
        }

        try {
            return "ENC-" + idEncomenda.toBigIntegerExact();
        } catch (ArithmeticException e) {
            return "ENC-" + idEncomenda.toPlainString();
        }
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "-";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private String formatarData(String data) {
        if (isBlank(data)) {
            return "-";
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(data);
            return dateTime.format(
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm"
                    )
            );
        } catch (Exception ignored) {
            return data;
        }
    }

    private VBox card() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

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

    private Button outlineButton(String text) {
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

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return label;
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String valorOuTraco(String value) {
        return isBlank(value) ? "-" : value;
    }

    private record DadosFaturasPage(
            List<FaturaDto> faturas,
            List<EncomendaDto> encomendas
    ) {
    }
}