package pt.projeto.fabricachapeus.web.dto;

public class RegistoClienteForm {

    private String nome;
    private String email;
    private String telefone;
    private String nif;
    private String tipo = "Particular";
    private String rua;
    private String nporta;
    private String codpostal;
    private String cidade;
    private String observacoes;
    private String password;

    public RegistoClienteForm() {
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getNif() {
        return nif;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRua() {
        return rua;
    }

    public String getNporta() {
        return nporta;
    }

    public String getCodpostal() {
        return codpostal;
    }

    public String getCidade() {
        return cidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getPassword() {
        return password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setNporta(String nporta) {
        this.nporta = nporta;
    }

    public void setCodpostal(String codpostal) {
        this.codpostal = codpostal;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}