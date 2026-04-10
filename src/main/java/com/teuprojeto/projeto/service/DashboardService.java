package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.dashboard.DashboardDesignerResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardFuncionarioResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardGestorResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardRececionistaResponse;
import com.teuprojeto.projeto.dto.dashboard.DashboardResumoResponse;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.repository.ClienteRepository;
import com.teuprojeto.projeto.repository.DesignEncomendaRepository;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.FaturaRepository;
import com.teuprojeto.projeto.repository.GastoMaterialRepository;
import com.teuprojeto.projeto.repository.MaterialRepository;
import com.teuprojeto.projeto.repository.MovimentoFinanceiroRepository;
import com.teuprojeto.projeto.repository.ProducaoEncomendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final EncomendaRepository encomendaRepository;
    private final ClienteRepository clienteRepository;
    private final FaturaRepository faturaRepository;
    private final DesignEncomendaRepository designEncomendaRepository;
    private final ProducaoEncomendaRepository producaoEncomendaRepository;
    private final GastoMaterialRepository gastoMaterialRepository;
    private final MaterialRepository materialRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;
    private final FinanceiroService financeiroService;

    public DashboardService(
            EncomendaRepository encomendaRepository,
            ClienteRepository clienteRepository,
            FaturaRepository faturaRepository,
            DesignEncomendaRepository designEncomendaRepository,
            ProducaoEncomendaRepository producaoEncomendaRepository,
            GastoMaterialRepository gastoMaterialRepository,
            MaterialRepository materialRepository,
            MovimentoFinanceiroRepository movimentoFinanceiroRepository,
            FinanceiroService financeiroService
    ) {
        this.encomendaRepository = encomendaRepository;
        this.clienteRepository = clienteRepository;
        this.faturaRepository = faturaRepository;
        this.designEncomendaRepository = designEncomendaRepository;
        this.producaoEncomendaRepository = producaoEncomendaRepository;
        this.gastoMaterialRepository = gastoMaterialRepository;
        this.materialRepository = materialRepository;
        this.movimentoFinanceiroRepository = movimentoFinanceiroRepository;
        this.financeiroService = financeiroService;
    }

    public DashboardResumoResponse obterResumo() {
        List<Encomenda> encomendas = encomendaRepository.findAll();

        DashboardResumoResponse response = new DashboardResumoResponse();
        response.setTotalEncomendas(encomendas.size());
        response.setAguardaDesign(contarPorEstado(encomendas, 1L));
        response.setEmPreparacao(contarPorEstado(encomendas, 2L));
        response.setProntas(contarPorEstado(encomendas, 3L));
        response.setPagas(contarPorEstado(encomendas, 4L));

        return response;
    }

    public DashboardGestorResponse obterResumoGestor() {
        List<Encomenda> encomendas = encomendaRepository.findAll();
        List<Material> materiais = materialRepository.findAll();

        DashboardGestorResponse response = new DashboardGestorResponse();
        response.setTotalEncomendas(encomendas.size());
        response.setAguardaDesign(contarPorEstado(encomendas, 1L));
        response.setEmPreparacao(contarPorEstado(encomendas, 2L));
        response.setProntas(contarPorEstado(encomendas, 3L));
        response.setPagas(contarPorEstado(encomendas, 4L));
        response.setSaldoAtual(financeiroService.obterContaEmpresa().getSaldoAtual());
        response.setTotalMovimentos(movimentoFinanceiroRepository.count());
        response.setMateriaisAbaixoMinimo(
                materiais.stream()
                        .filter(m -> m.getStockAtual().compareTo(m.getStockMinimo()) < 0)
                        .count()
        );

        return response;
    }

    public DashboardRececionistaResponse obterResumoRececionista() {
        List<Encomenda> encomendas = encomendaRepository.findAll();

        DashboardRececionistaResponse response = new DashboardRececionistaResponse();
        response.setTotalClientes(clienteRepository.count());
        response.setTotalEncomendas(encomendas.size());
        response.setAguardaDesign(contarPorEstado(encomendas, 1L));
        response.setEmPreparacao(contarPorEstado(encomendas, 2L));
        response.setProntas(contarPorEstado(encomendas, 3L));
        response.setPagas(contarPorEstado(encomendas, 4L));
        response.setTotalFaturas(faturaRepository.count());

        return response;
    }

    public DashboardDesignerResponse obterResumoDesigner() {
        DashboardDesignerResponse response = new DashboardDesignerResponse();
        response.setTotalEncomendasComDesign(encomendaRepository.findByDesignTrue().size());
        response.setDesignsEnviadosCliente(designEncomendaRepository.findByEstadoDesign("ENVIADO_CLIENTE").size());
        response.setDesignsAprovadosCliente(designEncomendaRepository.findByEstadoDesign("APROVADO_CLIENTE").size());
        response.setDesignsRejeitadosCliente(designEncomendaRepository.findByEstadoDesign("REJEITADO_CLIENTE").size());

        return response;
    }

    public DashboardFuncionarioResponse obterResumoFuncionario() {
        List<Encomenda> encomendas = encomendaRepository.findAll();

        DashboardFuncionarioResponse response = new DashboardFuncionarioResponse();
        response.setEmPreparacao(contarPorEstado(encomendas, 2L));
        response.setProntas(contarPorEstado(encomendas, 3L));
        response.setTotalRegistosProducao(producaoEncomendaRepository.count());
        response.setTotalGastosMaterial(gastoMaterialRepository.count());

        return response;
    }

    private long contarPorEstado(List<Encomenda> encomendas, Long estado) {
        return encomendas.stream()
                .filter(e -> estado.equals(e.getIdestado()))
                .count();
    }
}