package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.cliente.AlterarPasswordClienteRequest;
import com.teuprojeto.projeto.dto.cliente.AtualizarClienteRequest;
import com.teuprojeto.projeto.dto.cliente.ClienteLoginRequest;
import com.teuprojeto.projeto.dto.cliente.ClienteLoginResponse;
import com.teuprojeto.projeto.dto.cliente.CriarClienteRequest;
import com.teuprojeto.projeto.entity.Cliente;
import com.teuprojeto.projeto.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criar(CriarClienteRequest request) {
        validarCamposObrigatorios(request);

        String email = request.getEmail().trim().toLowerCase();
        String nif = request.getNif().trim();

        if (clienteRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe um cliente com esse email.");
        }

        if (clienteRepository.existsByNif(nif)) {
            throw new IllegalArgumentException("Já existe um cliente com esse NIF.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome().trim());
        cliente.setEmail(email);
        cliente.setTelefone(request.getTelefone().trim());
        cliente.setNif(nif);
        cliente.setTipo(valorOuDefault(request.getTipo(), "Particular"));
        cliente.setRua(request.getRua().trim());
        cliente.setNporta(valorOpcional(request.getNporta()));
        cliente.setCodpostal(request.getCodpostal().trim());
        cliente.setCidade(request.getCidade().trim());
        cliente.setObservacoes(valorOpcional(request.getObservacoes()));
        cliente.setPassword(valorOuDefault(request.getPassword(), "1234"));

        return clienteRepository.save(cliente);
    }

    public ClienteLoginResponse login(ClienteLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("A password é obrigatória.");
        }

        Cliente cliente = clienteRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!cliente.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        return new ClienteLoginResponse(
                cliente.getCod(),
                cliente.getNome(),
                cliente.getEmail()
        );
    }

    public void alterarPassword(AlterarPasswordClienteRequest request) {
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("Cliente inválido.");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if (request.getPasswordAtual() == null || !cliente.getPassword().equals(request.getPasswordAtual())) {
            throw new IllegalArgumentException("A password atual está incorreta.");
        }

        if (request.getNovaPassword() == null || request.getNovaPassword().isBlank()) {
            throw new IllegalArgumentException("A nova password é obrigatória.");
        }

        cliente.setPassword(request.getNovaPassword());
        clienteRepository.save(cliente);
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

        validarCamposObrigatoriosAtualizar(request);

        String email = request.getEmail().trim().toLowerCase();
        String nif = request.getNif().trim();

        if (clienteRepository.existsByEmailIgnoreCaseAndCodNot(email, id)) {
            throw new IllegalArgumentException("Já existe outro cliente com esse email.");
        }

        if (clienteRepository.existsByNifAndCodNot(nif, id)) {
            throw new IllegalArgumentException("Já existe outro cliente com esse NIF.");
        }

        cliente.setNome(request.getNome().trim());
        cliente.setEmail(email);
        cliente.setTelefone(request.getTelefone().trim());
        cliente.setNif(nif);
        cliente.setTipo(valorOuDefault(request.getTipo(), "Particular"));
        cliente.setRua(request.getRua().trim());
        cliente.setNporta(valorOpcional(request.getNporta()));
        cliente.setCodpostal(request.getCodpostal().trim());
        cliente.setCidade(request.getCidade().trim());
        cliente.setObservacoes(valorOpcional(request.getObservacoes()));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            cliente.setPassword(request.getPassword().trim());
        }

        return clienteRepository.save(cliente);
    }

    private void validarCamposObrigatorios(CriarClienteRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (request.getTelefone() == null || request.getTelefone().isBlank()) {
            throw new IllegalArgumentException("O telefone é obrigatório.");
        }

        if (request.getNif() == null || request.getNif().isBlank()) {
            throw new IllegalArgumentException("O NIF é obrigatório.");
        }

        if (request.getRua() == null || request.getRua().isBlank()) {
            throw new IllegalArgumentException("A rua é obrigatória.");
        }

        if (request.getCodpostal() == null || request.getCodpostal().isBlank()) {
            throw new IllegalArgumentException("O código postal é obrigatório.");
        }

        if (request.getCidade() == null || request.getCidade().isBlank()) {
            throw new IllegalArgumentException("A cidade é obrigatória.");
        }
    }

    private void validarCamposObrigatoriosAtualizar(AtualizarClienteRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (request.getTelefone() == null || request.getTelefone().isBlank()) {
            throw new IllegalArgumentException("O telefone é obrigatório.");
        }

        if (request.getNif() == null || request.getNif().isBlank()) {
            throw new IllegalArgumentException("O NIF é obrigatório.");
        }

        if (request.getRua() == null || request.getRua().isBlank()) {
            throw new IllegalArgumentException("A rua é obrigatória.");
        }

        if (request.getCodpostal() == null || request.getCodpostal().isBlank()) {
            throw new IllegalArgumentException("O código postal é obrigatório.");
        }

        if (request.getCidade() == null || request.getCidade().isBlank()) {
            throw new IllegalArgumentException("A cidade é obrigatória.");
        }
    }

    private String valorOuDefault(String valor, String valorDefault) {
        if (valor == null || valor.isBlank()) {
            return valorDefault;
        }

        return valor.trim();
    }

    private String valorOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}