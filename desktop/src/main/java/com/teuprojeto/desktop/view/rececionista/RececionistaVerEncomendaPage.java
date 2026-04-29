package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.LinhaEncomendaDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        VBox root = RececionistaUiFactory.createPageContainer("Ver Encomenda");

        Long encomendaId = shell.getEncomendaSelecionadaId();

        if (encomendaId == null) {
            VBox card = RececionistaUiFactory.createCard();
            Label aviso = new Label("Nenhuma encomenda foi selecionada.");
            Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));
            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return criarScroll(root);
        }

        Label estado = new Label("A carregar encomenda...");
        estado.setStyle("-fx-text-fill: #666666;");

        Label numeroValor = valor("-");
        Label clienteValor = valor("-");
        Label estadoValor = valor("-");
        Label dataValor = valor("-");
        Label entregaValor = valor("-");
        Label valorTotalValor = valor("-");
        Label designValor = valor("-");
        Label funcionarioValor = valor("-");
        Label descricaoDesignValor = valor("-");
        Label observacoesValor = valor("-");

        VBox dadosCard = RececionistaUiFactory.createCard();
        dadosCard.getChildren().addAll(
                titulo("Dados Gerais"),
                campo("Número", numeroValor),
                campo("Cliente", clienteValor),
                campo("Estado", estadoValor),
                campo("Data", dataValor),
                campo("Data de entrega", entregaValor),
                campo("Valor total", valorTotalValor),
                campo("Design", designValor),
                campo("Funcionário atribuído", funcionarioValor),
                campo("Descrição do design", descricaoDesignValor),
                campo("Observações", observacoesValor)
        );

        TableView<LinhaViewRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(250);

        TableColumn<LinhaViewRow, String> chapeuCol = new TableColumn<>("Chapéu");
        chapeuCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getChapeu()));

        TableColumn<LinhaViewRow, String> quantidadeCol = new TableColumn<>("Quantidade");
        quantidadeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQuantidade()));
        TableColumn<LinhaViewRow, String> tamanhoCol = new TableColumn<>("Tamanho");
        tamanhoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTamanho()));

        TableColumn<LinhaViewRow, String> coresCol = new TableColumn<>("Cores");
        coresCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCores()));

        table.getColumns().addAll(chapeuCol, quantidadeCol, tamanhoCol, coresCol);

        VBox linhasCard = RececionistaUiFactory.createCard();
        linhasCard.getChildren().addAll(titulo("Linhas da Encomenda"), table);

        Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
        voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));

        HBox actions = new HBox(10, voltar);

        root.getChildren().addAll(estado, dadosCard, linhasCard, actions);

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
                table
        );

        return criarScroll(root);
    }

    private Parent criarScroll(VBox root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #efefef; -fx-background-color: #efefef;");
        root.setStyle("-fx-background-color: #efefef;");
        return scrollPane;
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
                               TableView<LinhaViewRow> table) {

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
            estadoValor.setText(mapearEstado(encomenda.getIdestado()));
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

            List<LinhaViewRow> rows = data.linhas().stream()
                    .map(linha -> new LinhaViewRow(
                            nomesChapeus.getOrDefault(linha.getCodchapeu(), "Chapéu #" + linha.getCodchapeu()),
                            String.valueOf(linha.getQuantidade()),
                            valorOuTraco(linha.getTamanho()),
                            valorOuTraco(linha.getCores())
                    ))
                    .toList();

            table.getItems().setAll(rows);

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

    private Label titulo(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private VBox campo(String labelText, Label valor) {
        VBox box = new VBox(4);

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #666666;");

        box.getChildren().addAll(label, valor);
        return box;
    }

    private Label valor(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 15; -fx-text-fill: #222;");
        return label;
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "-";
        }
        return String.format("%.2f €", valor.doubleValue());
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

    private record DetalheEncomendaData(
            EncomendaDto encomenda,
            ClienteDto cliente,
            List<LinhaEncomendaDto> linhas,
            List<ChapeuDto> chapeus
    ) {
    }

    private static class LinhaViewRow {
        private final String chapeu;
        private final String quantidade;
        private final String tamanho;
        private final String cores;

        private LinhaViewRow(String chapeu, String quantidade, String tamanho, String cores) {
            this.chapeu = chapeu;
            this.quantidade = quantidade;
            this.tamanho = tamanho;
            this.cores = cores;
        }

        public String getChapeu() {
            return chapeu;
        }

        public String getQuantidade() {
            return quantidade;
        }

        public String getTamanho() {
            return tamanho;
        }

        public String getCores() {
            return cores;
        }
    }
}