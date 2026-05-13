package pt.projeto.fabricachapeus.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.projeto.fabricachapeus.web.dto.*;
import pt.projeto.fabricachapeus.web.service.BackendAuthService;

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
            String codChapeu,
            String quantidade,
            String dataEntrega,
            String tamanho,
            String cores,
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
            if (isBlank(codChapeu) || isBlank(quantidade) || isBlank(dataEntrega) || isBlank(tamanho) || isBlank(cores)) {
                model.addAttribute("erro", "Preenche todos os campos obrigatórios.");
                model.addAttribute("chapeus", backendAuthService.listarChapeus());
                return "nova-encomenda";
            }

            List<ChapeuDto> chapeus = backendAuthService.listarChapeus();

            ChapeuDto chapeuSelecionado = chapeus.stream()
                    .filter(c -> c.getCod() != null && c.getCod().equals(Long.valueOf(codChapeu)))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Chapéu inválido."));

            LinhaEncomendaWebRequest linha = new LinhaEncomendaWebRequest();
            linha.setCodChapeu(chapeuSelecionado.getCod());
            linha.setQuantidade(Long.valueOf(quantidade));
            linha.setPrecoUnitario(chapeuSelecionado.getPrecoactvenda());
            linha.setTamanho(tamanho.trim());
            linha.setCores(cores.trim());

            CriarEncomendaWebRequest request = new CriarEncomendaWebRequest();
            request.setIdCliente(Integer.valueOf(clienteId.toString()));
            request.setDataEntrega(dataEntrega);
            request.setObservacoes(isBlank(observacoes) ? null : observacoes.trim());
            request.setDesign("on".equalsIgnoreCase(design));
            request.setDescricaoDesign("on".equalsIgnoreCase(design) ? descricaoDesign : null);
            request.setLinhas(List.of(linha));

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

            for (DesignEncomendaDto design : designs) {
                Map<String, Object> designMap = new HashMap<>();
                designMap.put("design", design);
                designMap.put("imagens", backendAuthService.listarImagensDesign(design.getId()));
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
    public String faturas(HttpSession session, Model model) {
        return protegerPagina(session, model, "faturas");
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        return protegerPagina(session, model, "perfil");
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}