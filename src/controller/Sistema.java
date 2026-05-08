package controller;

import model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sistema {

    private static final int MAX_GRUPOS = 5;
    private static Sistema instancia;

    private final List<Clube> clubes;
    private final List<Campeonato> campeonatos;
    private final List<Partida> partidas;
    private final List<Grupo> grupos;
    private final List<Participante> participantes;
    private final List<Aposta> apostas;
    private final Administrador administrador;

    private Sistema() {
        clubes        = new ArrayList<>();
        campeonatos   = new ArrayList<>();
        partidas      = new ArrayList<>();
        grupos        = new ArrayList<>();
        participantes = new ArrayList<>();
        apostas       = new ArrayList<>();
        administrador = new Administrador("Administrador", "admin");
    }

    public static Sistema getInstancia() {
        if (instancia == null) instancia = new Sistema();
        return instancia;
    }

    public String cadastrarClube(String nome, String estado) {
        for (Clube c : clubes)
            if (c.getNome().equalsIgnoreCase(nome)) return "Clube já cadastrado";
        clubes.add(new Clube(nome, estado));
        return "ok";
    }

    public String cadastrarCampeonato(String nome, int ano) {
        for (Campeonato c : campeonatos)
            if (c.getNome().equalsIgnoreCase(nome)) return "Campeonato já cadastrado";
        campeonatos.add(new Campeonato(nome, ano));
        return "ok";
    }

    public String adicionarClubeAoCampeonato(Campeonato campeonato, Clube clube) {
        return campeonato.adicionarClube(clube);
    }

    public String cadastrarPartida(Clube mandante, Clube visitante, LocalDateTime dataHora, Campeonato campeonato) {
        if (mandante.equals(visitante)) return "Os clubes da partida não podem ser iguais";
        partidas.add(new Partida(mandante, visitante, dataHora, campeonato));
        return "ok";
    }

    public String cadastrarGrupo(String nome, Campeonato campeonato) {
        if (grupos.size() >= MAX_GRUPOS) return "Limite de grupos atingido (máx. 5)";
        for (Grupo g : grupos)
            if (g.getNome().equalsIgnoreCase(nome)) return "Nome de grupo já utilizado";
        grupos.add(new Grupo(nome, campeonato));
        return "ok";
    }

    public String cadastrarParticipante(String nome, Grupo grupo) {
        Participante p = new Participante(nome, nome);
        String res = grupo.adicionarParticipante(p);
        if ("ok".equals(res)) participantes.add(p);
        return res;
    }

    public String registrarAposta(Participante participante, Partida partida, int golsMandante, int golsVisitante) {
        if (!partida.isApostaPermitida())
            return "Apostas encerradas: a partida já ocorreu ou está a menos de 20 minutos";
        for (Aposta a : apostas)
            if (a.getParticipante().equals(participante) && a.getPartida().equals(partida))
                return participante.getNome() + " já apostou nesta partida";
        apostas.add(new Aposta(participante, partida, golsMandante, golsVisitante));
        return "ok";
    }

    public void registrarResultado(Partida partida, int golsMandante, int golsVisitante) {
        partida.registrarResultado(golsMandante, golsVisitante);
        for (Aposta a : apostas)
            if (a.getPartida().equals(partida)) {
                int pts = a.calcularPontuacao();
                a.getParticipante().adicionarPontos(pts);
            }
    }

    public boolean jaApostou(Participante participante, Partida partida) {
        for (Aposta a : apostas)
            if (a.getParticipante().equals(participante) && a.getPartida().equals(partida)) return true;
        return false;
    }

    public Clube findClube(String nome) {
        for (Clube c : clubes) if (c.getNome().equals(nome)) return c;
        return null;
    }

    public Campeonato findCampeonato(String nome) {
        for (Campeonato c : campeonatos) if (c.getNome().equals(nome)) return c;
        return null;
    }

    public Grupo findGrupo(String nome) {
        for (Grupo g : grupos) if (g.getNome().equals(nome)) return g;
        return null;
    }

    public Participante findParticipante(String nome) {
        for (Participante p : participantes) if (p.getNome().equals(nome)) return p;
        return null;
    }

    public Partida findPartida(String mandante, String visitante, String dataHora) {
        for (Partida p : partidas)
            if (p.getClubeMandante().getNome().equals(mandante)
                    && p.getClubeVisitante().getNome().equals(visitante)
                    && p.getDataHora().toString().equals(dataHora)) return p;
        return null;
    }

    public List<Clube>        getClubes()        { return clubes; }
    public List<Campeonato>   getCampeonatos()   { return campeonatos; }
    public List<Partida>      getPartidas()      { return partidas; }
    public List<Grupo>        getGrupos()        { return grupos; }
    public List<Participante> getParticipantes() { return participantes; }
    public List<Aposta>       getApostas()       { return apostas; }
    public Administrador      getAdministrador() { return administrador; }
}
