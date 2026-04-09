package com.teuprojeto.projeto.controller;

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

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Cliente procurarPorId(@PathVariable Integer id) {
        return clienteService.procurarPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Integer id) {
        clienteService.apagar(id);
    }
}