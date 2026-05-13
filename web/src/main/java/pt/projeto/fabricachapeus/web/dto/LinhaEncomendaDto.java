package pt.projeto.fabricachapeus.web.dto;

public class LinhaEncomendaDto {

    private Long id;
    private Long numencomenda;
    private Long codchapeu;
    private Long quantidade;
    private String tamanho;
    private String cores;

    public Long getId() {
        return id;
    }

    public Long getNumencomenda() {
        return numencomenda;
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public String getTamanho() {
        return tamanho;
    }

    public String getCores() {
        return cores;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumencomenda(Long numencomenda) {
        this.numencomenda = numencomenda;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }
}