package com.teuprojeto.projeto.entity;

import java.io.Serializable;
import java.util.Objects;

public class ModeloChapeuId implements Serializable {

    private Long codchapeu;
    private Long idmaterial;

    public ModeloChapeuId() {
    }

    public ModeloChapeuId(Long codchapeu, Long idmaterial) {
        this.codchapeu = codchapeu;
        this.idmaterial = idmaterial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModeloChapeuId that)) return false;
        return Objects.equals(codchapeu, that.codchapeu) &&
                Objects.equals(idmaterial, that.idmaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codchapeu, idmaterial);
    }
}