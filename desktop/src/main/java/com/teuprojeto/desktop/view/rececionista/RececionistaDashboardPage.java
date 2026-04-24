package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.DashboardRececionistaResponseDto;
import com.teuprojeto.desktop.service.DashboardApiService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RececionistaDashboardPage {

    private final RececionistaShellView shell;
    private final DashboardApiService dashboardApiService = new DashboardApiService();

    public RececionistaDashboardPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Dashboard");

        Label estado = new Label("A carregar dashboard...");
        estado.setStyle("-fx-text-fill: #666666;");

        HBox linha1 = new HBox(16);
        Label totalClientes = statValue("-");
        Label totalEncomendas = statValue("-");
        Label prontas = statValue("-");
        Label pagas = statValue("-");

        linha1.getChildren().addAll(
                createStatCard("Clientes", totalClientes),
                createStatCard("Encomendas", totalEncomendas),
                createStatCard("Prontas", prontas),
                createStatCard("Pagas", pagas)
        );

        HBox linha2 = new HBox(16);
        Label aguardaDesign = statValue("-");
        Label emPreparacao = statValue("-");
        Label totalFaturas = statValue("-");

        linha2.getChildren().addAll(
                createStatCard("Aguarda Design", aguardaDesign),
                createStatCard("Em Preparação", emPreparacao),
                createStatCard("Faturas", totalFaturas)
        );

        VBox quickActions = RececionistaUiFactory.createCard();
        Label qaTitle = new Label("Ações rápidas");
        qaTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox buttons = new HBox(12);

        Button btnClientes = RececionistaUiFactory.primaryButton("Listar Clientes");
        btnClientes.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        Button btnCriarCliente = RececionistaUiFactory.secondaryButton("Criar Cliente");
        btnCriarCliente.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_CRIAR));

        Button btnCriarEncomenda = RececionistaUiFactory.secondaryButton("Criar Encomenda");
        btnCriarEncomenda.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        Button btnFaturas = RececionistaUiFactory.secondaryButton("Ver Faturas");
        btnFaturas.setOnAction(e -> shell.navigateTo(RececionistaPage.FATURAS));

        Button btnAtualizar = RececionistaUiFactory.secondaryButton("Atualizar");
        btnAtualizar.setOnAction(e -> carregarDashboard(
                estado,
                totalClientes,
                totalEncomendas,
                aguardaDesign,
                emPreparacao,
                prontas,
                pagas,
                totalFaturas
        ));

        buttons.getChildren().addAll(btnClientes, btnCriarCliente, btnCriarEncomenda, btnFaturas, btnAtualizar);
        quickActions.getChildren().addAll(qaTitle, buttons);

        VBox resumo = RececionistaUiFactory.createCard();
        Label resumoTitle = new Label("Resumo");
        resumoTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label linhaResumo1 = new Label();
        linhaResumo1.setStyle("-fx-font-size: 15; -fx-text-fill: #333333;");

        Label linhaResumo2 = new Label();
        linhaResumo2.setStyle("-fx-font-size: 15; -fx-text-fill: #333333;");

        resumo.getChildren().addAll(resumoTitle, linhaResumo1, linhaResumo2);

        root.getChildren().addAll(estado, linha1, linha2, quickActions, resumo);

        carregarDashboard(
                estado,
                totalClientes,
                totalEncomendas,
                aguardaDesign,
                emPreparacao,
                prontas,
                pagas,
                totalFaturas,
                linhaResumo1,
                linhaResumo2
        );

        return root;
    }

    private void carregarDashboard(Label estado,
                                   Label totalClientes,
                                   Label totalEncomendas,
                                   Label aguardaDesign,
                                   Label emPreparacao,
                                   Label prontas,
                                   Label pagas,
                                   Label totalFaturas) {
        carregarDashboard(
                estado,
                totalClientes,
                totalEncomendas,
                aguardaDesign,
                emPreparacao,
                prontas,
                pagas,
                totalFaturas,
                null,
                null
        );
    }

    private void carregarDashboard(Label estado,
                                   Label totalClientes,
                                   Label totalEncomendas,
                                   Label aguardaDesign,
                                   Label emPreparacao,
                                   Label prontas,
                                   Label pagas,
                                   Label totalFaturas,
                                   Label linhaResumo1,
                                   Label linhaResumo2) {

        estado.setText("A carregar dashboard...");

        Task<DashboardRececionistaResponseDto> task = new Task<>() {
            @Override
            protected DashboardRececionistaResponseDto call() {
                return dashboardApiService.obterDashboardRececionista();
            }
        };

        task.setOnSucceeded(event -> {
            DashboardRececionistaResponseDto dados = task.getValue();

            totalClientes.setText(String.valueOf(dados.getTotalClientes()));
            totalEncomendas.setText(String.valueOf(dados.getTotalEncomendas()));
            aguardaDesign.setText(String.valueOf(dados.getAguardaDesign()));
            emPreparacao.setText(String.valueOf(dados.getEmPreparacao()));
            prontas.setText(String.valueOf(dados.getProntas()));
            pagas.setText(String.valueOf(dados.getPagas()));
            totalFaturas.setText(String.valueOf(dados.getTotalFaturas()));

            if (linhaResumo1 != null) {
                linhaResumo1.setText("Encomendas prontas a tratar: " + dados.getProntas());
            }

            if (linhaResumo2 != null) {
                linhaResumo2.setText("Encomendas com design pendente: " + dados.getAguardaDesign());
            }

            estado.setText("Dashboard atualizado.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar dashboard.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao obter dashboard");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox createStatCard(String label, Label valueLabel) {
        VBox card = RececionistaUiFactory.createCard();
        card.setPrefWidth(220);

        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        card.getChildren().addAll(l1, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label statValue(String value) {
        Label label = new Label(value);
        label.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");
        return label;
    }
}