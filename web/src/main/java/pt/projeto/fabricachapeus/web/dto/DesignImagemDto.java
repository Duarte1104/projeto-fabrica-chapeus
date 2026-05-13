package pt.projeto.fabricachapeus.web.dto;

public class DesignImagemDto {

    private Long id;
    private Long idDesignEncomenda;
    private String urlImagem;

    public Long getId() {
        return id;
    }

    public Long getIdDesignEncomenda() {
        return idDesignEncomenda;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdDesignEncomenda(Long idDesignEncomenda) {
        this.idDesignEncomenda = idDesignEncomenda;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}