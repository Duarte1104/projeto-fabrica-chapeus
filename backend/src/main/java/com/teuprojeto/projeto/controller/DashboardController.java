package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.dashboard.DashboardDesignerResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardFuncionarioResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardGestorResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardRececionistaResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardResumoResponse;
import com.teuprojeto.projeto.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    public DashboardResumoResponse obterResumo() {
        return dashboardService.obterResumo();
    }

    @GetMapping("/gestor")
    public DashboardGestorResponse obterResumoGestor() {
        return dashboardService.obterResumoGestor();
    }

    @GetMapping("/rececionista")
    public DashboardRececionistaResponse obterResumoRececionista() {
        return dashboardService.obterResumoRececionista();
    }

    @GetMapping("/designer")
    public DashboardDesignerResponse obterResumoDesigner() {
        return dashboardService.obterResumoDesigner();
    }

    @GetMapping("/funcionario")
    public DashboardFuncionarioResponse obterResumoFuncionario() {
        return dashboardService.obterResumoFuncionario();
    }
}