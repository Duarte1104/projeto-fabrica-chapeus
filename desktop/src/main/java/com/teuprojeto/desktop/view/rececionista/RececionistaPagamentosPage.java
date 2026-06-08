package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.CriarPagamentoRequestDto;
import com.teuprojeto.desktop.dto.CriarReciboRequestDto;
import com.teuprojeto.desktop.dto.FaturaDto;
import com.teuprojeto.desktop.dto.PagamentoDto;
import com.teuprojeto.desktop.dto.ReciboDto;
import com.teuprojeto.desktop.service.FaturaApiService;
import com.teuprojeto.desktop.service.PagamentoApiService;
import com.teuprojeto.desktop.service.ReciboApiService;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RececionistaPagamentosPage {

    private final RececionistaShellView shell;
    private final FaturaApiService faturaApiService = new FaturaApiService();
    private final PagamentoApiService pagamentoApiService = new PagamentoApiService();
    private final ReciboApiService reciboApiService = new ReciboApiService();

    private List<FaturaPagamentoResumo> faturasResumo = List.of();
    private Map<Long, ReciboDto> recibosPorPagamento = new HashMap<>();

    public RececionistaPagamentosPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Pagamentos e Recibos");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte pagamentos recebidos e emita recibos associados.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button novoPagamentoBtn = RececionistaUiFactory.primaryButton("Registar Pagamento");
        Button atualizarBtn = outlineButton("Atualizar");

        topBar.getChildren().addAll(spacer, novoPagamentoBtn, atualizarBtn);

        Label statusLabel = new Label("A carregar pagamentos...");
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        HBox stats = new HBox(18);

        Label totalPagamentos = statNumber("-");
        Label totalRecebido = statNumber("-");
        Label recibosEmitidos = statNumber("-");
        Label recibosPendentes = statNumber("-");

        stats.getChildren().addAll(
                statCard("Pagamentos", totalPagamentos, "Total registados", "#2563eb", "💳"),
                statCard("Recebido", totalRecebido, "Total pago", "#16a34a", "€"),
                statCard("Recibos Emitidos", recibosEmitidos, "Pagamentos com recibo", "#7c3aed", "🧾"),
                statCard("Por Emitir", recibosPendentes, "Aguardam recibo", "#f97316", "!")
        );

        VBox pagamentosCard = card();
        VBox listaPagamentos = new VBox(14);

        pagamentosCard.getChildren().addAll(
                sectionHeader("💳", "Pagamentos Recebidos", "Pagamentos efetuados pelos clientes e estado do recibo."),
                separator(),
                listaPagamentos
        );

        root.getChildren().addAll(header, topBar, statusLabel, stats, pagamentosCard);

        novoPagamentoBtn.setOnAction(e -> abrirDialogCriarPagamento(
                statusLabel,
                listaPagamentos,
                totalPagamentos,
                totalRecebido,
                recibosEmitidos,
                recibosPendentes
        ));

        atualizarBtn.setOnAction(e -> carregarDados(
                statusLabel,
                listaPagamentos,
                totalPagamentos,
                totalRecebido,
                recibosEmitidos,
                recibosPendentes
        ));

        carregarDados(
                statusLabel,
                listaPagamentos,
                totalPagamentos,
                totalRecebido,
                recibosEmitidos,
                recibosPendentes
        );

        return wrap(root);
    }

    private void carregarDados(
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        statusLabel.setText("A carregar pagamentos e recibos...");

        Task<DadosPagamentosPage> task = new Task<>() {
            @Override
            protected DadosPagamentosPage call() {
                List<FaturaDto> faturas = faturaApiService.listarFaturas();
                List<PagamentoDto> pagamentos = pagamentoApiService.listarPagamentos();
                List<ReciboDto> recibos = reciboApiService.listarRecibos();

                return new DadosPagamentosPage(faturas, pagamentos, recibos);
            }
        };

        task.setOnSucceeded(event -> {
            DadosPagamentosPage dados = task.getValue();

            faturasResumo = criarResumoFaturas(dados.faturas(), dados.pagamentos());
            recibosPorPagamento = criarMapaRecibosPorPagamento(dados.recibos());

            preencherStats(
                    dados.pagamentos(),
                    dados.recibos(),
                    totalPagamentos,
                    totalRecebido,
                    recibosEmitidos,
                    recibosPendentes
            );

            preencherListaPagamentos(
                    listaPagamentos,
                    dados.pagamentos(),
                    statusLabel,
                    totalPagamentos,
                    totalRecebido,
                    recibosEmitidos,
                    recibosPendentes
            );

            statusLabel.setText(
                    "Pagamentos carregados: " + dados.pagamentos().size()
                            + " | Recibos emitidos: " + dados.recibos().size()
            );
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao carregar pagamentos.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter pagamentos e recibos");
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

    private Map<Long, ReciboDto> criarMapaRecibosPorPagamento(List<ReciboDto> recibos) {
        Map<Long, ReciboDto> mapa = new HashMap<>();

        for (ReciboDto recibo : recibos) {
            if (recibo.getIdPagamento() != null) {
                mapa.put(recibo.getIdPagamento(), recibo);
            }
        }

        return mapa;
    }

    private List<FaturaPagamentoResumo> criarResumoFaturas(
            List<FaturaDto> faturas,
            List<PagamentoDto> pagamentos
    ) {
        Map<Long, BigDecimal> totalPagoPorFatura = new HashMap<>();

        for (PagamentoDto pagamento : pagamentos) {
            if (pagamento.getNumfatura() == null || pagamento.getValorpago() == null) {
                continue;
            }

            BigDecimal totalAtual = totalPagoPorFatura.getOrDefault(
                    pagamento.getNumfatura(),
                    BigDecimal.ZERO
            );

            totalPagoPorFatura.put(
                    pagamento.getNumfatura(),
                    totalAtual.add(pagamento.getValorpago())
            );
        }

        return faturas.stream()
                .sorted(Comparator.comparing(FaturaDto::getId).reversed())
                .map(fatura -> {
                    BigDecimal valorFatura = fatura.getValor() == null ? BigDecimal.ZERO : fatura.getValor();
                    BigDecimal totalPago = totalPagoPorFatura.getOrDefault(fatura.getId(), BigDecimal.ZERO);
                    BigDecimal valorEmDivida = valorFatura.subtract(totalPago);

                    if (valorEmDivida.compareTo(BigDecimal.ZERO) < 0) {
                        valorEmDivida = BigDecimal.ZERO;
                    }

                    return new FaturaPagamentoResumo(fatura, totalPago, valorEmDivida);
                })
                .toList();
    }

    private void preencherStats(
            List<PagamentoDto> pagamentos,
            List<ReciboDto> recibos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        BigDecimal recebido = pagamentos.stream()
                .map(PagamentoDto::getValorpago)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int emitidos = recibos.size();
        int pendentes = pagamentos.size() - emitidos;

        if (pendentes < 0) {
            pendentes = 0;
        }

        totalPagamentos.setText(String.valueOf(pagamentos.size()));
        totalRecebido.setText(formatarMoeda(recebido));
        recibosEmitidos.setText(String.valueOf(emitidos));
        recibosPendentes.setText(String.valueOf(pendentes));
    }

    private void preencherListaPagamentos(
            VBox listaPagamentos,
            List<PagamentoDto> pagamentos,
            Label statusLabel,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        listaPagamentos.getChildren().clear();

        if (pagamentos.isEmpty()) {
            listaPagamentos.getChildren().add(emptyBox("Ainda não existem pagamentos registados."));
            return;
        }

        pagamentos.stream()
                .sorted(Comparator.comparing(PagamentoDto::getCod).reversed())
                .forEach(pagamento -> listaPagamentos.getChildren().add(
                        pagamentoCard(
                                pagamento,
                                recibosPorPagamento.get(pagamento.getCod()),
                                statusLabel,
                                listaPagamentos,
                                totalPagamentos,
                                totalRecebido,
                                recibosEmitidos,
                                recibosPendentes
                        )
                ));
    }

    private VBox pagamentoCard(
            PagamentoDto pagamento,
            ReciboDto recibo,
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        box.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(4);

        Label numero = new Label("Pagamento #" + valorOuTraco(pagamento.getCod()));
        numero.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label fatura = new Label("Fatura: " + formatarNumeroFatura(pagamento.getNumfatura()));
        fatura.setStyle("-fx-font-size: 13; -fx-text-fill: #2563eb; -fx-font-weight: bold;");

        left.getChildren().addAll(numero, fatura);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valor = badge(formatarMoeda(pagamento.getValorpago()), "#dcfce7", "#15803d");

        top.getChildren().addAll(left, spacer, valor);

        HBox dados = new HBox(20);
        dados.getChildren().addAll(
                infoBlock("Encomenda", formatarNumeroEncomenda(pagamento.getIdencomenda())),
                infoBlock("Método", valorOuTraco(pagamento.getMetodopagamento())),
                infoBlock("Data", formatarData(pagamento.getDatapagamento()))
        );

        HBox reciboRow = new HBox(14);
        reciboRow.setAlignment(Pos.CENTER_LEFT);

        if (recibo == null) {
            Label estado = badge("Aguarda recibo", "#ffedd5", "#ea580c");

            Button emitirBtn = RececionistaUiFactory.primaryButton("Emitir Recibo");
            emitirBtn.setOnAction(e -> abrirDialogEmitirRecibo(
                    pagamento,
                    statusLabel,
                    listaPagamentos,
                    totalPagamentos,
                    totalRecebido,
                    recibosEmitidos,
                    recibosPendentes
            ));

            reciboRow.getChildren().addAll(estado, emitirBtn);
        } else {
            Label estado = badge(
                    "Recibo emitido: RC-" + formatarId(recibo.getId()),
                    "#dcfce7",
                    "#15803d"
            );

            VBox reciboInfo = new VBox(4);
            reciboInfo.getChildren().addAll(
                    estado,
                    smallText("Data do recibo: " + formatarData(recibo.getData()))
            );

            reciboRow.getChildren().add(reciboInfo);
        }

        box.getChildren().addAll(top, dados, reciboRow);

        if (pagamento.getObservacoes() != null && !pagamento.getObservacoes().isBlank()) {
            Label obs = new Label(pagamento.getObservacoes());
            obs.setWrapText(true);
            obs.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
            box.getChildren().add(obs);
        }

        return box;
    }

    private void abrirDialogEmitirRecibo(
            PagamentoDto pagamento,
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Emitir Recibo");

        ButtonType emitirType = new ButtonType("Emitir Recibo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(emitirType, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(12));

        Label title = new Label("Emitir Recibo");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label(
                "Vai ser emitido um recibo para o pagamento #"
                        + pagamento.getCod()
                        + ", no valor de "
                        + formatarMoeda(pagamento.getValorpago())
                        + "."
        );
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações do recibo...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        content.getChildren().addAll(
                title,
                subtitle,
                separator(),
                fieldLabel("Observações"),
                observacoes
        );

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(buttonType -> buttonType);

        dialog.showAndWait().ifPresent(result -> {
            if (result == emitirType) {
                CriarReciboRequestDto dto = new CriarReciboRequestDto();
                dto.setIdPagamento(pagamento.getCod());
                dto.setObservacoes(isBlank(observacoes.getText()) ? null : observacoes.getText().trim());

                emitirRecibo(
                        dto,
                        statusLabel,
                        listaPagamentos,
                        totalPagamentos,
                        totalRecebido,
                        recibosEmitidos,
                        recibosPendentes
                );
            }
        });
    }

    private void emitirRecibo(
            CriarReciboRequestDto dto,
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        statusLabel.setText("A emitir recibo...");

        Task<ReciboDto> task = new Task<>() {
            @Override
            protected ReciboDto call() {
                return reciboApiService.criarRecibo(dto);
            }
        };

        task.setOnSucceeded(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Recibo emitido com sucesso");
            alert.setContentText("O recibo foi associado ao pagamento selecionado.");
            alert.showAndWait();

            carregarDados(
                    statusLabel,
                    listaPagamentos,
                    totalPagamentos,
                    totalRecebido,
                    recibosEmitidos,
                    recibosPendentes
            );
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao emitir recibo.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao emitir recibo");
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

    private void abrirDialogCriarPagamento(
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        List<FaturaPagamentoResumo> faturasComDivida = faturasResumo.stream()
                .filter(resumo -> resumo.valorEmDivida().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (faturasComDivida.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sem faturas pendentes");
            alert.setContentText("Não existem faturas com valor em dívida para registar pagamento.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registar Pagamento");

        ButtonType guardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarType, ButtonType.CANCEL);

        ComboBox<FaturaPagamentoResumo> faturaBox = new ComboBox<>();
        faturaBox.getItems().addAll(faturasComDivida);
        faturaBox.setMaxWidth(Double.MAX_VALUE);
        faturaBox.setStyle(inputStyle());

        faturaBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(FaturaPagamentoResumo resumo) {
                if (resumo == null || resumo.fatura() == null) {
                    return "";
                }

                return formatarNumeroFatura(resumo.fatura().getId())
                        + " | "
                        + formatarNumeroEncomenda(resumo.fatura().getIdEncomenda())
                        + " | Em dívida: "
                        + formatarMoeda(resumo.valorEmDivida());
            }

            @Override
            public FaturaPagamentoResumo fromString(String string) {
                return null;
            }
        });

        TextField valorField = new TextField();
        valorField.setPromptText("Ex.: 20.00");
        valorField.setStyle(inputStyle());

        ComboBox<String> metodoBox = new ComboBox<>();
        metodoBox.getItems().addAll("Numerário", "Cartão", "MB WAY", "Transferência", "Outro");
        metodoBox.setValue("MB WAY");
        metodoBox.setMaxWidth(Double.MAX_VALUE);
        metodoBox.setStyle(inputStyle());

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações do pagamento...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        Label infoDivida = new Label("Selecione uma fatura para ver o valor em dívida.");
        infoDivida.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        faturaBox.setOnAction(e -> {
            FaturaPagamentoResumo resumo = faturaBox.getValue();

            if (resumo != null) {
                infoDivida.setText(
                        "Valor em dívida: "
                                + formatarMoeda(resumo.valorEmDivida())
                                + " | Valor total: "
                                + formatarMoeda(resumo.fatura().getValor())
                );
            }
        });

        VBox content = new VBox(12);
        content.setPadding(new Insets(12));

        Label title = new Label("Novo Pagamento");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Registe um pagamento parcial ou total para uma fatura pendente.");
        subtitle.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        content.getChildren().addAll(
                title,
                subtitle,
                separator(),
                fieldLabel("Fatura"),
                faturaBox,
                infoDivida,
                fieldLabel("Valor pago"),
                valorField,
                fieldLabel("Método de pagamento"),
                metodoBox,
                fieldLabel("Observações"),
                observacoes
        );

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(buttonType -> buttonType);

        dialog.showAndWait().ifPresent(result -> {
            if (result == guardarType) {
                if (faturaBox.getValue() == null) {
                    mostrarAviso("Dados inválidos", "Seleciona uma fatura.");
                    return;
                }

                BigDecimal valorPago;

                try {
                    valorPago = parseValor(valorField.getText());
                } catch (IllegalArgumentException e) {
                    mostrarAviso("Valor inválido", e.getMessage());
                    return;
                }

                if (valorPago.compareTo(faturaBox.getValue().valorEmDivida()) > 0) {
                    mostrarAviso(
                            "Valor inválido",
                            "O valor pago não pode ser superior ao valor em dívida: "
                                    + formatarMoeda(faturaBox.getValue().valorEmDivida())
                    );
                    return;
                }

                CriarPagamentoRequestDto dto = new CriarPagamentoRequestDto();
                dto.setIdFatura(faturaBox.getValue().fatura().getId());
                dto.setValorPago(valorPago);
                dto.setMetodoPagamento(metodoBox.getValue());
                dto.setObservacoes(isBlank(observacoes.getText()) ? null : observacoes.getText().trim());

                criarPagamento(
                        dto,
                        statusLabel,
                        listaPagamentos,
                        totalPagamentos,
                        totalRecebido,
                        recibosEmitidos,
                        recibosPendentes
                );
            }
        });
    }

    private void criarPagamento(
            CriarPagamentoRequestDto dto,
            Label statusLabel,
            VBox listaPagamentos,
            Label totalPagamentos,
            Label totalRecebido,
            Label recibosEmitidos,
            Label recibosPendentes
    ) {
        statusLabel.setText("A registar pagamento...");

        Task<PagamentoDto> task = new Task<>() {
            @Override
            protected PagamentoDto call() {
                return pagamentoApiService.criarPagamento(dto);
            }
        };

        task.setOnSucceeded(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Pagamento registado com sucesso");
            alert.setContentText("O pagamento foi registado e associado à respetiva fatura.");
            alert.showAndWait();

            carregarDados(
                    statusLabel,
                    listaPagamentos,
                    totalPagamentos,
                    totalRecebido,
                    recibosEmitidos,
                    recibosPendentes
            );
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Erro ao registar pagamento.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao registar pagamento");
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

    private VBox statCard(String title, Label value, String subtitle, String color, String iconText) {
        VBox card = card();
        card.setPrefWidth(240);
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(54, 54);
        icon.setPrefSize(54, 54);
        icon.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 14, 0, 0, 5);"
        );

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 14; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        text.getChildren().addAll(titleLabel, value, subtitleLabel);
        box.getChildren().addAll(icon, text);

        card.getChildren().add(box);
        return card;
    }

    private Label statNumber(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
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

    private VBox infoBlock(String title, String value) {
        VBox box = new VBox(4);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        Label v = new Label(value == null ? "-" : value);
        v.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

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

    private VBox emptyBox(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);
        return box;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return label;
    }

    private Label smallText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");
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

    private BigDecimal parseValor(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Indica o valor pago.");
        }

        try {
            String normalizado = texto.trim().replace(",", ".");
            BigDecimal valor = new BigDecimal(normalizado);

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor pago deve ser superior a zero.");
            }

            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O valor deve ser numérico. Exemplo: 20.00");
        }
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

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "0,00 €";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private String formatarData(String data) {
        if (data == null || data.isBlank()) {
            return "-";
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(data);
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return data;
        }
    }

    private String formatarId(Long id) {
        if (id == null) {
            return "?";
        }

        return String.format("%03d", id);
    }

    private String valorOuTraco(Object valor) {
        if (valor == null) {
            return "-";
        }

        return valor.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private record DadosPagamentosPage(
            List<FaturaDto> faturas,
            List<PagamentoDto> pagamentos,
            List<ReciboDto> recibos
    ) {
    }

    private record FaturaPagamentoResumo(
            FaturaDto fatura,
            BigDecimal totalPago,
            BigDecimal valorEmDivida
    ) {
    }
}