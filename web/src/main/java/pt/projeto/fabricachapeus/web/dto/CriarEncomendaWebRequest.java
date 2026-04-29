package pt.projeto.fabricachapeus.web.dto;

import java.util.List;

public class CriarEncomendaWebRequest {

    private Integer idCliente;
    private String dataEntrega;
    private String observacoes;
    private Boolean design;
    private String descricaoDesign;
    private List<LinhaEncomendaWebRequest> linhas;

    public CriarEncomendaWebRequest() {
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Boolean getDesign() {
        return design;
    }

    public void setDesign(Boolean design) {
        this.design = design;
    }

    public String getDescricaoDesign() {
        return descricaoDesign;
    }

    public void setDescricaoDesign(String descricaoDesign) {
        this.descricaoDesign = descricaoDesign;
    }

    public List<LinhaEncomendaWebRequest> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<LinhaEncomendaWebRequest> linhas) {
        this.linhas = linhas;
    }
}