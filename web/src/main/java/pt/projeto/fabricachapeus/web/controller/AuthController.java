package pt.projeto.fabricachapeus.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.projeto.fabricachapeus.web.dto.ClienteLoginResponse;
import pt.projeto.fabricachapeus.web.dto.LoginForm;
import pt.projeto.fabricachapeus.web.dto.RegistoClienteForm;
import pt.projeto.fabricachapeus.web.service.BackendAuthService;

@Controller
public class AuthController {

    private final BackendAuthService backendAuthService;

    public AuthController(BackendAuthService backendAuthService) {
        this.backendAuthService = backendAuthService;
    }

    @GetMapping("/login")
    public String login(
            Model model,
            @RequestParam(value = "sucesso", required = false) String sucesso,
            @RequestParam(value = "logout", required = false) String logout
    ) {
        model.addAttribute("loginForm", new LoginForm());

        if (sucesso != null) {
            model.addAttribute("sucesso", "Conta criada com sucesso. Já pode iniciar sessão.");
        }

        if (logout != null) {
            model.addAttribute("sucesso", "Sessão terminada com sucesso.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(
            @ModelAttribute LoginForm loginForm,
            Model model,
            HttpSession session
    ) {
        try {
            ClienteLoginResponse response = backendAuthService.login(loginForm);

            if (response == null || response.getCod() == null) {
                model.addAttribute("erro", "Resposta inválida do backend.");
                model.addAttribute("loginForm", loginForm);
                return "login";
            }

            session.setAttribute("clienteId", response.getCod());
            session.setAttribute("clienteNome", response.getNome());
            session.setAttribute("clienteEmail", response.getEmail());

            return "redirect:/cliente";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("loginForm", loginForm);
            return "login";
        }
    }

    @GetMapping("/registo")
    public String registo(Model model) {
        model.addAttribute("registoForm", new RegistoClienteForm());
        return "registo";
    }

    @PostMapping("/registo")
    public String processarRegisto(
            @ModelAttribute RegistoClienteForm registoForm,
            Model model
    ) {
        try {
            backendAuthService.registarCliente(registoForm);
            return "redirect:/login?sucesso=1";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("registoForm", registoForm);
            return "registo";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=1";
    }
}