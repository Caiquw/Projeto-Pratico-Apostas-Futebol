package model;

public abstract class EntidadeEsportiva implements Cadastravel {
    private String nome;

    public EntidadeEsportiva() {
        this.nome = "";
    }

    public EntidadeEsportiva(String nome) {
        this.nome = nome;
    }

    public abstract String getCategoria();

    @Override
    public String exibirInfo() {
        return "[" + getCategoria() + "] " + nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
