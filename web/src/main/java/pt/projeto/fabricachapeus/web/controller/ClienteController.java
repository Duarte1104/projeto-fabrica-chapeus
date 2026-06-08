package pt.projeto.fabricachapeus.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.projeto.fabricachapeus.web.dto.*;
import pt.projeto.fabricachapeus.web.service.BackendAuthService;
import java.math.BigDecimal;

import java.util.*;

@Controller
public class ClienteController {

    private final BackendAuthService backendAuthService;

    public ClienteController(BackendAuthService backendAuthService) {
        this.backendAuthService = backendAuthService;
    }

    @GetMapping("/cliente")
    public String dashboard(HttpSession session, Model model) {
        return protegerPagina(session, model, "cliente");
    }

    @GetMapping("/catalogo")
    public String catalogo(HttpSession session, Model model) {
        String pagina = protegerPagina(session, model, "catalogo");

        if ("catalogo".equals(pagina)) {
            model.addAttribute("chapeus", backendAuthService.listarChapeus());
        }

        return pagina;
    }

    @GetMapping("/nova-encomenda")
    public String novaEncomenda(HttpSession session, Model model) {
        String pagina = protegerPagina(session, model, "nova-encomenda");

        if ("nova-encomenda".equals(pagina)) {
            model.addAttribute("chapeus", backendAuthService.listarChapeus());
        }

        return pagina;
    }

    @PostMapping("/nova-encomenda")
    public String criarEncomenda(
            HttpSession session,
            Model model,
            @RequestParam("codChapeu") List<String> codChapeus,
            @RequestParam("quantidade") List<String> quantidades,
            @RequestParam("tamanho") List<String> tamanhos,
            @RequestParam("cores") List<String> cores,
            String dataEntrega,
            String observacoes,
            String design,
            String descricaoDesign
    ) {
        Object clienteId = session.getAttribute("clienteId");

        if (clienteId == null) {
            return "redirect:/login";
        }

        adicionarDadosSessaoAoModel(session, model);

        try {
            if (isBlank(dataEntrega)) {
                throw new IllegalArgumentException("Indica a data pretendida de entrega.");
            }

            if (codChapeus == null || codChapeus.isEmpty()) {
                throw new IllegalArgumentException("Adiciona pelo menos um chapéu à encomenda.");
            }

            if (codChapeus.size() != quantidades.size()
                    || codChapeus.size() != tamanhos.size()
                    || codChapeus.size() != cores.size()) {
                throw new IllegalArgumentException("Existem linhas incompletas na encomenda.");
            }

            List<ChapeuDto> chapeus = backendAuthService.listarChapeus();
            List<LinhaEncomendaWebRequest> linhas = new ArrayList<>();

            for (int i = 0; i < codChapeus.size(); i++) {
                String codChapeu = codChapeus.get(i);
                String quantidade = quantidades.get(i);
                String tamanho = tamanhos.get(i);
                String cor = cores.get(i);

                if (isBlank(codChapeu) || isBlank(quantidade) || isBlank(tamanho) || isBlank(cor)) {
                    throw new IllegalArgumentException("Preenche todos os campos das linhas da encomenda.");
                }

                ChapeuDto chapeuSelecionado = chapeus.stream()
                        .filter(c -> c.getCod() != null && c.getCod().equals(Long.valueOf(codChapeu)))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Chapéu inválido."));

                Long qtd = Long.valueOf(quantidade);

                if (qtd <= 0) {
                    throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
                }

                LinhaEncomendaWebRequest linha = new LinhaEncomendaWebRequest();
                linha.setCodChapeu(chapeuSelecionado.getCod());
                linha.setQuantidade(qtd);
                linha.setPrecoUnitario(chapeuSelecionado.getPrecoactvenda());
                linha.setTamanho(tamanho.trim());
                linha.setCores(cor.trim());

                linhas.add(linha);
            }

            CriarEncomendaWebRequest request = new CriarEncomendaWebRequest();
            request.setIdCliente(Integer.valueOf(clienteId.toString()));
            request.setDataEntrega(dataEntrega);
            request.setObservacoes(isBlank(observacoes) ? null : observacoes.trim());
            request.setDesign("on".equalsIgnoreCase(design));
            request.setDescricaoDesign("on".equalsIgnoreCase(design) ? descricaoDesign : null);
            request.setLinhas(linhas);

            backendAuthService.criarEncomenda(request);

            return "redirect:/encomendas?sucesso=1";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("chapeus", backendAuthService.listarChapeus());
            return "nova-encomenda";
        }
    }

