package pt.projeto.fabricachapeus.web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pt.projeto.fabricachapeus.web.dto.*;

import java.util.List;

@Service
public class BackendAuthService {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    public BackendAuthService(RestTemplateBuilder restTemplateBuilder,
                              @Value("${backend.base-url}") String backendBaseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.backendBaseUrl = backendBaseUrl;
    }

    public ClienteLoginResponse login(LoginForm form) {
        try {
            return restTemplate.postForObject(backendBaseUrl + "/clientes/login", form, ClienteLoginResponse.class);
        } catch (HttpStatusCodeException e) {
            throw new IllegalArgumentException("Email ou password inválidos.");
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível contactar o backend.");
        }
    }

    public void registarCliente(RegistoClienteForm form) {
        try {
            restTemplate.postForObject(backendBaseUrl + "/clientes/registo-web", form, Object.class);
        } catch (HttpStatusCodeException e) {
            throw new IllegalArgumentException("Não foi possível criar a conta. Verifica se o email ou NIF já existem.");
        }
    }

    public void alterarPasswordCliente(AlterarPasswordClienteForm form) {
        try {
            restTemplate.postForObject(backendBaseUrl + "/clientes/alterar-password", form, Void.class);
        } catch (HttpStatusCodeException e) {
            throw new IllegalArgumentException("Não foi possível alterar a password. Verifica se a password atual está correta.");
        }
    }

    public List<ChapeuDto> listarChapeus() {
        try {
            ChapeuDto[] chapeus = restTemplate.getForObject(backendBaseUrl + "/chapeus", ChapeuDto[].class);
            return chapeus == null ? List.of() : List.of(chapeus);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar o catálogo de chapéus.");
        }
    }

    public void criarEncomenda(CriarEncomendaWebRequest request) {
        try {
            restTemplate.postForObject(backendBaseUrl + "/encomendas", request, Object.class);
        } catch (HttpStatusCodeException e) {
            throw new IllegalArgumentException("Não foi possível criar a encomenda. Verifica os dados preenchidos.");
        }
    }
}