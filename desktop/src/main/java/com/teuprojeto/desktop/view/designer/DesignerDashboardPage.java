package com.teuprojeto.desktop.view.designer;

import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.service.DesignApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class DesignerDashboardPage {

    private final DesignerShellView shell;
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();
    private final DesignApiService designApiService = new DesignApiService();

    public DesignerDashboardPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = pageContainer("Dashboard");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #666666;");

        Label pendentes = statValue("-");
        Label enviados = statValue("-");
        Label aprovados = statValue("-");
        Label rejeitados = statValue("-");

        HBox linha1 = new HBox(16,
                statCard("Pedidos Pendentes", pendentes),
                statCard("Enviados ao Cliente", enviados),
                statCard("Aprovados", aprovados),
                statCard("Rejeitados", rejeitados)
        );

        VBox quickActions = card();
        Label qaTitle = sectionTitle("Ações rápidas");

        HBox botoes = new HBox(12);
        Button pedidosBtn = primaryButton("Pedidos de Design");
        pedidosBtn.setOnAction(e -> shell.navigateTo(DesignerPage.PEDIDOS_DESIGN));

        Button historicoBtn = secondaryButton("Histórico");
        historicoBtn.setOnAction(e -> shell.navigateTo(DesignerPage.HISTORICO));

        Button atualizarBtn = secondaryButton("Atualizar");
        atualizarBtn.setOnAction(e -> carregarDashboard(estado, pendentes, enviados, aprovados, rejeitados));

        botoes.getChildren().addAll(pedidosBtn, historicoBtn, atualizarBtn);
        quickActions.getChildren().addAll(qaTitle, botoes);

        VBox resumo = card();
        Label resumoTitle = sectionTitle("Resumo");
        Label resumo1 = new Label();
        Label resumo2 = new Label();
        resumo1.setStyle("-fx-font-size: 15;");
        resumo2.setStyle("-fx-font-size: 15;");
        resumo.getChildren().addAll(resumoTitle, resumo1, resumo2);

        root.getChildren().addAll(estado, linha1, quickActions, resumo);

        Task<DashboardData> task = new Task<>() {
            @Override
            protected DashboardData call() {
                List<EncomendaDto> encomendas = encomendaApiService.listarEncomendas();
                List<DesignEncomendaDto> designs = designApiService.listarTodos();
                return new DashboardData(encomendas, designs);
            }
        };

        task.setOnSucceeded(event -> {
            DashboardData data = task.getValue();

            long totalPendentes = data.encomendas().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getDesign()) && Long.valueOf(1L).equals(e.getIdestado()))
                    .count();

            long totalEnviados = data.designs().stream()
                    .filter(d -> "ENVIADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            long totalAprovados = data.designs().stream()
                    .filter(d -> "APROVADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            long totalRejeitados = data.designs().stream()
                    .filter(d -> "REJEITADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                    .count();

            pendentes.setText(String.valueOf(totalPendentes));
            enviados.setText(String.valueOf(totalEnviados));
            aprovados.setText(String.valueOf(totalAprovados));
            rejeitados.setText(String.valueOf(totalRejeitados));

            resumo1.setText("Encomendas à espera de proposta: " + totalPendentes);
            resumo2.setText("Designs enviados aguardam decisão fictícia do cliente.");

            estado.setText("Dashboard atualizado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar dashboard.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return root;
    }

    private void carregarDashboard(Label estado, Label pendentes, Label enviados, Label aprovados, Label rejeitados) {
        estado.setText("Atualiza a página para ver os dados mais recentes.");
    }

    private record DashboardData(List<EncomendaDto> encomendas, List<DesignEncomendaDto> designs) {
    }

    private VBox pageContainer(String titleText) {
        VBox root = new VBox(18);
        root.setStyle("-fx-padding: 28; -fx-background-color: #efefef;");
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");
        root.getChildren().add(title);
        return root;
    }

    private VBox card() {
        VBox box = new VBox(12);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 22; -fx-border-color: #e0e0e0; -fx-border-radius: 12;");
        return box;
    }

    private VBox statCard(String label, Label value) {
        VBox card = card();
        card.setPrefWidth(220);
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #666666; -fx-font-size: 14;");
        card.getChildren().addAll(labelNode, value);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label statValue(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");
        return label;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold;");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #cccccc; -fx-background-radius: 10; -fx-border-radius: 10;");
        return button;
    }
}