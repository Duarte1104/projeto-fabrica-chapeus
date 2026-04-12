package com.teuprojeto.desktop.view.designer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class MockDesignerData {

    private static final ObservableList<PedidoDesignRow> PEDIDOS = FXCollections.observableArrayList(
            new PedidoDesignRow(
                    "ENC-101",
                    "João Santos",
                    "Chapéu Clássico",
                    3,
                    "20/03/2026",
                    "Logótipo minimalista bordado a azul escuro.",
                    "AGUARDA_DESIGN",
                    "Cliente quer algo elegante."
            ),
            new PedidoDesignRow(
                    "ENC-102",
                    "Ana Costa",
                    "Chapéu Elegante",
                    10,
                    "22/03/2026",
                    "Design com iniciais douradas.",
                    "AGUARDA_DESIGN",
                    "Usar tons neutros."
            ),
            new PedidoDesignRow(
                    "ENC-103",
                    "Pedro Lima",
                    "Chapéu Desportivo",
                    12,
                    "24/03/2026",
                    "Proposta enviada com símbolo lateral.",
                    "ENVIADO_CLIENTE",
                    "A aguardar resposta."
            ),
            new PedidoDesignRow(
                    "ENC-104",
                    "Marta Silva",
                    "Chapéu Vintage",
                    5,
                    "25/03/2026",
                    "Design floral aprovado pelo cliente.",
                    "APROVADO_CLIENTE",
                    "Seguir para preparação."
            ),
            new PedidoDesignRow(
                    "ENC-105",
                    "Carlos Ferreira",
                    "Chapéu Premium",
                    8,
                    "27/03/2026",
                    "Cliente rejeitou a primeira proposta.",
                    "REJEITADO_CLIENTE",
                    "Criar nova versão futuramente."
            )
    );

    private MockDesignerData() {
    }

    public static ObservableList<PedidoDesignRow> getPedidos() {
        return PEDIDOS;
    }

    public static long totalPendentes() {
        return PEDIDOS.stream()
                .filter(p -> p.getEstadoDesign().equals("AGUARDA_DESIGN"))
                .count();
    }

    public static long totalEnviados() {
        return PEDIDOS.stream()
                .filter(p -> p.getEstadoDesign().equals("ENVIADO_CLIENTE"))
                .count();
    }

    public static long totalAprovados() {
        return PEDIDOS.stream()
                .filter(p -> p.getEstadoDesign().equals("APROVADO_CLIENTE"))
                .count();
    }

    public static long totalRejeitados() {
        return PEDIDOS.stream()
                .filter(p -> p.getEstadoDesign().equals("REJEITADO_CLIENTE"))
                .count();
    }
}