package com.teuprojeto.desktop.view.rececionista;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class RececionistaFaturasPage {

    private final RececionistaShellView shell;

    public RececionistaFaturasPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Faturas");

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
        table.setItems(FXCollections.observableArrayList(
                new FaturaRow("FT-001", "ENC-1232", "89.90 €", "10/04/2026"),
                new FaturaRow("FT-002", "ENC-1110", "487.90 €", "10/04/2026")
        ));

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().add(table);

        root.getChildren().add(card);
        return root;
    }
}