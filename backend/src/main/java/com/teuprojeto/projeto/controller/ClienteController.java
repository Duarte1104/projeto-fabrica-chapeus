package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.cliente.AlterarPasswordClienteRequest;
import com.teuprojeto.projeto.dto.cliente.AtualizarClienteRequest;
import com.teuprojeto.projeto.dto.cliente.ClienteLoginRequest;
import com.teuprojeto.projeto.dto.cliente.ClienteLoginResponse;
import com.teuprojeto.projeto.dto.cliente.CriarClienteRequest;
import com.teuprojeto.projeto.entity.Cliente;
import com.teuprojeto.projeto.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criar(@RequestBody CriarClienteRequest request) {
        return clienteService.criar(request);
    }

    @PostMapping("/registo-web")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criarPelaWeb(@RequestBody CriarClienteRequest request) {
        return clienteService.criar(request);
    }

    @PostMapping("/login")
    public ClienteLoginResponse login(@RequestBody ClienteLoginRequest request) {
        return clienteService.login(request);
    }

    @PostMapping("/alterar-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarPassword(@RequestBody AlterarPasswordClienteRequest request) {
        clienteService.alterarPassword(request);
    }

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Cliente procurarPorId(@PathVariable Integer id) {
        return clienteService.procurarPorId(id);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Integer id, @RequestBody AtualizarClienteRequest request) {
        return clienteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Integer id) {
        clienteService.apagar(id);
    }
}