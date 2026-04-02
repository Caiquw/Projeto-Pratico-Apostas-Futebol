package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Partida implements Cadastravel {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Clube clubeMandante;
    private Clube clubeVisitante;
    private LocalDateTime dataHora;
    private Campeonato campeonato;
    private int golsMandante;
    private int golsVisitante;
    private boolean realizada;

    public Partida() {
        this.golsMandante = 0;
        this.golsVisitante = 0;
        this.realizada = false;
    }

    public Partida(Clube mandante, Clube visitante, LocalDateTime dataHora, Campeonato campeonato) {
        this();
        this.clubeMandante = mandante;
        this.clubeVisitante = visitante;
        this.dataHora = dataHora;
        this.campeonato = campeonato;
    }

    /** verifica se ainda é possível apostar (até 20 min antes) */
    public boolean isApostaPermitida() {
        if (realizada) return false;
        return LocalDateTime.now().isBefore(dataHora.minusMinutes(20));
    }

    public void registrarResultado(int golsMandante, int golsVisitante) {
        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.realizada = true;
    }

    /** retorna o resultado como String: nome do vencedor ou "Empate" */
    public String getResultado() {
        if (!realizada) return "Não realizada";
        if (golsMandante > golsVisitante) return clubeMandante.getNome();
        if (golsVisitante > golsMandante) return clubeVisitante.getNome();
        return "Empate";
    }

    @Override
    public String exibirInfo() {
        String placar = realizada ? (golsMandante + " x " + golsVisitante) : "vs";
        String data = (dataHora != null) ? dataHora.format(FORMATTER) : "-";
        return clubeMandante + " " + placar + " " + clubeVisitante + " | " + data;
    }

    @Override
    public String toString() {
        return exibirInfo();
    }

    public Clube getClubeMandante() { return clubeMandante; }
    public void setClubeMandante(Clube c) { this.clubeMandante = c; }
    public Clube getClubeVisitante() { return clubeVisitante; }
    public void setClubeVisitante(Clube c) { this.clubeVisitante = c; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato c) { this.campeonato = c; }
    public int getGolsMandante() { return golsMandante; }
    public int getGolsVisitante() { return golsVisitante; }
    public boolean isRealizada() { return realizada; }
}
