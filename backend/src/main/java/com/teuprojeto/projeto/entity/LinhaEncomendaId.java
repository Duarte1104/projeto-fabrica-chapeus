package com.teuprojeto.projeto.entity;

import java.io.Serializable;
import java.util.Objects;

public class LinhaEncomendaId implements Serializable {

    private Long numencomenda;
    private Long codchapeu;

    public LinhaEncomendaId() {
    }

    public LinhaEncomendaId(Long numencomenda, Long codchapeu) {
        this.numencomenda = numencomenda;
        this.codchapeu = codchapeu;
    }

    public Long getNumencomenda() {
        return numencomenda;
    }

    public void setNumencomenda(Long numencomenda) {
        this.numencomenda = numencomenda;
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinhaEncomendaId that)) return false;
        return Objects.equals(numencomenda, that.numencomenda) &&
                Objects.equals(codchapeu, that.codchapeu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numencomenda, codchapeu);
    }
}