package com.teuprojeto.desktop.view.funcionario;

public class FuncionarioEncomendaRow {

    private final String codigoOrdem;
    private final String codigoEncomenda;
    private final String produto;
    private final String cliente;
    private final int quantidadeTotal;
    private final int unidadesConcluidas;
    private final String prioridade;
    private final String estado;
    private final String dataLimite;
    private final boolean montagemConcluida;
    private final boolean costurasConcluidas;
    private final boolean personalizacaoConcluida;
    private final String observacoes;

    public FuncionarioEncomendaRow(String codigoOrdem,
                                   String codigoEncomenda,
                                   String produto,
                                   String cliente,
                                   int quantidadeTotal,
                                   int unidadesConcluidas,
                                   String prioridade,
                                   String estado,
                                   String dataLimite,
                                   boolean montagemConcluida,
                                   boolean costurasConcluidas,
                                   boolean personalizacaoConcluida,
                                   String observacoes) {
        this.codigoOrdem = codigoOrdem;
        this.codigoEncomenda = codigoEncomenda;
        this.produto = produto;
        this.cliente = cliente;
        this.quantidadeTotal = quantidadeTotal;
        this.unidadesConcluidas = unidadesConcluidas;
        this.prioridade = prioridade;
        this.estado = estado;
        this.dataLimite = dataLimite;
        this.montagemConcluida = montagemConcluida;
        this.costurasConcluidas = costurasConcluidas;
        this.personalizacaoConcluida = personalizacaoConcluida;
        this.observacoes = observacoes;
    }

    public String getCodigoOrdem() {
        return codigoOrdem;
    }

    public String getCodigoEncomenda() {
        return codigoEncomenda;
    }

    public String getProduto() {
        return produto;
    }

    public String getCliente() {
        return cliente;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getUnidadesConcluidas() {
        return unidadesConcluidas;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public String getEstado() {
        return estado;
    }

    public String getDataLimite() {
        return dataLimite;
    }

    public boolean isMontagemConcluida() {
        return montagemConcluida;
    }

    public boolean isCosturasConcluidas() {
        return costurasConcluidas;
    }

    public boolean isPersonalizacaoConcluida() {
        return personalizacaoConcluida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public double getProgresso() {
        if (quantidadeTotal <= 0) {
            return 0;
        }
        return (double) unidadesConcluidas / quantidadeTotal;
    }

    public int getProgressoPercent() {
        return (int) Math.round(getProgresso() * 100);
    }

    public boolean isConcluida() {
        return unidadesConcluidas >= quantidadeTotal;
    }
}