package model;

public class Participante extends Pessoa {
    private int pontuacaoTotal;
    private Grupo grupo;

    public Participante() {
        super();
        this.pontuacaoTotal = 0;
    }

    public Participante(String nome, String login) {
        super(nome, login);
        this.pontuacaoTotal = 0;
    }

    @Override
    public String getTipo() {
        return "Participante";
    }

    @Override
    public String exibirInfo() {
        String grupoNome = (grupo != null) ? grupo.getNome() : "Sem grupo";
        return "[Participante] " + getNome() + " | Grupo: " + grupoNome + " | Pontos: " + pontuacaoTotal;
    }

    public void adicionarPontos(int pontos) {
        this.pontuacaoTotal += pontos;
    }

    public int getPontuacaoTotal() { return pontuacaoTotal; }
    public void setPontuacaoTotal(int pontuacaoTotal) { this.pontuacaoTotal = pontuacaoTotal; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
}
