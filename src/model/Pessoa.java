package model;

public abstract class Pessoa implements Cadastravel {
    private String nome;
    private String login;

    public Pessoa() {
        this.nome = "";
        this.login = "";
    }

    public Pessoa(String nome, String login) {
        this.nome = nome;
        this.login = login;
    }

    public abstract String getTipo();

    @Override
    public String exibirInfo() {
        return "[" + getTipo() + "] " + nome + " (login: " + login + ")";
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
}
