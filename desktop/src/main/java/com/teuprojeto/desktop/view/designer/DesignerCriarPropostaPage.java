package com.teuprojeto.desktop.view.designer;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class DesignerCriarPropostaPage {

    private final DesignerShellView shell;
    private String ficheiroSelecionado = "Nenhum ficheiro selecionado";

    public DesignerCriarPropostaPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        PedidoDesignRow pedido = shell.getPedidoSelecionado();

        VBox content = DesignerUiFactory.createPageContainer("Criar Proposta de Design");
        content.setFillWidth(true);

        if (pedido == null) {
            VBox card = DesignerUiFactory.createCard();

            Label aviso = new Label("Nenhum pedido de design foi selecionado.");
            Button voltar = DesignerUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

            card.getChildren().addAll(aviso, voltar);
            content.getChildren().add(card);

            return createScrollable(content);
        }

        HBox topo = new HBox(18);

        VBox dadosPedido = DesignerUiFactory.createCard();
        dadosPedido.setPrefWidth(520);
        dadosPedido.setMinWidth(520);
        HBox.setHgrow(dadosPedido, Priority.ALWAYS);

        dadosPedido.getChildren().addAll(
                sectionTitle("Dados da Encomenda"),
                item("Código", pedido.getCodigoEncomenda()),
                item("Cliente", pedido.getCliente()),
                item("Produto", pedido.getProduto()),
                item("Quantidade", String.valueOf(pedido.getQuantidade())),
                item("Data", pedido.getData()),
                item("Pedido", pedido.getDescricaoPedido()),
                item("Observações", pedido.getObservacoes())
        );

        VBox especificacoes = DesignerUiFactory.createCard();
        especificacoes.setPrefWidth(380);
        especificacoes.setMinWidth(380);

        TextField categoria = new TextField();
        ComboBox<String> tamanho = new ComboBox<>();
        tamanho.getItems().addAll("Pequeno", "Médio", "Grande");
        tamanho.setMaxWidth(Double.MAX_VALUE);

        TextField corPrincipal = new TextField();
        TextField coresSecundarias = new TextField();
        TextField materialPrincipal = new TextField();
        TextField quantidadeMaterial = new TextField();

        GridPane rightForm = new GridPane();
        rightForm.setHgap(12);
        rightForm.setVgap(12);

        rightForm.add(new Label("Categoria"), 0, 0);
        rightForm.add(categoria, 0, 1);

        rightForm.add(new Label("Tamanho"), 0, 2);
        rightForm.add(tamanho, 0, 3);

        rightForm.add(new Label("Cor Principal"), 0, 4);
        rightForm.add(corPrincipal, 0, 5);

        rightForm.add(new Label("Cores Secundárias"), 0, 6);
        rightForm.add(coresSecundarias, 0, 7);

        rightForm.add(new Label("Material Principal"), 0, 8);
        rightForm.add(materialPrincipal, 0, 9);

        rightForm.add(new Label("Quantidade Material"), 0, 10);
        rightForm.add(quantidadeMaterial, 0, 11);

        especificacoes.getChildren().addAll(sectionTitle("Especificações"), rightForm);

        topo.getChildren().addAll(dadosPedido, especificacoes);

        VBox fichaTecnica = DesignerUiFactory.createCard();

        TextField nomeDesign = new TextField();
        nomeDesign.setPromptText("Nome da proposta / produto");

        TextArea descricaoTecnica = new TextArea();
        descricaoTecnica.setPromptText("Descrição técnica...");
        descricaoTecnica.setPrefRowCount(4);

        TextArea instrucoes = new TextArea();
        instrucoes.setPromptText("Instruções de produção...");
        instrucoes.setPrefRowCount(4);

        fichaTecnica.getChildren().addAll(
                sectionTitle("Ficha Técnica"),
                new Label("Nome do Design"),
                nomeDesign,
                new Label("Descrição Técnica"),
                descricaoTecnica,
                new Label("Instruções de Produção"),
                instrucoes
        );

        VBox uploadCard = DesignerUiFactory.createCard();

        Label ficheiroLabel = new Label(ficheiroSelecionado);
        ficheiroLabel.setStyle("-fx-text-fill: #666;");

        Button selecionarFicheiro = DesignerUiFactory.secondaryButton("Selecionar Ficheiro");
        selecionarFicheiro.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Selecionar ficheiro do design");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Ficheiros de imagem e PDF", "*.png", "*.jpg", "*.jpeg", "*.pdf", "*.svg")
            );

            File file = chooser.showOpenDialog(null);
            if (file != null) {
                ficheiroSelecionado = file.getName();
                ficheiroLabel.setText(ficheiroSelecionado);
            }
        });

        Button guardar = DesignerUiFactory.primaryButton("Guardar Rascunho");
        guardar.setOnAction(e -> mostrarIndisponivel("Guardar rascunho"));

        Button enviar = DesignerUiFactory.primaryButton("Enviar ao Cliente");
        enviar.setOnAction(e -> mostrarIndisponivel("Enviar ao cliente"));

        Button cancelar = DesignerUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

        HBox botoes = new HBox(10, guardar, enviar, cancelar);

        uploadCard.getChildren().addAll(
                sectionTitle("Ficheiro do Design"),
                ficheiroLabel,
                selecionarFicheiro,
                botoes
        );

        content.getChildren().addAll(topo, fichaTecnica, uploadCard);

        return createScrollable(content);
    }

    private Parent createScrollable(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #efefef; -fx-background-color: #efefef;");
        content.setStyle("-fx-background-color: #efefef;");

        return scrollPane;
    }

    private VBox item(String label, String value) {
        VBox box = new VBox(4);

        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #666;");

        Label l2 = new Label(value);

        box.getChildren().addAll(l1, l2);
        return box;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private void mostrarIndisponivel(String acao) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Função ainda não disponível");
        alert.setContentText(acao + " só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}