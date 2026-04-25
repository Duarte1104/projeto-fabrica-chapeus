package com.teuprojeto.desktop.view.funcionario;

public class FuncionarioEncomendaRow {

    private final Long idEncomenda;
    private final String codigoEncomenda;
    private final String produto;
    private final String cliente;
    private final long quantidadeTotal;
    private final String prioridade;
    private final String estado;
    private final String dataLimite;
    private final boolean montagemConcluida;
    private final boolean costurasConcluidas;
    private final boolean personalizacaoConcluida;
    private final boolean precisaPersonalizacao;
    private final String montagemComentario;
    private final String costurasComentario;
    private final String personalizacaoComentario;
    private final String observacoes;
    private final boolean atribuida;
    private final boolean concluida;

    public FuncionarioEncomendaRow(Long idEncomenda,
                                   String codigoEncomenda,
                                   String produto,
                                   String cliente,
                                   long quantidadeTotal,
                                   String prioridade,
                                   String estado,
                                   String dataLimite,
                                   boolean montagemConcluida,
                                   boolean costurasConcluidas,
                                   boolean personalizacaoConcluida,
                                   boolean precisaPersonalizacao,
                                   String montagemComentario,
                                   String costurasComentario,
                                   String personalizacaoComentario,
                                   String observacoes,
                                   boolean atribuida,
                                   boolean concluida) {
        this.idEncomenda = idEncomenda;
        this.codigoEncomenda = codigoEncomenda;
        this.produto = produto;
        this.cliente = cliente;
        this.quantidadeTotal = quantidadeTotal;
        this.prioridade = prioridade;
        this.estado = estado;
        this.dataLimite = dataLimite;
        this.montagemConcluida = montagemConcluida;
        this.costurasConcluidas = costurasConcluidas;
        this.personalizacaoConcluida = personalizacaoConcluida;
        this.precisaPersonalizacao = precisaPersonalizacao;
        this.montagemComentario = montagemComentario;
        this.costurasComentario = costurasComentario;
        this.personalizacaoComentario = personalizacaoComentario;
        this.observacoes = observacoes;
        this.atribuida = atribuida;
        this.concluida = concluida;
    }

    public Long getIdEncomenda() {
        return idEncomenda;
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

    public long getQuantidadeTotal() {
        return quantidadeTotal;
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

    public boolean isPrecisaPersonalizacao() {
        return precisaPersonalizacao;
    }

    public String getMontagemComentario() {
        return montagemComentario;
    }

    public String getCosturasComentario() {
        return costurasComentario;
    }

    public String getPersonalizacaoComentario() {
        return personalizacaoComentario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public boolean isAtribuida() {
        return atribuida;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public int getEtapasConcluidas() {
        if (concluida) {
            return getTotalEtapas();
        }

        int total = 0;
        if (montagemConcluida) {
            total++;
        }
        if (costurasConcluidas) {
            total++;
        }
        if (precisaPersonalizacao && personalizacaoConcluida) {
            total++;
        }
        return total;
    }

    public int getTotalEtapas() {
        return precisaPersonalizacao ? 3 : 2;
    }

    public double getProgresso() {
        int totalEtapas = getTotalEtapas();
        if (totalEtapas <= 0) {
            return 0;
        }
        return (double) getEtapasConcluidas() / totalEtapas;
    }

    public int getProgressoPercent() {
        return (int) Math.round(getProgresso() * 100);
    }

    public String getResumoEtapas() {
        return getEtapasConcluidas() + "/" + getTotalEtapas() + " etapas concluídas";
    }
}