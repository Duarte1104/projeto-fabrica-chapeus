package com.teuprojeto.desktop.view.designer;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DesignerPedidosDesignPage {

    private final DesignerShellView shell;

    public DesignerPedidosDesignPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = DesignerUiFactory.createPageContainer("Pedidos de Design");

        TextField search = new TextField();
        search.setPromptText("Pesquisar pedido...");
        HBox.setHgrow(search, Priority.ALWAYS);

        HBox actions = new HBox(10, search);

        TableView<PedidoDesignRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PedidoDesignRow, String> codigo = new TableColumn<>("Encomenda");
        codigo.setCellValueFactory(c -> c.getValue().codigoEncomendaProperty());

        TableColumn<PedidoDesignRow, String> cliente = new TableColumn<>("Cliente");
        cliente.setCellValueFactory(c -> c.getValue().clienteProperty());

        TableColumn<PedidoDesignRow, String> produto = new TableColumn<>("Produto");
        produto.setCellValueFactory(c -> c.getValue().produtoProperty());

        TableColumn<PedidoDesignRow, Number> quantidade = new TableColumn<>("Qtd");
        quantidade.setCellValueFactory(c -> c.getValue().quantidadeProperty());

        TableColumn<PedidoDesignRow, String> data = new TableColumn<>("Data");
        data.setCellValueFactory(c -> c.getValue().dataProperty());

        TableColumn<PedidoDesignRow, String> descricao = new TableColumn<>("Pedido");
        descricao.setCellValueFactory(c -> c.getValue().descricaoPedidoProperty());

        TableColumn<PedidoDesignRow, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(c -> c.getValue().estadoDesignProperty());

        TableColumn<PedidoDesignRow, Void> acao = new TableColumn<>("Ações");
        acao.setCellFactory(col -> new TableCell<>() {
            private final Button abrirBtn = new Button("Criar");
            private final HBox box = new HBox(abrirBtn);

            {
                box.setAlignment(Pos.CENTER);
                abrirBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 6; -fx-border-radius: 6;");
                abrirBtn.setOnAction(e -> {
                    PedidoDesignRow pedido = getTableView().getItems().get(getIndex());
                    shell.setPedidoSelecionado(pedido);
                    shell.navigateTo(DesignerPage.CRIAR_PROPOSTA);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(codigo, cliente, produto, quantidade, data, descricao, estado, acao);

        FilteredList<PedidoDesignRow> filtrados = new FilteredList<>(
                MockDesignerData.getPedidos(),
                pedido -> pedido.getEstadoDesign().equals("AGUARDA_DESIGN")
        );

        search.textProperty().addListener((obs, oldValue, newValue) -> {
            String termo = newValue == null ? "" : newValue.trim().toLowerCase();

            filtrados.setPredicate(pedido -> {
                if (!pedido.getEstadoDesign().equals("AGUARDA_DESIGN")) {
                    return false;
                }

                if (termo.isBlank()) {
                    return true;
                }

                return pedido.getCodigoEncomenda().toLowerCase().contains(termo)
                        || pedido.getCliente().toLowerCase().contains(termo)
                        || pedido.getProduto().toLowerCase().contains(termo)
                        || pedido.getDescricaoPedido().toLowerCase().contains(termo);
            });
        });

        SortedList<PedidoDesignRow> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);

        VBox card = DesignerUiFactory.createCard();
        card.getChildren().addAll(actions, table);

        root.getChildren().add(card);
        return root;
    }
}