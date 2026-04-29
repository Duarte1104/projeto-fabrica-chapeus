package pt.projeto.fabricachapeus.web.dto;

public class ClienteLoginResponse {

    private Integer cod;
    private String nome;
    private String email;

    public ClienteLoginResponse() {
    }

    public Integer getCod() {
        return cod;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}