package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.LinhaEncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RececionistaVerEncomendaPage {

    private final RececionistaShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaVerEncomendaPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Ver Encomenda");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte os dados completos da encomenda selecionada.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Long encomendaId = shell.getEncomendaSelecionadaId();

        if (encomendaId == null) {
            VBox card = card();

            Label aviso = new Label("Nenhuma encomenda foi selecionada.");
            aviso.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

            Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().addAll(header, card);

            return wrap(root);
        }

        Label estado = new Label("A carregar encomenda...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label numeroValor = valueLabel("-");
        Label clienteValor = valueLabel("-");
        Label estadoValor = valueLabel("-");
        Label dataValor = valueLabel("-");
        Label entregaValor = valueLabel("-");
        Label valorTotalValor = valueLabel("-");
        Label designValor = valueLabel("-");
        Label funcionarioValor = valueLabel("-");
        Label descricaoDesignValor = valueLabel("-");
        Label observacoesValor = valueLabel("-");

        VBox dadosCard = card();

        HBox dadosHeader = sectionHeader(
                "📦",
                "Dados Gerais",
                "Informação principal da encomenda."
        );

        HBox infoGrid1 = new HBox(30);
        infoGrid1.getChildren().addAll(
                infoBlock("Número", numeroValor),
                infoBlock("Cliente", clienteValor),
                infoBlock("Estado", estadoValor),
                infoBlock("Valor total", valorTotalValor)
        );

        HBox infoGrid2 = new HBox(30);
        infoGrid2.getChildren().addAll(
                infoBlock("Data", dataValor),
                infoBlock("Data de entrega", entregaValor),
                infoBlock("Design", designValor),
                infoBlock("Funcionário", funcionarioValor)
        );

        VBox textosBox = new VBox(14);
        textosBox.getChildren().addAll(
                bigInfoBlock("Descrição do design", descricaoDesignValor),
                bigInfoBlock("Observações", observacoesValor)
        );

        dadosCard.getChildren().addAll(
                dadosHeader,
                separator(),
                infoGrid1,
                infoGrid2,
                textosBox
        );

        VBox linhasCard = card();

        VBox linhasLista = new VBox(14);

        linhasCard.getChildren().addAll(
                sectionHeader(
                        "🎩",
                        "Linhas da Encomenda",
                        "Chapéus, quantidades, tamanhos e cores associados."
                ),
                separator(),
                linhasLista
        );

        Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
        voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));

        HBox actions = new HBox(12, voltar);
        actions.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(header, estado, dadosCard, linhasCard, actions);

        carregarDados(
                encomendaId,
                estado,
                numeroValor,
                clienteValor,
                estadoValor,
                dataValor,
                entregaValor,
                valorTotalValor,
                designValor,
                funcionarioValor,
                descricaoDesignValor,
                observacoesValor,
                linhasLista
        );

        return wrap(root);
    }

    private void carregarDados(Long encomendaId,
                               Label estado,
                               Label numeroValor,
                               Label clienteValor,
                               Label estadoValor,
                               Label dataValor,
                               Label entregaValor,
                               Label valorTotalValor,
                               Label designValor,
                               Label funcionarioValor,
                               Label descricaoDesignValor,
                               Label observacoesValor,
                               VBox linhasLista) {

        Task<DetalheEncomendaData> task = new Task<>() {
            @Override
            protected DetalheEncomendaData call() {
                EncomendaDto encomenda = encomendaApiService.procurarPorId(encomendaId);
                ClienteDto cliente = clienteApiService.procurarPorId(encomenda.getIdcliente());
                List<LinhaEncomendaDto> linhas = encomendaApiService.listarLinhas(encomendaId);
                List<ChapeuDto> chapeus = encomendaApiService.listarChapeus();

                return new DetalheEncomendaData(encomenda, cliente, linhas, chapeus);
            }
        };

        task.setOnSucceeded(event -> {
            DetalheEncomendaData data = task.getValue();

            EncomendaDto encomenda = data.encomenda();
            ClienteDto cliente = data.cliente();

            numeroValor.setText("ENC-" + encomenda.getNum());
            clienteValor.setText(cliente.getNome());
            estadoValor.setText(formatarEstado(mapearEstado(encomenda.getIdestado())));
            dataValor.setText(valorOuTraco(encomenda.getData()));
            entregaValor.setText(valorOuTraco(encomenda.getDataEntrega()));
            valorTotalValor.setText(formatarValor(encomenda.getValortotal()));
            designValor.setText(Boolean.TRUE.equals(encomenda.getDesign()) ? "Sim" : "Não");
            funcionarioValor.setText(encomenda.getIdfuncionario() == null ? "Por atribuir" : "Funcionário #" + encomenda.getIdfuncionario());
            descricaoDesignValor.setText(valorOuTraco(encomenda.getDescricaoDesign()));
            observacoesValor.setText(valorOuTraco(encomenda.getObservacoes()));

            Map<Long, String> nomesChapeus = data.chapeus().stream()
                    .filter(c -> c.getCod() != null)
                    .collect(Collectors.toMap(
                            ChapeuDto::getCod,
                            c -> c.getNome() == null ? "Chapéu #" + c.getCod() : c.getNome(),
                            (a, b) -> a
                    ));

            linhasLista.getChildren().clear();

            if (data.linhas().isEmpty()) {
                linhasLista.getChildren().add(emptyBox("Esta encomenda ainda não tem linhas."));
            } else {
                for (LinhaEncomendaDto linha : data.linhas()) {
                    linhasLista.getChildren().add(linhaCard(linha, nomesChapeus));
                }
            }

            estado.setText("Encomenda carregada.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar encomenda.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter encomenda");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox linhaCard(LinhaEncomendaDto linha, Map<Long, String> nomesChapeus) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        StackPane icon = new StackPane();
        icon.setMinSize(52, 52);
        icon.setPrefSize(52, 52);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 16;");

        Label iconText = new Label("🎩");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        String nomeChapeu = nomesChapeus.getOrDefault(
                linha.getCodchapeu(),
                "Chapéu #" + linha.getCodchapeu()
        );

        VBox main = new VBox(4);

        Label chapeu = new Label(nomeChapeu);
        chapeu.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label codigo = new Label("Código: " + linha.getCodchapeu());
        codigo.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        main.getChildren().addAll(chapeu, codigo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(
                icon,
                main,
                spacer,
                infoBlock("Quantidade", String.valueOf(linha.getQuantidade())),
                infoBlock("Tamanho", valorOuTraco(linha.getTamanho())),
                infoBlock("Cores", valorOuTraco(linha.getCores()))
        );

        return row;
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
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

        return card;
    }

    private VBox infoBlock(String labelText, String value) {
        return infoBlock(labelText, valueLabel(value));
    }

    private VBox infoBlock(String labelText, Label valor) {
        VBox box = new VBox(4);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        box.getChildren().addAll(label, valor);
        return box;
    }

    private VBox bigInfoBlock(String labelText, Label valor) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(14));
        box.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;"
        );

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        valor.setWrapText(true);

        box.getChildren().addAll(label, valor);
        return box;
    }

    private Label valueLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        return label;
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
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        return scrollPane;
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "-";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private String valorOuTraco(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String mapearEstado(Long idestado) {
        if (idestado == null) {
            return "SEM_ESTADO";
        }

        return switch (idestado.intValue()) {
            case 1 -> "AGUARDA_DESIGN";
            case 2 -> "PREPARACAO";
            case 3 -> "PRONTA";
            case 4 -> "PAGA";
            default -> "ESTADO_" + idestado;
        };
    }

    private String formatarEstado(String estado) {
        if (estado == null) {
            return "-";
        }

        return switch (estado.toUpperCase()) {
            case "AGUARDA_DESIGN" -> "Aguarda design";
            case "PREPARACAO", "EM_PREPARACAO" -> "Em preparação";
            case "PRONTA" -> "Pronta";
            case "PAGA" -> "Paga";
            default -> estado;
        };
    }

    private record DetalheEncomendaData(
            EncomendaDto encomenda,
            ClienteDto cliente,
            List<LinhaEncomendaDto> linhas,
            List<ChapeuDto> chapeus
    ) {
    }
}