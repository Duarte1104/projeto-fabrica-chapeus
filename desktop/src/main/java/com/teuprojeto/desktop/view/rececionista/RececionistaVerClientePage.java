package com.teuprojeto.desktop.view.rececionista;

import com.fasterxml.jackson.databind.JsonNode;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class RececionistaVerClientePage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaVerClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        ClienteRow clienteSelecionado = shell.getClienteSelecionado();

        VBox root = RececionistaUiFactory.createPageContainer("Ver Cliente");

        if (clienteSelecionado == null || clienteSelecionado.getCod() == null) {
            VBox card = RececionistaUiFactory.createCard();

            Label aviso = new Label("Nenhum cliente foi selecionado.");
            aviso.setStyle("-fx-font-size: 15; -fx-text-fill: #444;");

            Button voltar = RececionistaUiFactory.secondaryButton("Voltar à lista");
            voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return root;
        }

        HBox content = new HBox(18);

        VBox info = RececionistaUiFactory.createCard();
        info.setPrefWidth(340);

        Label nomeValor = valueLabel("A carregar...");
        Label emailValor = valueLabel("-");
        Label telefoneValor = valueLabel("-");
        Label moradaValor = valueLabel("-");
        Label nifValor = valueLabel("-");
        Label tipoValor = valueLabel("-");

        info.getChildren().addAll(
                title("Informações Pessoais"),
                item("Nome", nomeValor),
                item("Email", emailValor),
                item("Telefone", telefoneValor),
                item("Morada", moradaValor),
                item("NIF", nifValor),
                item("Tipo", tipoValor)
        );

        VBox historico = RececionistaUiFactory.createCard();
        historico.setPrefWidth(650);

        Label estadoCarregamento = new Label("A carregar histórico...");
        estadoCarregamento.setStyle("-fx-text-fill: #666;");

        VBox listaHistorico = new VBox(12);

        Button criarEncomenda = RececionistaUiFactory.primaryButton("Criar Encomenda");
        criarEncomenda.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        Button voltar = RececionistaUiFactory.secondaryButton("Voltar");
        voltar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        HBox actions = new HBox(10, criarEncomenda, voltar);

        historico.getChildren().addAll(
                title("Histórico de Encomendas"),
                estadoCarregamento,
                listaHistorico,
                actions
        );

        content.getChildren().addAll(info, historico);
        root.getChildren().add(content);

        carregarClienteEDados(
                clienteSelecionado.getCod(),
                nomeValor,
                emailValor,
                telefoneValor,
                moradaValor,
                nifValor,
                tipoValor,
                estadoCarregamento,
                listaHistorico
        );

        return root;
    }

    private void carregarClienteEDados(Integer clienteId,
                                       Label nomeValor,
                                       Label emailValor,
                                       Label telefoneValor,
                                       Label moradaValor,
                                       Label nifValor,
                                       Label tipoValor,
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

            listaHistorico.getChildren().clear();

            if (encomendas == null || encomendas.isEmpty()) {
                estadoCarregamento.setText("Sem encomendas para este cliente.");
                return;
            }

            estadoCarregamento.setText("Total de encomendas: " + encomendas.size());

            for (JsonNode encomenda : encomendas) {
                Label linha = new Label(formatarLinhaEncomenda(encomenda));
                linha.setStyle("-fx-font-size: 15; -fx-text-fill: #333;");
                listaHistorico.getChildren().add(linha);
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

    private String formatarLinhaEncomenda(JsonNode encomenda) {
        String codigo = valorOuTraco(obterCodigo(encomenda));
        String data = valorOuTraco(obterCampoTexto(encomenda, "dataentrega", "dataEntrega", "dataencomenda", "dataEncomenda"));
        String produto = valorOuTraco(obterProduto(encomenda));
        String estado = valorOuTraco(obterEstado(encomenda));

        return codigo + " | " + data + " | " + produto + " | " + estado;
    }

    private String obterCodigo(JsonNode encomenda) {
        String codigo = obterCampoTexto(encomenda, "codigo", "cod", "id");
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

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private VBox item(String label, Label valueLabel) {
        VBox box = new VBox(4);

        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #666;");

        box.getChildren().addAll(l1, valueLabel);
        return box;
    }

    private Label valueLabel(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 15; -fx-text-fill: #222;");
        return label;
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