package model;

public class Administrador extends Pessoa {


    public Administrador() {
        super();
    }


    public Administrador(String nome, String login) {
        super(nome, login);
    }

    @Override
    public String getTipo() {
        return "Administrador";
    }


    @Override
    public String exibirInfo() {
        return "[ADMINISTRADOR] " + getNome();
    }
}
