package model;

import java.util.ArrayList;
import java.util.List;

public class Campeonato extends EntidadeEsportiva {
    private static final int MAX_CLUBES = 8;
    private int ano;
    private List<Clube> clubes;

    public Campeonato() {
        super();
        this.ano = 2025;
        this.clubes = new ArrayList<>();
    }

    public Campeonato(String nome, int ano) {
        super(nome);
        this.ano = ano;
        this.clubes = new ArrayList<>();
    }

    @Override
    public String getCategoria() {
        return "Campeonato";
    }

    @Override
    public String exibirInfo() {
        return "[Campeonato] " + getNome() + " " + ano + " | Clubes: " + clubes.size() + "/" + MAX_CLUBES;
    }

    public String adicionarClube(Clube clube) {
        if (clubes.size() >= MAX_CLUBES) return "Limite de clubes atingido (máx. 8)";
        if (clubes.contains(clube)) return "Clube já cadastrado neste campeonato";
        clubes.add(clube);
        return "ok";
    }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public List<Clube> getClubes() { return clubes; }
}
