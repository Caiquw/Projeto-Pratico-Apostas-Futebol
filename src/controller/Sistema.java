package controller;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class Sistema {
    private static final int MAX_GRUPOS = 5;
    private static final int MAX_PARTICIPANTES = 5;

    private static Sistema instancia;

    private final List<Clube> clubes;
    private final List<Campeonato> campeonatos;
    private final List<Partida> partidas;
    private final List<Grupo> grupos;
    private final List<Participante> participantes;
    private final List<Aposta> apostas;
    private final Administrador administrador;

    private Sistema() {
        clubes = new ArrayList<>();
        campeonatos = new ArrayList<>();
        partidas = new ArrayList<>();
        grupos = new ArrayList<>();
        participantes = new ArrayList<>();
        apostas = new ArrayList<>();
        administrador = new Administrador("Administrador", "admin");
    }

    public static Sistema getInstancia() {
        if (instancia == null) {
            instancia = new Sistema();
        }
        return instancia;
    }


    public String cadastrarClube(Clube clube) {
        for (Clube c : clubes) {

            if (c.getNome().equalsIgnoreCase(clube.getNome())) return "Clube já cadastrado";
        }
        clubes.add(clube);

        return "ok";
    }


    public String cadastrarCampeonato(Campeonato campeonato) {
        for (Campeonato c : campeonatos) {
            if (c.getNome().equalsIgnoreCase(campeonato.getNome())) return "Campeonato já cadastrado";
        }
        campeonatos.add(campeonato);
        return "ok";
    }


    public String cadastrarPartida(Partida partida) {
        if (partida.getClubeMandante().equals(partida.getClubeVisitante())) {
            return "Os clubes da partida não podem ser iguais";
        }
        partidas.add(partida);
        return "ok";
    }


    public String cadastrarGrupo(Grupo grupo) {
        if (grupos.size() >= MAX_GRUPOS) return "Limite de grupos atingido (máx. 5)";
        for (Grupo g : grupos) {
            if (g.getNome().equalsIgnoreCase(grupo.getNome())) return "Nome de grupo já utilizado";
        }
        grupos.add(grupo);
        return "ok";
    }


    public String cadastrarParticipante(Participante participante) {
        if (participantes.size() >= MAX_PARTICIPANTES) return "Limite de participantes atingido (máx. 5)";
        for (Participante p : participantes) {
            if (p.getLogin().equalsIgnoreCase(participante.getLogin())) return "Login já utilizado";
        }
        participantes.add(participante);
        return "ok";
    }


    public String registrarAposta(Aposta aposta) {
        if (!aposta.getPartida().isApostaPermitida()) {
            return "Apostas encerradas: a partida já ocorreu ou está a menos de 20 minutos";
        }
        for (Aposta a : apostas) {
            if (a.getParticipante().equals(aposta.getParticipante())
                    && a.getPartida().equals(aposta.getPartida())) {
                return "Este participante já apostou nesta partida";
            }
        }
        apostas.add(aposta);
        return "ok";
    }


    public void registrarResultado(Partida partida, int golsMandante, int golsVisitante) {
        partida.registrarResultado(golsMandante, golsVisitante);
        for (Aposta a : apostas) {
            if (a.getPartida().equals(partida)) {
                int pts = a.calcularPontuacao();
                a.getParticipante().adicionarPontos(pts);
            }
        }
    }

    public List<Aposta> getApostasDaPartida(Partida partida) {
        List<Aposta> resultado = new ArrayList<>();
        for (Aposta a : apostas) {
            if (a.getPartida().equals(partida)) resultado.add(a);
        }
        return resultado;
    }


    public List<Clube> getClubes() { return clubes; }
    public List<Campeonato> getCampeonatos() { return campeonatos; }
    public List<Partida> getPartidas() { return partidas; }
    public List<Grupo> getGrupos() { return grupos; }
    public List<Participante> getParticipantes() { return participantes; }
    public List<Aposta> getApostas() { return apostas; }
    public Administrador getAdministrador() { return administrador; }
}