    @GetMapping("/encomendas")
    public String encomendas(HttpSession session, Model model, String sucesso) {
        String pagina = protegerPagina(session, model, "encomendas");

        if (!"encomendas".equals(pagina)) {
            return pagina;
        }

        Object clienteIdObj = session.getAttribute("clienteId");

        if (clienteIdObj == null) {
            return "redirect:/login";
        }

        Integer clienteId = Integer.valueOf(clienteIdObj.toString());

        List<EncomendaDto> encomendas = backendAuthService.listarEncomendasCliente(clienteId);
        List<Map<String, Object>> encomendasDetalhadas = new ArrayList<>();

        for (EncomendaDto encomenda : encomendas) {
            Map<String, Object> dados = new HashMap<>();

            dados.put("encomenda", encomenda);
            dados.put("estadoTexto", estadoTexto(encomenda.getIdestado()));
            dados.put("linhas", backendAuthService.listarLinhasEncomenda(encomenda.getNum()));

            List<DesignEncomendaDto> designs = backendAuthService.listarDesignsDaEncomenda(encomenda.getNum());
            List<Map<String, Object>> designsDetalhados = new ArrayList<>();

            for (DesignEncomendaDto designDto : designs) {
                Map<String, Object> designMap = new HashMap<>();
                designMap.put("design", designDto);
                designMap.put("imagens", backendAuthService.listarImagensDesign(designDto.getId()));
                designsDetalhados.add(designMap);
            }

            dados.put("designs", designsDetalhados);
            encomendasDetalhadas.add(dados);
        }

        model.addAttribute("encomendas", encomendasDetalhadas);

        if (sucesso != null) {
            model.addAttribute("sucesso", "Encomenda criada com sucesso.");
        }

        return "encomendas";
    }

    @PostMapping("/designs/{id}/aprovar")
    public String aprovarDesign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            backendAuthService.aprovarDesign(id);
            redirectAttributes.addFlashAttribute("sucesso", "Design aprovado com sucesso.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/encomendas";
    }

    @PostMapping("/designs/{id}/rejeitar")
    public String rejeitarDesign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            backendAuthService.rejeitarDesign(id);
            redirectAttributes.addFlashAttribute("sucesso", "Design rejeitado com sucesso.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/encomendas";
    }

    @GetMapping("/faturas")
    public String faturas(
            HttpSession session,
            Model model
    ) {
        String pagina = protegerPagina(session, model, "faturas");

        if (!"faturas".equals(pagina)) {
            return pagina;
        }

        Object clienteIdObj = session.getAttribute("clienteId");

        if (clienteIdObj == null) {
            return "redirect:/login";
        }

        Integer clienteId = Integer.valueOf(clienteIdObj.toString());

        List<EncomendaDto> encomendas = backendAuthService.listarEncomendasCliente(clienteId);
        List<Map<String, Object>> faturasDetalhadas = new ArrayList<>();

        for (EncomendaDto encomenda : encomendas) {
            List<FaturaDto> faturas = backendAuthService.listarFaturasPorEncomenda(encomenda.getNum());

            for (FaturaDto fatura : faturas) {
                List<PagamentoDto> pagamentos = backendAuthService.listarPagamentosPorFatura(fatura.getId());

                BigDecimal totalPago = calcularTotalPago(pagamentos);
                BigDecimal valorEmDivida = calcularValorEmDivida(fatura, totalPago);

                Map<String, Object> dados = new HashMap<>();
                dados.put("fatura", fatura);
                dados.put("encomenda", encomenda);
                dados.put("pagamentos", pagamentos);
                dados.put("totalPago", totalPago);
                dados.put("valorEmDivida", valorEmDivida);
                dados.put("estadoPagamento", estadoPagamento(totalPago, valorEmDivida));
                dados.put("temDivida", valorEmDivida.compareTo(BigDecimal.ZERO) > 0);

                faturasDetalhadas.add(dados);
            }
        }

        model.addAttribute("faturas", faturasDetalhadas);

        return "faturas";
    }

