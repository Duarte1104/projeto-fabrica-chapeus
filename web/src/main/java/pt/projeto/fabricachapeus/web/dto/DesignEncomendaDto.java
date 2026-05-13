package pt.projeto.fabricachapeus.web.dto;

public class DesignEncomendaDto {

    private Long id;
    private String descricaoDesigner;
    private String estadoDesign;

    public Long getId() {
        return id;
    }

    public String getDescricaoDesigner() {
        return descricaoDesigner;
    }

    public String getEstadoDesign() {
        return estadoDesign;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescricaoDesigner(String descricaoDesigner) {
        this.descricaoDesigner = descricaoDesigner;
    }

    public void setEstadoDesign(String estadoDesign) {
        this.estadoDesign = estadoDesign;
    }
}