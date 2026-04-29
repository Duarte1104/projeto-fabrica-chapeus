package pt.projeto.fabricachapeus.web.dto;

public class AlterarPasswordClienteForm {

    private Integer clienteId;
    private String passwordAtual;
    private String novaPassword;
    private String confirmarPassword;

    public AlterarPasswordClienteForm() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public String getPasswordAtual() {
        return passwordAtual;
    }

    public String getNovaPassword() {
        return novaPassword;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public void setPasswordAtual(String passwordAtual) {
        this.passwordAtual = passwordAtual;
    }

    public void setNovaPassword(String novaPassword) {
        this.novaPassword = novaPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}