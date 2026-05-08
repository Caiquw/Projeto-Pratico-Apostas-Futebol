package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Grupo implements Cadastravel {
    private static final int MAX_PARTICIPANTES = 5;
    private String nome;
    private Campeonato campeonato;
    private List<Participante> participantes;

    public Grupo() {
        this.participantes = new ArrayList<>();
    }

    public Grupo(String nome, Campeonato campeonato) {
        this();
        this.nome = nome;
        this.campeonato = campeonato;
    }

    public String adicionarParticipante(Participante p) {
        if (participantes.size() >= MAX_PARTICIPANTES)
            return "Limite de participantes atingido (máx. 5)";
        if (participantes.contains(p))
            return "Participante já está no grupo";
        participantes.add(p);
        p.setGrupo(this);
        return "ok";
    }

    /** retorna participantes ordenados por pontuação (desc) */
    public List<Participante> getClassificacao() {
        List<Participante> ranking = new ArrayList<>(participantes);
        ranking.sort(Comparator.comparingInt(Participante::getPontuacaoTotal).reversed());
        return ranking;
    }

    @Override
    public String exibirInfo() {
        return "[Grupo] " + nome + " | Campeonato: " + (campeonato != null ? campeonato.getNome() : "-")
            + " | Participantes: " + participantes.size() + "/" + MAX_PARTICIPANTES;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato c) { this.campeonato = c; }
    public List<Participante> getParticipantes() { return participantes; }
}
