package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.encomenda.CriarEncomendaRequest;
import com.teuprojeto.projeto.dto.encomenda.LinhaEncomendaRequest;
import com.teuprojeto.projeto.entity.ChapeuMaterial;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.GastoMaterial;
import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.entity.Tamanho;
import com.teuprojeto.projeto.repository.ChapeuMaterialRepository;
import com.teuprojeto.projeto.repository.ClienteRepository;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.FuncionarioRepository;
import com.teuprojeto.projeto.repository.GastoMaterialRepository;
import com.teuprojeto.projeto.repository.LinhaEncomendaRepository;
import com.teuprojeto.projeto.repository.MaterialRepository;
import com.teuprojeto.projeto.repository.TamanhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final LinhaEncomendaRepository linhaEncomendaRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ChapeuMaterialRepository chapeuMaterialRepository;
    private final MaterialRepository materialRepository;
    private final TamanhoRepository tamanhoRepository;
    private final GastoMaterialRepository gastoMaterialRepository;

    public EncomendaService(
            EncomendaRepository encomendaRepository,
            LinhaEncomendaRepository linhaEncomendaRepository,
            ClienteRepository clienteRepository,
            FuncionarioRepository funcionarioRepository,
            ChapeuMaterialRepository chapeuMaterialRepository,
            MaterialRepository materialRepository,
            TamanhoRepository tamanhoRepository,
            GastoMaterialRepository gastoMaterialRepository
    ) {
        this.encomendaRepository = encomendaRepository;
        this.linhaEncomendaRepository = linhaEncomendaRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.chapeuMaterialRepository = chapeuMaterialRepository;
        this.materialRepository = materialRepository;
        this.tamanhoRepository = tamanhoRepository;
        this.gastoMaterialRepository = gastoMaterialRepository;
    }

    @Transactional
    public Encomenda criar(CriarEncomendaRequest request) {

        if (!clienteRepository.existsById(request.getIdCliente())) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        if (request.getLinhas() == null || request.getLinhas().isEmpty()) {
            throw new IllegalArgumentException("A encomenda deve ter pelo menos uma linha.");
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (LinhaEncomendaRequest linha : request.getLinhas()) {
            validarLinha(linha);

            BigDecimal subtotal = linha.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(linha.getQuantidade()));

            valorTotal = valorTotal.add(subtotal);
        }

        Encomenda encomenda = new Encomenda();
        encomenda.setData(LocalDate.now());
        encomenda.setHora(LocalTime.now());
        encomenda.setDataEntrega(request.getDataEntrega());
        encomenda.setObservacoes(request.getObservacoes());
        encomenda.setIdcliente(request.getIdCliente());
        encomenda.setValortotal(valorTotal);
        encomenda.setDesign(request.getDesign());
        encomenda.setDescricaoDesign(request.getDescricaoDesign());
        encomenda.setIdfuncionario(null);

        if (Boolean.TRUE.equals(request.getDesign())) {
            encomenda.setIdestado(1L);
        } else {
            encomenda.setIdestado(2L);
        }

        Encomenda encomendaGuardada = encomendaRepository.save(encomenda);

        for (LinhaEncomendaRequest linhaRequest : request.getLinhas()) {
            LinhaEncomenda linha = new LinhaEncomenda();
            linha.setNumencomenda(encomendaGuardada.getNum());
            linha.setCodchapeu(linhaRequest.getCodChapeu());
            linha.setQuantidade(linhaRequest.getQuantidade());
            linha.setTamanho(linhaRequest.getTamanho().trim());
            linha.setCores(linhaRequest.getCores().trim());

            linhaEncomendaRepository.save(linha);

            descontarMateriaisDaLinha(encomendaGuardada, linhaRequest);
        }

        return encomendaGuardada;
    }

    private void validarLinha(LinhaEncomendaRequest linha) {
        if (linha.getCodChapeu() == null) {
            throw new IllegalArgumentException("O chapéu é obrigatório em todas as linhas.");
        }

        if (linha.getQuantidade() == null || linha.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
        }

        if (linha.getPrecoUnitario() == null) {
            throw new IllegalArgumentException("O preço unitário é obrigatório.");
        }

        if (linha.getTamanho() == null || linha.getTamanho().isBlank()) {
            throw new IllegalArgumentException("O tamanho é obrigatório em todas as linhas.");
        }

        if (linha.getCores() == null || linha.getCores().isBlank()) {
            throw new IllegalArgumentException("Indique pelo menos uma cor em todas as linhas.");
        }
    }

    private void descontarMateriaisDaLinha(Encomenda encomenda, LinhaEncomendaRequest linhaRequest) {
        List<ChapeuMaterial> materiaisDoChapeu =
                chapeuMaterialRepository.findByIdChapeu(linhaRequest.getCodChapeu());

        if (materiaisDoChapeu.isEmpty()) {
            return;
        }

        Tamanho tamanho = tamanhoRepository.findById(linhaRequest.getTamanho().trim())
                .orElseThrow(() -> new IllegalArgumentException("Tamanho inválido: " + linhaRequest.getTamanho()));

        BigDecimal multiplicador = tamanho.getMultiplicador();

        for (ChapeuMaterial chapeuMaterial : materiaisDoChapeu) {
            Material material = materialRepository.findById(chapeuMaterial.getIdMaterial())
                    .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));

            BigDecimal quantidadeNecessaria = chapeuMaterial.getQuantidadePorUnidade()
                    .multiply(BigDecimal.valueOf(linhaRequest.getQuantidade()))
                    .multiply(multiplicador);

            material.setStockAtual(material.getStockAtual().subtract(quantidadeNecessaria));
            materialRepository.save(material);

            GastoMaterial gasto = new GastoMaterial();
            gasto.setIdEncomenda(BigDecimal.valueOf(encomenda.getNum()));
            gasto.setIdMaterial(material.getId());
            gasto.setMaterial(material.getNome());
            gasto.setQuantidade(quantidadeNecessaria);
            gasto.setObservacoes(
                    "Gerado automaticamente pela criação da encomenda. " +
                            "Chapéu #" + linhaRequest.getCodChapeu() +
                            ", tamanho " + linhaRequest.getTamanho() +
                            ", cores: " + linhaRequest.getCores()
            );

            gastoMaterialRepository.save(gasto);
        }
    }

    public List<Encomenda> listarTodas() {
        return encomendaRepository.findAll();
    }

    public List<Encomenda> listarComDesign() {
        return encomendaRepository.findByDesignTrue();
    }

    public List<Encomenda> listarPorCliente(Integer idCliente) {
        return encomendaRepository.findByIdcliente(idCliente);
    }

    public List<Encomenda> listarDisponiveisParaFuncionario() {
        return encomendaRepository.findByIdfuncionarioIsNullAndIdestado(2L);
    }

    public List<Encomenda> listarPorFuncionario(Long idFuncionario) {
        return encomendaRepository.findByIdfuncionario(idFuncionario);
    }

    public Encomenda procurarPorId(Long id) {
        return encomendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));
    }

    public List<LinhaEncomenda> listarLinhas(Long idEncomenda) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        return linhaEncomendaRepository.findByNumencomenda(encomenda.getNum());
    }

    @Transactional
    public void apagar(Long id) {
        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        linhaEncomendaRepository.deleteByNumencomenda(encomenda.getNum());
        encomendaRepository.delete(encomenda);
    }

    @Transactional
    public Encomenda mudarEstado(Long idEncomenda, Long idNovoEstado) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        encomenda.setIdestado(idNovoEstado);
        return encomendaRepository.save(encomenda);
    }

    @Transactional
    public Encomenda aceitarEncomenda(Long idEncomenda, Long idFuncionario) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (encomenda.getIdfuncionario() != null) {
            throw new IllegalArgumentException("Esta encomenda já foi atribuída a um funcionário.");
        }

        if (!Long.valueOf(2L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("Só é possível aceitar encomendas em preparação.");
        }

        encomenda.setIdfuncionario(idFuncionario);
        return encomendaRepository.save(encomenda);
    }
}