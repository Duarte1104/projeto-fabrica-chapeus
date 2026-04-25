package com.teuprojeto.desktop.service;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.LinhaEncomendaDto;
import com.teuprojeto.desktop.dto.ProducaoEncomendaDto;
import com.teuprojeto.desktop.view.funcionario.FuncionarioEncomendaRow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FuncionarioDataService {

    private final EncomendaApiService encomendaApiService;
    private final ClienteApiService clienteApiService;
    private final ProducaoApiService producaoApiService;

    public FuncionarioDataService() {
        this.encomendaApiService = new EncomendaApiService();
        this.clienteApiService = new ClienteApiService();
        this.producaoApiService = new ProducaoApiService();
    }

    public FuncionarioDashboardData carregarDashboard(Long idFuncionario) {
        List<FuncionarioEncomendaRow> minhas = construirRows(encomendaApiService.listarPorFuncionario(idFuncionario), true);
        List<FuncionarioEncomendaRow> disponiveis = construirRows(encomendaApiService.listarDisponiveisParaFuncionario(), false);
        return new FuncionarioDashboardData(minhas, disponiveis);
    }

    public List<FuncionarioEncomendaRow> carregarMinhasEncomendas(Long idFuncionario) {
        return construirRows(encomendaApiService.listarPorFuncionario(idFuncionario), true);
    }

    private List<FuncionarioEncomendaRow> construirRows(List<EncomendaDto> encomendas, boolean atribuida) {
        List<ClienteDto> clientes = clienteApiService.listarTodos();
        List<ChapeuDto> chapeus = encomendaApiService.listarChapeus();

        Map<Integer, String> nomesClientes = clientes.stream()
                .filter(c -> c.getCod() != null)
                .collect(Collectors.toMap(
                        ClienteDto::getCod,
                        c -> c.getNome() == null ? "Cliente #" + c.getCod() : c.getNome(),
                        (a, b) -> a
                ));

        Map<Long, String> nomesChapeus = chapeus.stream()
                .filter(c -> c.getCod() != null)
                .collect(Collectors.toMap(
                        ChapeuDto::getCod,
                        c -> c.getNome() == null ? "Chapéu #" + c.getCod() : c.getNome(),
                        (a, b) -> a
                ));

        return encomendas.stream()
                .map(encomenda -> construirRow(encomenda, atribuida, nomesClientes, nomesChapeus))
                .sorted(Comparator.comparing(FuncionarioEncomendaRow::getIdEncomenda).reversed())
                .toList();
    }

    private FuncionarioEncomendaRow construirRow(EncomendaDto encomenda,
                                                 boolean atribuida,
                                                 Map<Integer, String> nomesClientes,
                                                 Map<Long, String> nomesChapeus) {

        List<LinhaEncomendaDto> linhas = listarLinhasSemFalhar(encomenda.getNum());
        ProducaoEncomendaDto producao = producaoApiService.procurarPorEncomendaOuNull(encomenda.getNum());

        String produto = construirResumoProduto(linhas, nomesChapeus);
        long quantidadeTotal = linhas.stream()
                .mapToLong(l -> l.getQuantidade() == null ? 0 : l.getQuantidade())
                .sum();

        boolean precisaPersonalizacao = Boolean.TRUE.equals(encomenda.getDesign());

        boolean montagem = producao != null && Boolean.TRUE.equals(producao.getMontagemConcluida());
        boolean costuras = producao != null && Boolean.TRUE.equals(producao.getCosturasConcluidas());
        boolean personalizacao = producao != null && Boolean.TRUE.equals(producao.getPersonalizacaoConcluida());
        boolean concluida = (producao != null && Boolean.TRUE.equals(producao.getConcluida()))
                || (encomenda.getIdestado() != null && encomenda.getIdestado() >= 3);

        return new FuncionarioEncomendaRow(
                encomenda.getNum(),
                "ENC-" + encomenda.getNum(),
                produto,
                nomesClientes.getOrDefault(encomenda.getIdcliente(), "Cliente #" + encomenda.getIdcliente()),
                quantidadeTotal,
                calcularPrioridade(encomenda.getDataEntrega()),
                mapearEstado(encomenda.getIdestado()),
                valorOuTraco(encomenda.getDataEntrega()),
                montagem,
                costuras,
                personalizacao,
                precisaPersonalizacao,
                producao == null ? "" : valorOuVazio(producao.getMontagemComentario()),
                producao == null ? "" : valorOuVazio(producao.getCosturasComentario()),
                producao == null ? "" : valorOuVazio(producao.getPersonalizacaoComentario()),
                valorOuVazio(encomenda.getObservacoes()),
                atribuida,
                concluida
        );
    }

    private List<LinhaEncomendaDto> listarLinhasSemFalhar(Long idEncomenda) {
        try {
            return encomendaApiService.listarLinhas(idEncomenda);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String construirResumoProduto(List<LinhaEncomendaDto> linhas, Map<Long, String> nomesChapeus) {
        if (linhas == null || linhas.isEmpty()) {
            return "Sem linhas";
        }

        if (linhas.size() == 1) {
            LinhaEncomendaDto linha = linhas.get(0);
            return nomesChapeus.getOrDefault(linha.getCodchapeu(), "Chapéu #" + linha.getCodchapeu());
        }

        LinhaEncomendaDto primeira = linhas.get(0);
        String primeiroNome = nomesChapeus.getOrDefault(primeira.getCodchapeu(), "Chapéu #" + primeira.getCodchapeu());
        return primeiroNome + " +" + (linhas.size() - 1) + " tipo(s)";
    }

    private String calcularPrioridade(String dataEntrega) {
        if (dataEntrega == null || dataEntrega.isBlank()) {
            return "Normal";
        }

        try {
            LocalDate entrega = LocalDate.parse(dataEntrega);
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), entrega);

            if (dias <= 2) {
                return "Alta";
            }
        } catch (Exception ignored) {
        }

        return "Normal";
    }

    private String valorOuTraco(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String valorOuVazio(String value) {
        return value == null ? "" : value;
    }

    private String mapearEstado(Long idestado) {
        if (idestado == null) {
            return "SEM_ESTADO";
        }

        return switch (idestado.intValue()) {
            case 1 -> "AGUARDA_DESIGN";
            case 2 -> "PREPARACAO";
            case 3 -> "PRONTA";
            case 4 -> "PAGA";
            default -> "ESTADO_" + idestado;
        };
    }

    public record FuncionarioDashboardData(
            List<FuncionarioEncomendaRow> minhasEncomendas,
            List<FuncionarioEncomendaRow> encomendasDisponiveis
    ) {
    }
}