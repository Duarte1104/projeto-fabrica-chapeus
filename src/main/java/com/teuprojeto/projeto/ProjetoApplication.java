package com.teuprojeto.projeto;

import com.teuprojeto.projeto.entity.Chapeu;
import com.teuprojeto.projeto.entity.Cliente;
import com.teuprojeto.projeto.entity.Codpostal;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.EstadoEncomenda;
import com.teuprojeto.projeto.entity.Etapa;
import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.entity.Fornecedor;
import com.teuprojeto.projeto.entity.Funcionario;
import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.entity.ModeloChapeu;
import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.entity.TipoFuncionario;
import com.teuprojeto.projeto.entity.OrdemProducao;
import com.teuprojeto.projeto.service.ChapeuService;
import com.teuprojeto.projeto.service.ClienteService;
import com.teuprojeto.projeto.service.CodpostalService;
import com.teuprojeto.projeto.service.EncomendaService;
import com.teuprojeto.projeto.service.EstadoEncomendaService;
import com.teuprojeto.projeto.service.EtapaService;
import com.teuprojeto.projeto.service.FaturaService;
import com.teuprojeto.projeto.service.FornecedorService;
import com.teuprojeto.projeto.service.FuncionarioService;
import com.teuprojeto.projeto.service.LinhaEncomendaService;
import com.teuprojeto.projeto.service.MaterialService;
import com.teuprojeto.projeto.service.ModeloChapeuService;
import com.teuprojeto.projeto.service.OrdemProducaoService;
import com.teuprojeto.projeto.service.PagamentoService;
import com.teuprojeto.projeto.service.TipoFuncionarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
public class ProjetoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetoApplication.class, args);
    }

    @Bean
    CommandLineRunner run(
            CodpostalService codpostalService,
            EstadoEncomendaService estadoEncomendaService,
            ChapeuService chapeuService,
            FornecedorService fornecedorService,
            MaterialService materialService,
            EtapaService etapaService,
            TipoFuncionarioService tipoFuncionarioService,
            OrdemProducaoService ordemProducaoService,
            FuncionarioService funcionarioService,
            ClienteService clienteService,
            EncomendaService encomendaService,
            FaturaService faturaService,
            PagamentoService pagamentoService,
            LinhaEncomendaService linhaEncomendaService,
            ModeloChapeuService modeloChapeuService
    ) {
        return args -> {

            System.out.println("=== TESTE CODPOSTAL ===");
            Codpostal cp = new Codpostal();
            cp.setCodpostal("1000-001");
            cp.setLocalidade("Lisboa");
            codpostalService.guardar(cp);
            codpostalService.listarTodos().forEach(c ->
                    System.out.println(c.getCodpostal() + " - " + c.getLocalidade())
            );

            System.out.println("=== TESTE ESTADO ENCOMENDA ===");
            EstadoEncomenda estado = new EstadoEncomenda();
            estado.setId(1L);
            estado.setDescricao("Pendente");
            estadoEncomendaService.guardar(estado);
            estadoEncomendaService.listarTodos().forEach(e ->
                    System.out.println(e.getId() + " - " + e.getDescricao())
            );

            System.out.println("=== TESTE CHAPEU ===");
            Chapeu chapeu = new Chapeu();
            chapeu.setCod(1L);
            chapeu.setNome("Chapéu Clássico");
            chapeu.setPrecoactvenda(new BigDecimal("29.99"));
            chapeuService.guardar(chapeu);
            chapeuService.listarTodos().forEach(c ->
                    System.out.println(c.getCod() + " - " + c.getNome() + " - " + c.getPrecoactvenda())
            );

            System.out.println("=== TESTE FORNECEDOR ===");
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNum(1L);
            fornecedor.setNome("Fornecedor Central");
            fornecedor.setTelefone("913333333");
            fornecedor.setEmail("fornecedor@email.com");
            fornecedor.setRua("Rua dos Fornecedores");
            fornecedor.setNporta("20");
            fornecedor.setCodpostal("1000-001");
            fornecedorService.guardar(fornecedor);
            fornecedorService.listarTodos().forEach(f ->
                    System.out.println(f.getNum() + " - " + f.getNome())
            );

            System.out.println("=== TESTE MATERIAL ===");
            Material material = new Material();
            material.setId(1L);
            material.setQtdstock(100L);
            material.setPreco(new BigDecimal("5.50"));
            material.setNumfornecedor(1L);
            materialService.guardar(material);
            materialService.listarTodos().forEach(m ->
                    System.out.println(m.getId() + " - " + m.getQtdstock() + " - " + m.getPreco())
            );

            System.out.println("=== TESTE ETAPA ===");
            Etapa etapa = new Etapa();
            etapa.setId(1L);
            etapa.setDescricao("Corte");
            etapaService.guardar(etapa);
            etapaService.listarTodos().forEach(e ->
                    System.out.println(e.getId() + " - " + e.getDescricao())
            );

            System.out.println("=== TESTE TIPO FUNCIONARIO ===");
            TipoFuncionario tipoFuncionario = new TipoFuncionario();
            tipoFuncionario.setId(1L);
            tipoFuncionario.setDescricao("Costureiro");
            tipoFuncionarioService.guardar(tipoFuncionario);
            tipoFuncionarioService.listarTodos().forEach(t ->
                    System.out.println(t.getId() + " - " + t.getDescricao())
            );

            System.out.println("=== TESTE ORDEM PRODUCAO ===");
            OrdemProducao ordemProducao = new OrdemProducao();
            ordemProducao.setId(1L);
            ordemProducao.setDatainicio(LocalDate.now());
            ordemProducao.setDatafim(LocalDate.now().plusDays(3));
            ordemProducao.setQuantidade(50L);
            ordemProducao.setIdetapa(1L);
            ordemProducao.setCodchapeu(1L);
            ordemProducaoService.guardar(ordemProducao);
            ordemProducaoService.listarTodos().forEach(o ->
                    System.out.println(o.getId() + " - " + o.getQuantidade())
            );

            System.out.println("=== TESTE FUNCIONARIO ===");
            Funcionario funcionario = new Funcionario();
            funcionario.setNum(1L);
            funcionario.setNif("123456789");
            funcionario.setTelefone("914444444");
            funcionario.setEmail("funcionario@email.com");
            funcionario.setRua("Rua do Trabalho");
            funcionario.setNporta("30");
            funcionario.setCodpostal("1000-001");
            funcionario.setIdtipofuncionario(1L);
            funcionario.setIdordemproducao(1L);
            funcionarioService.guardar(funcionario);
            funcionarioService.listarTodos().forEach(f ->
                    System.out.println(f.getNum() + " - " + f.getNif())
            );

            System.out.println("=== TESTE CLIENTE ===");
            Cliente cliente = new Cliente();
            cliente.setCod(1);
            cliente.setNome("João Silva");
            cliente.setTelefone("912345678");
            cliente.setEmail("joao@email.com");
            cliente.setRua("Rua Principal");
            cliente.setNporta("10");
            cliente.setCodpostal("1000-001");
            clienteService.guardar(cliente);
            clienteService.listarTodos().forEach(c ->
                    System.out.println(c.getCod() + " - " + c.getNome())
            );

            System.out.println("=== TESTE ENCOMENDA ===");
            Encomenda encomenda = new Encomenda();
            encomenda.setNum(new BigDecimal("1"));
            encomenda.setData(LocalDate.now());
            encomenda.setHora(LocalTime.now());
            encomenda.setValortotal(new BigDecimal("59.98"));
            encomenda.setIdestado(1L);
            encomenda.setIdcliente(1L);
            encomenda.setIdfornecedor(1L);
            encomendaService.guardar(encomenda);
            encomendaService.listarTodos().forEach(e ->
                    System.out.println(e.getNum() + " - " + e.getValortotal())
            );

            System.out.println("=== TESTE FATURA ===");
            Fatura fatura = new Fatura();
            fatura.setNum(1L);
            fatura.setValor(new BigDecimal("59.98"));
            faturaService.guardar(fatura);
            faturaService.listarTodos().forEach(f ->
                    System.out.println(f.getNum() + " - " + f.getValor())
            );

            System.out.println("=== TESTE PAGAMENTO ===");
            Pagamento pagamento = new Pagamento();
            pagamento.setCod(1L);
            pagamento.setNome("Pagamento Multibanco");
            pagamento.setPrecoactvenda(new BigDecimal("59.98"));
            pagamento.setIdencomenda(new BigDecimal("1"));
            pagamento.setNumfatura(1L);
            pagamentoService.guardar(pagamento);
            pagamentoService.listarTodos().forEach(p ->
                    System.out.println(p.getCod() + " - " + p.getNome())
            );

            System.out.println("=== TESTE LINHA ENCOMENDA ===");
            LinhaEncomenda linhaEncomenda = new LinhaEncomenda();
            linhaEncomenda.setNumencomenda(1L);
            linhaEncomenda.setCodchapeu(1L);
            linhaEncomenda.setQuantidade(2L);
            linhaEncomendaService.guardar(linhaEncomenda);
            linhaEncomendaService.listarTodos().forEach(l ->
                    System.out.println(l.getNumencomenda() + " - " + l.getCodchapeu() + " - " + l.getQuantidade())
            );

            System.out.println("=== TESTE MODELO CHAPEU ===");
            ModeloChapeu modeloChapeu = new ModeloChapeu();
            modeloChapeu.setCodchapeu(1L);
            modeloChapeu.setIdmaterial(1L);
            modeloChapeuService.guardar(modeloChapeu);
            modeloChapeuService.listarTodos().forEach(m ->
                    System.out.println(m.getCodchapeu() + " - " + m.getIdmaterial())
            );
        };
    }
}