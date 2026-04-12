package com.teuprojeto.desktop.view.gestor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class MockGestorData {

    private static final ObservableList<MaterialRow> MATERIAIS = FXCollections.observableArrayList(
            new MaterialRow(1, "Linho", 250, 80, "m", 6.50),
            new MaterialRow(2, "Couro", 48, 60, "m²", 14.20),
            new MaterialRow(3, "Fita Decorativa", 120, 40, "un", 1.10),
            new MaterialRow(4, "Caixa Embalagem", 35, 30, "un", 0.85)
    );

    private static final ObservableList<DespesaRow> DESPESAS = FXCollections.observableArrayList(
            new DespesaRow("DESP-121", "20/03/2026", "Linho", "250 metros", "TextiPro", "1300.00"),
            new DespesaRow("DESP-122", "22/03/2026", "Couro", "30 m²", "Couros SA", "426.00"),
            new DespesaRow("DESP-123", "24/03/2026", "Fita Decorativa", "100 unidades", "DecorTex", "110.00")
    );

    private MockGestorData() {
    }

    public static ObservableList<MaterialRow> getMateriais() {
        return MATERIAIS;
    }

    public static ObservableList<DespesaRow> getDespesas() {
        return DESPESAS;
    }

    public static double totalDespesas() {
        return DESPESAS.stream()
                .mapToDouble(d -> Double.parseDouble(d.getValor()))
                .sum();
    }
}