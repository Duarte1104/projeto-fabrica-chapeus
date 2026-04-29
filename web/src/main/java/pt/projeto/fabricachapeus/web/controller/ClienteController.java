package pt.projeto.fabricachapeus.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pt.projeto.fabricachapeus.web.dto.*;
import pt.projeto.fabricachapeus.web.service.BackendAuthService;

import java.util.List;

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

        if ("encomendas".equals(pagina) && sucesso != null) {
            model.addAttribute("sucesso", "Encomenda criada com sucesso.");
        }

        return pagina;
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}