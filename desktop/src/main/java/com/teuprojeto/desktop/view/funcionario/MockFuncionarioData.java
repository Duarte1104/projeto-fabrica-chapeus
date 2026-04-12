package com.teuprojeto.desktop.view.funcionario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class MockFuncionarioData {

    private static final ObservableList<FuncionarioEncomendaRow> ENCOMENDAS = FXCollections.observableArrayList(
            new FuncionarioEncomendaRow(
                    "OP-1001",
                    "ENC-1234",
                    "Fedora Clássico",
                    "João Santos",
                    20,
                    15,
                    "Alta",
                    "EM_PRODUCAO",
                    "20/03/2026",
                    true,
                    true,
                    false,
                    "Terminar personalização azul-marinho."
            ),
            new FuncionarioEncomendaRow(
                    "OP-1005",
                    "ENC-1238",
                    "Fedora Vintage",
                    "Carlos Ferreira",
                    30,
                    8,
                    "Alta",
                    "EM_PRODUCAO",
                    "22/03/2026",
                    true,
                    false,
                    false,
                    "Cliente pediu acabamento castanho escuro."
            ),
            new FuncionarioEncomendaRow(
                    "OP-1007",
                    "ENC-1240",
                    "Boné Sport",
                    "Ana Costa",
                    25,
                    25,
                    "Normal",
                    "CONCLUIDA",
                    "18/03/2026",
                    true,
                    true,
                    true,
                    "Ordem já concluída."
            )
    );

    private static final List<String> MATERIAIS = List.of(
            "Linho",
            "Couro",
            "Fita Decorativa",
            "Linha Azul",
            "Caixa Embalagem"
    );

    private MockFuncionarioData() {
    }

    public static ObservableList<FuncionarioEncomendaRow> getEncomendas() {
        return ENCOMENDAS;
    }

    public static long totalAtribuido() {
        return ENCOMENDAS.size();
    }

    public static int totalUnidadesConcluidas() {
        return ENCOMENDAS.stream()
                .mapToInt(FuncionarioEncomendaRow::getUnidadesConcluidas)
                .sum();
    }

    public static int totalUnidades() {
        return ENCOMENDAS.stream()
                .mapToInt(FuncionarioEncomendaRow::getQuantidadeTotal)
                .sum();
    }

    public static int progressoMedioPercent() {
        if (ENCOMENDAS.isEmpty()) {
            return 0;
        }

        double media = ENCOMENDAS.stream()
                .mapToInt(FuncionarioEncomendaRow::getProgressoPercent)
                .average()
                .orElse(0);

        return (int) Math.round(media);
    }

    public static List<String> getMateriais() {
        return MATERIAIS;
    }
}