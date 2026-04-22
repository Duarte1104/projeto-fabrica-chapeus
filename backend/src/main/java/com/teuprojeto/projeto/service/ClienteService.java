package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.cliente.CriarClienteRequest;
import com.teuprojeto.projeto.entity.Cliente;
import com.teuprojeto.projeto.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import com.teuprojeto.projeto.dto.cliente.AtualizarClienteRequest;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criar(CriarClienteRequest request) {
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um cliente com esse email.");
        }

        if (clienteRepository.existsByNif(request.getNif())) {
            throw new IllegalArgumentException("Já existe um cliente com esse NIF.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setNif(request.getNif());
        cliente.setTipo(request.getTipo());
        cliente.setRua(request.getRua());
        cliente.setNporta(request.getNporta());
        cliente.setCodpostal(request.getCodpostal());
        cliente.setCidade(request.getCidade());
        cliente.setObservacoes(request.getObservacoes());

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente procurarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
    }

    public void apagar(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        clienteRepository.deleteById(id);
    }

    public Cliente atualizar(Integer id, AtualizarClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setNif(request.getNif());
        cliente.setTipo(request.getTipo());
        cliente.setRua(request.getRua());
        cliente.setNporta(request.getNporta());
        cliente.setCodpostal(request.getCodpostal());
        cliente.setCidade(request.getCidade());
        cliente.setObservacoes(request.getObservacoes());

        return clienteRepository.save(cliente);
    }
}