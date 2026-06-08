package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByNumfatura(Long numfatura);

    List<Pagamento> findByIdencomenda(BigDecimal idencomenda);

    @Query("SELECT COALESCE(SUM(p.valorpago), 0) FROM Pagamento p WHERE p.numfatura = :numfatura")
    BigDecimal somarTotalPagoPorFatura(@Param("numfatura") Long numfatura);
}