    @PostMapping("/faturas/{idFatura}/pagar")
    public String pagarFatura(
            @PathVariable Long idFatura,
            @RequestParam String valorPago,
            @RequestParam String metodoPagamento,
            @RequestParam(required = false) String observacoes,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Object clienteIdObj = session.getAttribute("clienteId");

        if (clienteIdObj == null) {
            return "redirect:/login";
        }

        try {
            Integer clienteId = Integer.valueOf(clienteIdObj.toString());

            FaturaDto faturaEncontrada = null;
            BigDecimal valorEmDivida = BigDecimal.ZERO;

            List<EncomendaDto> encomendas = backendAuthService.listarEncomendasCliente(clienteId);

            for (EncomendaDto encomenda : encomendas) {
                List<FaturaDto> faturas = backendAuthService.listarFaturasPorEncomenda(encomenda.getNum());

                for (FaturaDto fatura : faturas) {
                    if (fatura.getId() != null && fatura.getId().equals(idFatura)) {
                        List<PagamentoDto> pagamentos = backendAuthService.listarPagamentosPorFatura(fatura.getId());
                        BigDecimal totalPago = calcularTotalPago(pagamentos);

                        faturaEncontrada = fatura;
                        valorEmDivida = calcularValorEmDivida(fatura, totalPago);
                        break;
                    }
                }

                if (faturaEncontrada != null) {
                    break;
                }
            }

            if (faturaEncontrada == null) {
                throw new IllegalArgumentException("Fatura não encontrada para este cliente.");
            }

            if (valorEmDivida.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Esta fatura já se encontra totalmente paga.");
            }

            BigDecimal valor = parseValor(valorPago);

            if (valor.compareTo(valorEmDivida) > 0) {
                throw new IllegalArgumentException(
                        "O valor pago não pode ser superior ao valor em dívida: "
                                + valorEmDivida + " €."
                );
            }

            CriarPagamentoRequestDto request = new CriarPagamentoRequestDto();
            request.setIdFatura(idFatura);
            request.setValorPago(valor);
            request.setMetodoPagamento(isBlank(metodoPagamento) ? "Web" : metodoPagamento.trim());
            request.setObservacoes(isBlank(observacoes) ? "Pagamento registado pelo cliente na aplicação web." : observacoes.trim());

            backendAuthService.criarPagamento(request);

            redirectAttributes.addFlashAttribute("sucesso", "Pagamento registado com sucesso.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/faturas";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        return protegerPagina(session, model, "perfil");
    }

    @GetMapping("/encomendas/{id}")
    public String detalheEncomenda(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ) {
        String pagina = protegerPagina(session, model, "detalhe-encomenda");

        if (!"detalhe-encomenda".equals(pagina)) {
            return pagina;
        }

        Object clienteIdObj = session.getAttribute("clienteId");

        if (clienteIdObj == null) {
            return "redirect:/login";
        }

        Integer clienteId = Integer.valueOf(clienteIdObj.toString());

        List<EncomendaDto> encomendas =
                backendAuthService.listarEncomendasCliente(clienteId);

        Map<String, Object> encomendaEncontrada = null;

        for (EncomendaDto encomenda : encomendas) {
            if (encomenda.getNum().equals(id)) {
                Map<String, Object> dados = new HashMap<>();

                dados.put("encomenda", encomenda);
                dados.put("estadoTexto", estadoTexto(encomenda.getIdestado()));
                dados.put("linhas", backendAuthService.listarLinhasEncomenda(encomenda.getNum()));

                List<DesignEncomendaDto> designs =
                        backendAuthService.listarDesignsDaEncomenda(encomenda.getNum());

                List<Map<String, Object>> designsDetalhados = new ArrayList<>();

                for (DesignEncomendaDto designDto : designs) {
                    Map<String, Object> designMap = new HashMap<>();
                    designMap.put("design", designDto);
                    designMap.put("imagens", backendAuthService.listarImagensDesign(designDto.getId()));
                    designsDetalhados.add(designMap);
                }

                dados.put("designs", designsDetalhados);
                encomendaEncontrada = dados;
                break;
            }
        }

        if (encomendaEncontrada == null) {
            return "redirect:/encomendas";
        }

        Map<Long, String> chapeusPorCod = new HashMap<>();

        for (ChapeuDto chapeu : backendAuthService.listarChapeus()) {
            chapeusPorCod.put(chapeu.getCod(), chapeu.getNome());
        }

        model.addAttribute("chapeusPorCod", chapeusPorCod);
        model.addAttribute("encomenda", encomendaEncontrada);

        return "detalhe-encomenda";
    }

    @PostMapping("/perfil/alterar-password")
    public String alterarPassword(AlterarPasswordClienteForm form, HttpSession session, Model model) {
        Object clienteId = session.getAttribute("clienteId");

        if (clienteId == null) {
            return "redirect:/login";
        }

        adicionarDadosSessaoAoModel(session, model);

        if (form.getNovaPassword() == null || form.getNovaPassword().isBlank()) {
            model.addAttribute("erro", "A nova password é obrigatória.");
            return "perfil";
        }

        if (!form.getNovaPassword().equals(form.getConfirmarPassword())) {
            model.addAttribute("erro", "A nova password e a confirmação não coincidem.");
            return "perfil";
        }

        try {
            form.setClienteId(Integer.valueOf(clienteId.toString()));
            backendAuthService.alterarPasswordCliente(form);
            model.addAttribute("sucesso", "Password alterada com sucesso.");
            return "perfil";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "perfil";
        }
    }

    private String protegerPagina(HttpSession session, Model model, String pagina) {
        Object clienteId = session.getAttribute("clienteId");

        if (clienteId == null) {
            return "redirect:/login";
        }

        adicionarDadosSessaoAoModel(session, model);
        return pagina;
    }

    private void adicionarDadosSessaoAoModel(HttpSession session, Model model) {
        model.addAttribute("clienteId", session.getAttribute("clienteId"));
        model.addAttribute("clienteNome", session.getAttribute("clienteNome"));
        model.addAttribute("clienteEmail", session.getAttribute("clienteEmail"));
    }

    private String estadoTexto(Long idEstado) {
        if (idEstado == null) {
            return "Sem estado";
        }

        return switch (idEstado.intValue()) {
            case 1 -> "Aguarda design";
            case 2 -> "Em preparação";
            case 3 -> "Pronta";
            case 4 -> "Paga";
            default -> "Estado " + idEstado;
        };
    }

    private BigDecimal calcularTotalPago(List<PagamentoDto> pagamentos) {
        if (pagamentos == null || pagamentos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pagamentos.stream()
                .map(PagamentoDto::getValorpago)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorEmDivida(FaturaDto fatura, BigDecimal totalPago) {
        BigDecimal valorFatura = fatura.getValor() == null ? BigDecimal.ZERO : fatura.getValor();
        BigDecimal valorEmDivida = valorFatura.subtract(totalPago == null ? BigDecimal.ZERO : totalPago);

        if (valorEmDivida.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return valorEmDivida;
    }

    private String estadoPagamento(BigDecimal totalPago, BigDecimal valorEmDivida) {
        if (valorEmDivida.compareTo(BigDecimal.ZERO) <= 0) {
            return "Paga";
        }

        if (totalPago != null && totalPago.compareTo(BigDecimal.ZERO) > 0) {
            return "Parcial";
        }

        return "Pendente";
    }

    private BigDecimal parseValor(String texto) {
        if (isBlank(texto)) {
            throw new IllegalArgumentException("Indica o valor a pagar.");
        }

        try {
            BigDecimal valor = new BigDecimal(texto.trim().replace(",", "."));

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor deve ser superior a zero.");
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O valor introduzido não é válido.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}