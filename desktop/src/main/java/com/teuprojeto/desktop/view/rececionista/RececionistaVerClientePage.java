package com.teuprojeto.desktop.view.rececionista;

import com.fasterxml.jackson.databind.JsonNode;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class RececionistaVerClientePage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaVerClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        ClienteRow clienteSelecionado = shell.getClienteSelecionado();

        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Ver Cliente");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Consulte os dados do cliente e o respetivo histórico de encomendas.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        if (clienteSelecionado == null || clienteSelecionado.getCod() == null) {
            VBox card = card();

            Label aviso = new Label("Nenhum cliente foi selecionado.");
            aviso.setStyle("-fx-font-size: 15; -fx-text-fill: #64748b; -fx-font-weight: bold;");

            Button voltar = RececionistaUiFactory.secondaryButton("Voltar à lista");
            voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().addAll(header, card);

            return wrap(root);
        }

        HBox content = new HBox(22);

        VBox info = card();
        info.setPrefWidth(390);

        HBox clienteHeader = new HBox(14);
        clienteHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setMinSize(64, 64);
        avatar.setPrefSize(64, 64);
        avatar.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 20;");

        Label iniciais = new Label(obterIniciais(clienteSelecionado.getNome()));
        iniciais.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
        avatar.getChildren().add(iniciais);

        VBox clienteTitleBox = new VBox(4);

        Label nomeTopo = new Label(valorOuTraco(clienteSelecionado.getNome()));
        nomeTopo.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label emailTopo = new Label(valorOuTraco(clienteSelecionado.getEmail()));
        emailTopo.setStyle("-fx-font-size: 13; -fx-text-fill: #2563eb; -fx-font-weight: bold;");

        clienteTitleBox.getChildren().addAll(nomeTopo, emailTopo);
        clienteHeader.getChildren().addAll(avatar, clienteTitleBox);

        Label nomeValor = valueLabel("A carregar...");
        Label emailValor = valueLabel("-");
        Label telefoneValor = valueLabel("-");
        Label moradaValor = valueLabel("-");
        Label nifValor = valueLabel("-");
        Label tipoValor = valueLabel("-");

        info.getChildren().addAll(
                clienteHeader,
                separator(),
                sectionTitle("Informações Pessoais"),
                item("Nome", nomeValor),
                item("Email", emailValor),
                item("Telefone", telefoneValor),
                item("Morada", moradaValor),
                item("NIF", nifValor),
                item("Tipo", tipoValor)
        );

        VBox historico = card();
        historico.setPrefWidth(720);
        HBox.setHgrow(historico, Priority.ALWAYS);

        HBox histHeader = new HBox();
        histHeader.setAlignment(Pos.CENTER_LEFT);

        VBox histTitleBox = new VBox(4);
        histTitleBox.getChildren().addAll(
                sectionTitle("Histórico de Encomendas"),
                smallText("Encomendas associadas a este cliente.")
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button criarEncomenda = RececionistaUiFactory.primaryButton("Criar Encomenda");
        criarEncomenda.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
        voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        histHeader.getChildren().addAll(histTitleBox, spacer, criarEncomenda, voltar);

        Label estadoCarregamento = new Label("A carregar histórico...");
        estadoCarregamento.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        VBox listaHistorico = new VBox(14);

        historico.getChildren().addAll(
                histHeader,
                separator(),
                estadoCarregamento,
                listaHistorico
        );

        content.getChildren().addAll(info, historico);
        root.getChildren().addAll(header, content);

        carregarClienteEDados(
                clienteSelecionado.getCod(),
                nomeValor,
                emailValor,
                telefoneValor,
                moradaValor,
                nifValor,
                tipoValor,
                nomeTopo,
                emailTopo,
                estadoCarregamento,
                listaHistorico
        );

        return wrap(root);
    }

    private void carregarClienteEDados(Integer clienteId,
                                       Label nomeValor,
                                       Label emailValor,
                                       Label telefoneValor,
                                       Label moradaValor,
                                       Label nifValor,
                                       Label tipoValor,
                                       Label nomeTopo,
                                       Label emailTopo,
                                       Label estadoCarregamento,
                                       VBox listaHistorico) {

        Task<ClienteDetalheData> task = new Task<>() {
            @Override
            protected ClienteDetalheData call() {
                ClienteDto cliente = clienteApiService.procurarPorId(clienteId);
                List<JsonNode> encomendas = clienteApiService.listarEncomendasPorCliente(clienteId);
                return new ClienteDetalheData(cliente, encomendas);
            }
        };

        task.setOnSucceeded(event -> {
            ClienteDetalheData data = task.getValue();
            ClienteDto cliente = data.cliente();
            List<JsonNode> encomendas = data.encomendas();

            nomeValor.setText(valorOuTraco(cliente.getNome()));
            emailValor.setText(valorOuTraco(cliente.getEmail()));
            telefoneValor.setText(valorOuTraco(cliente.getTelefone()));
            nifValor.setText(valorOuTraco(cliente.getNif()));
            tipoValor.setText(valorOuTraco(cliente.getTipo()));
            moradaValor.setText(montarMorada(cliente));

            nomeTopo.setText(valorOuTraco(cliente.getNome()));
            emailTopo.setText(valorOuTraco(cliente.getEmail()));

            listaHistorico.getChildren().clear();

            if (encomendas == null || encomendas.isEmpty()) {
                estadoCarregamento.setText("Sem encomendas para este cliente.");
                listaHistorico.getChildren().add(emptyBox("Ainda não existem encomendas associadas a este cliente."));
                return;
            }

            estadoCarregamento.setText("Total de encomendas: " + encomendas.size());

            for (JsonNode encomenda : encomendas) {
                listaHistorico.getChildren().add(encomendaCard(encomenda));
            }
        });

        task.setOnFailed(event -> {
            nomeValor.setText("-");
            emailValor.setText("-");
            telefoneValor.setText("-");
            nifValor.setText("-");
            tipoValor.setText("-");
            moradaValor.setText("-");
            listaHistorico.getChildren().clear();
            estadoCarregamento.setText("Erro ao carregar dados do cliente.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter cliente");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox encomendaCard(JsonNode encomenda) {
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

        Label iconText = new Label("📦");
        iconText.setStyle("-fx-font-size: 22;");
        icon.getChildren().add(iconText);

        String codigo = valorOuTraco(obterCodigo(encomenda));
        String data = valorOuTraco(obterCampoTexto(encomenda, "dataentrega", "dataEntrega", "dataencomenda", "dataEncomenda"));
        String produto = valorOuTraco(obterProduto(encomenda));
        String estado = valorOuTraco(obterEstado(encomenda));

        VBox main = new VBox(4);

        Label codigoLabel = new Label(codigo);
        codigoLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        Label produtoLabel = new Label(produto);
        produtoLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #0f172a; -fx-font-weight: bold;");

        main.getChildren().addAll(codigoLabel, produtoLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox dataBox = infoBlock("Data", data);

        Label estadoBadge = estadoBadge(estado);

        row.getChildren().addAll(icon, main, spacer, dataBox, estadoBadge);

        return row;
    }

    private String montarMorada(ClienteDto cliente) {
        StringBuilder sb = new StringBuilder();

        if (!isBlank(cliente.getRua())) {
            sb.append(cliente.getRua());
        }

        if (!isBlank(cliente.getNporta())) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(cliente.getNporta());
        }

        if (!isBlank(cliente.getCodpostal()) || !isBlank(cliente.getCidade())) {
            if (!sb.isEmpty()) {
                sb.append(" | ");
            }
            if (!isBlank(cliente.getCodpostal())) {
                sb.append(cliente.getCodpostal());
            }
            if (!isBlank(cliente.getCidade())) {
                if (!isBlank(cliente.getCodpostal())) {
                    sb.append(" ");
                }
                sb.append(cliente.getCidade());
            }
        }

        return sb.isEmpty() ? "-" : sb.toString();
    }

    private String obterCodigo(JsonNode encomenda) {
        String codigo = obterCampoTexto(encomenda, "codigo", "cod", "id", "num");
        if (isBlank(codigo)) {
            return "-";
        }

        if (codigo.startsWith("ENC-")) {
            return codigo;
        }

        return "ENC-" + codigo;
    }

    private String obterProduto(JsonNode encomenda) {
        String produtoDireto = obterCampoTexto(encomenda, "produto", "descricao");
        if (!isBlank(produtoDireto)) {
            return produtoDireto;
        }

        JsonNode linhas = encomenda.get("linhasEncomenda");
        if (linhas == null) {
            linhas = encomenda.get("linhasencomenda");
        }

        if (linhas != null && linhas.isArray() && !linhas.isEmpty()) {
            JsonNode primeiraLinha = linhas.get(0);

            String nome = obterCampoTexto(primeiraLinha, "produto", "descricao");
            if (!isBlank(nome)) {
                return nome;
            }

            JsonNode chapeu = primeiraLinha.get("chapeu");
            if (chapeu != null && chapeu.isObject()) {
                String nomeChapeu = obterCampoTexto(chapeu, "nome");
                if (!isBlank(nomeChapeu)) {
                    return nomeChapeu;
                }
            }
        }

        return "Encomenda";
    }

    private String obterEstado(JsonNode encomenda) {
        JsonNode estadoNode = encomenda.get("estado");

        if (estadoNode != null) {
            if (estadoNode.isTextual()) {
                return estadoNode.asText();
            }

            if (estadoNode.isObject()) {
                String nome = obterCampoTexto(estadoNode, "nome", "descricao");
                if (!isBlank(nome)) {
                    return nome;
                }

                String id = obterCampoTexto(estadoNode, "id", "cod");
                if (!isBlank(id)) {
                    return mapearEstadoPorId(id);
                }
            }

            if (estadoNode.isInt() || estadoNode.isLong()) {
                return mapearEstadoPorId(String.valueOf(estadoNode.asInt()));
            }
        }

        String estadoId = obterCampoTexto(encomenda, "estadoId", "idEstado", "idestado");
        if (!isBlank(estadoId)) {
            return mapearEstadoPorId(estadoId);
        }

        return "Sem estado";
    }

    private String mapearEstadoPorId(String valor) {
        return switch (valor) {
            case "1" -> "AGUARDA_DESIGN";
            case "2" -> "PREPARACAO";
            case "3" -> "PRONTA";
            case "4" -> "PAGA";
            default -> valor;
        };
    }

    private String obterCampoTexto(JsonNode node, String... campos) {
        for (String campo : campos) {
            JsonNode value = node.get(campo);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
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

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private Label smallText(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        return label;
    }

    private VBox item(String label, Label valueLabel) {
        VBox box = new VBox(4);

        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12;");

        box.getChildren().addAll(l1, valueLabel);
        return box;
    }

    private Label valueLabel(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
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

    private Label estadoBadge(String estado) {
        if (estado == null) {
            return badge("-", "#e5e7eb", "#334155");
        }

        return switch (estado.toUpperCase()) {
            case "AGUARDA_DESIGN" -> badge("Aguarda design", "#fee2e2", "#dc2626");
            case "PREPARACAO", "EM PREPARAÇÃO", "EM_PREPARACAO" -> badge("Em preparação", "#dbeafe", "#2563eb");
            case "PRONTA" -> badge("Pronta", "#ffedd5", "#ea580c");
            case "PAGA" -> badge("Paga", "#dcfce7", "#15803d");
            default -> badge(estado, "#e5e7eb", "#334155");
        };
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
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");
        return scrollPane;
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "CL";
        }

        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
    }

    private String valorOuTraco(String value) {
        return isBlank(value) ? "-" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ClienteDetalheData(ClienteDto cliente, List<JsonNode> encomendas) {
    }
}