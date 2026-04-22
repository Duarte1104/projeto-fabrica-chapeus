package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.CriarFaturaRequestDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.FaturaDto;
import com.teuprojeto.desktop.service.FaturaApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
        VBox root = RececionistaUiFactory.createPageContainer("Faturas");

        HBox actions = new HBox(10);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button criarFaturaBtn = RececionistaUiFactory.primaryButton("Criar Fatura");
        Button atualizarBtn = RececionistaUiFactory.secondaryButton("Atualizar");

        actions.getChildren().addAll(spacer, criarFaturaBtn, atualizarBtn);

        Label statusLabel = new Label("A carregar faturas...");
        statusLabel.setStyle("-fx-text-fill: #666666;");

        TableView<FaturaRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<FaturaRow, String> numero = new TableColumn<>("Fatura");
        numero.setCellValueFactory(c -> c.getValue().numeroProperty());

        TableColumn<FaturaRow, String> encomenda = new TableColumn<>("Encomenda");
        encomenda.setCellValueFactory(c -> c.getValue().encomendaProperty());

        TableColumn<FaturaRow, String> valor = new TableColumn<>("Valor");
        valor.setCellValueFactory(c -> c.getValue().valorProperty());

        TableColumn<FaturaRow, String> data = new TableColumn<>("Data");
        data.setCellValueFactory(c -> c.getValue().dataProperty());

        table.getColumns().addAll(numero, encomenda, valor, data);
        table.setItems(tableData);

        criarFaturaBtn.setOnAction(e -> abrirDialogCriarFatura(statusLabel, table));
        atualizarBtn.setOnAction(e -> carregarDados(statusLabel, table));

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(actions, statusLabel, table);

        root.getChildren().add(card);

        carregarDados(statusLabel, table);

        return root;
    }

    private void carregarDados(Label statusLabel, TableView<FaturaRow> table) {
        statusLabel.setText("A carregar faturas...");
        table.setDisable(true);

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

            encomendasFaturaveis = filtrarEncomendasFaturaveis(dados.encomendas(), dados.faturas());

            statusLabel.setText(
                    "Faturas carregadas: " + tableData.size() +
                            " | Encomendas faturáveis: " + encomendasFaturaveis.size()
            );
            table.setDisable(false);
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar faturas.");
            table.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter faturas");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private List<EncomendaDto> filtrarEncomendasFaturaveis(List<EncomendaDto> encomendas, List<FaturaDto> faturas) {
        Set<BigDecimal> idsJaFaturados = new HashSet<>();

        for (FaturaDto fatura : faturas) {
            if (fatura.getIdEncomenda() != null) {
                idsJaFaturados.add(fatura.getIdEncomenda());
            }
        }

        return encomendas.stream()
                .filter(encomenda -> encomenda.getNum() != null)
                .filter(encomenda -> Long.valueOf(3L).equals(encomenda.getIdestado()))
                .filter(encomenda -> !idsJaFaturados.contains(BigDecimal.valueOf(encomenda.getNum())))
                .toList();
    }

    private void abrirDialogCriarFatura(Label statusLabel, TableView<FaturaRow> table) {
        if (encomendasFaturaveis == null || encomendasFaturaveis.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sem encomendas faturáveis");
            alert.setContentText("Não existem encomendas prontas sem fatura.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Criar Fatura");

        ButtonType guardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarType, ButtonType.CANCEL);

        ComboBox<EncomendaDto> encomendaBox = new ComboBox<>();
        encomendaBox.getItems().addAll(encomendasFaturaveis);
        encomendaBox.setMaxWidth(Double.MAX_VALUE);
        encomendaBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(EncomendaDto encomenda) {
                if (encomenda == null || encomenda.getNum() == null) {
                    return "";
                }

                return "ENC-" + encomenda.getNum()
                        + " | Entrega: " + valorOuTraco(encomenda.getDataEntrega())
                        + " | Valor: " + formatarValor(encomenda.getValortotal());
            }

            @Override
            public EncomendaDto fromString(String string) {
                return null;
            }
        });

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da fatura...");
        observacoes.setPrefRowCount(4);

        VBox content = new VBox(12,
                new Label("Encomenda"),
                encomendaBox,
                new Label("Observações"),
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

                criarFatura(encomendaBox.getValue(), observacoes.getText(), statusLabel, table);
            }
        });
    }

    private void criarFatura(EncomendaDto encomenda, String observacoes, Label statusLabel, TableView<FaturaRow> table) {
        statusLabel.setText("A criar fatura...");
        table.setDisable(true);

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
            table.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Fatura criada com sucesso");
            alert.setContentText("A fatura foi criada para a encomenda ENC-" + encomenda.getNum() + ".");
            alert.showAndWait();

            carregarDados(statusLabel, table);
        });

        task.setOnFailed(event -> {
            table.setDisable(false);
            statusLabel.setText("Erro ao criar fatura.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao criar fatura");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
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
        return String.format("%.2f €", valor.doubleValue());
    }

    private String formatarData(String data) {
        if (isBlank(data)) {
            return "-";
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(data);
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception ignored) {
            return data;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String valorOuTraco(String value) {
        return isBlank(value) ? "-" : value;
    }

    private record DadosFaturasPage(List<FaturaDto> faturas, List<EncomendaDto> encomendas) {
    }
}