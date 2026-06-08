package pt.projeto.fabricachapeus.web.service;

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
            ChapeuDto[] chapeus = restTemplate.getForObject(
                    backendBaseUrl + "/chapeus",
                    ChapeuDto[].class
            );

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

    public List<EncomendaDto> listarEncomendasCliente(Integer idCliente) {
        try {
            EncomendaDto[] encomendas = restTemplate.getForObject(
                    backendBaseUrl + "/encomendas/cliente/" + idCliente,
                    EncomendaDto[].class
            );

            return encomendas == null ? List.of() : List.of(encomendas);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar as encomendas.");
        }
    }

    public List<LinhaEncomendaDto> listarLinhasEncomenda(Long idEncomenda) {
        try {
            LinhaEncomendaDto[] linhas = restTemplate.getForObject(
                    backendBaseUrl + "/encomendas/" + idEncomenda + "/linhas",
                    LinhaEncomendaDto[].class
            );

            return linhas == null ? List.of() : List.of(linhas);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar as linhas da encomenda.");
        }
    }

    public List<DesignEncomendaDto> listarDesignsDaEncomenda(Long idEncomenda) {
        try {
            DesignEncomendaDto[] designs = restTemplate.getForObject(
                    backendBaseUrl + "/designs/encomenda/" + idEncomenda,
                    DesignEncomendaDto[].class
            );

            return designs == null ? List.of() : List.of(designs);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar os designs.");
        }
    }

    public List<DesignImagemDto> listarImagensDesign(Long idDesign) {
        try {
            DesignImagemDto[] imagens = restTemplate.getForObject(
                    backendBaseUrl + "/designs/" + idDesign + "/imagens",
                    DesignImagemDto[].class
            );

            return imagens == null ? List.of() : List.of(imagens);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar as imagens do design.");
        }
    }

    public void aprovarDesign(Long idDesign) {
        try {
            restTemplate.postForObject(
                    backendBaseUrl + "/designs/" + idDesign + "/aprovar",
                    null,
                    Object.class
            );
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível aprovar o design.");
        }
    }

    public void rejeitarDesign(Long idDesign) {
        try {
            restTemplate.postForObject(
                    backendBaseUrl + "/designs/" + idDesign + "/rejeitar",
                    null,
                    Object.class
            );
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível rejeitar o design.");
        }
    }

    public List<FaturaDto> listarFaturasPorEncomenda(Long idEncomenda) {
        try {
            FaturaDto[] faturas = restTemplate.getForObject(
                    backendBaseUrl + "/faturas/encomenda/" + idEncomenda,
                    FaturaDto[].class
            );

            return faturas == null ? List.of() : List.of(faturas);

        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar as faturas.");
        }
    }
    public List<PagamentoDto> listarPagamentosPorFatura(Long idFatura) {
        try {
            PagamentoDto[] pagamentos = restTemplate.getForObject(
                    backendBaseUrl + "/pagamentos/fatura/" + idFatura,
                    PagamentoDto[].class
            );

            return pagamentos == null ? List.of() : List.of(pagamentos);

        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível carregar os pagamentos da fatura.");
        }
    }

    public void criarPagamento(CriarPagamentoRequestDto request) {
        try {
            restTemplate.postForObject(
                    backendBaseUrl + "/pagamentos",
                    request,
                    PagamentoDto.class
            );
        } catch (HttpStatusCodeException e) {
            String mensagem = e.getResponseBodyAsString();

            if (mensagem == null || mensagem.isBlank()) {
                throw new IllegalArgumentException("Não foi possível registar o pagamento.");
            }

            throw new IllegalArgumentException(mensagem);
        } catch (RestClientException e) {
            throw new IllegalStateException("Não foi possível contactar o backend para registar o pagamento.");
        }
    }
}