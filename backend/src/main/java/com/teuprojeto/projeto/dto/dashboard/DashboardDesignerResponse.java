package com.teuprojeto.projeto.dto.dashboard;

public class DashboardDesignerResponse {

    private long totalEncomendasComDesign;
    private long designsEnviadosCliente;
    private long designsAprovadosCliente;
    private long designsRejeitadosCliente;

    public DashboardDesignerResponse() {
    }

    public long getTotalEncomendasComDesign() {
        return totalEncomendasComDesign;
    }

    public void setTotalEncomendasComDesign(long totalEncomendasComDesign) {
        this.totalEncomendasComDesign = totalEncomendasComDesign;
    }

    public long getDesignsEnviadosCliente() {
        return designsEnviadosCliente;
    }

    public void setDesignsEnviadosCliente(long designsEnviadosCliente) {
        this.designsEnviadosCliente = designsEnviadosCliente;
    }

    public long getDesignsAprovadosCliente() {
        return designsAprovadosCliente;
    }

    public void setDesignsAprovadosCliente(long designsAprovadosCliente) {
        this.designsAprovadosCliente = designsAprovadosCliente;
    }

    public long getDesignsRejeitadosCliente() {
        return designsRejeitadosCliente;
    }

    public void setDesignsRejeitadosCliente(long designsRejeitadosCliente) {
        this.designsRejeitadosCliente = designsRejeitadosCliente;
    }
}