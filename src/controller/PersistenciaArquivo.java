package controller;

import model.*;
import java.io.*;
import java.time.LocalDateTime;

public class PersistenciaArquivo implements Persistencia {

    private static final String DIR            = "data/";
    private static final String ARQ_CLUBES     = DIR + "clubes.txt";
    private static final String ARQ_CAMPEONATOS= DIR + "campeonatos.txt";
    private static final String ARQ_CAMP_CLUBE = DIR + "camp_clubes.txt";
    private static final String ARQ_GRUPOS     = DIR + "grupos.txt";
    private static final String ARQ_PARTIC     = DIR + "participantes.txt";
    private static final String ARQ_PARTIDAS   = DIR + "partidas.txt";
    private static final String ARQ_APOSTAS    = DIR + "apostas.txt";

    @Override
    public void salvar(Sistema s) {
        new File(DIR).mkdirs();
        salvarClubes(s);
        salvarCampeonatos(s);
        salvarGrupos(s);
        salvarParticipantes(s);
        salvarPartidas(s);
        salvarApostas(s);
    }

    @Override
    public void carregar(Sistema s) {
        carregarClubes(s);
        carregarCampeonatos(s);
        carregarGrupos(s);
        carregarParticipantes(s);
        carregarPartidas(s);
        carregarApostas(s);
    }

    private void salvarClubes(Sistema s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQ_CLUBES))) {
            for (Clube c : s.getClubes()) {
                bw.write(c.getNome() + ";" + c.getEstado());
                bw.newLine();
            }
        } catch (IOException e) { err("salvar clubes", e); }
    }

    private void carregarClubes(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_CLUBES))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                s.cadastrarClube(p[0], p[1]);
            }
        } catch (IOException e) { err("carregar clubes", e); }
    }

    private void salvarCampeonatos(Sistema s) {
        try (BufferedWriter bwC = new BufferedWriter(new FileWriter(ARQ_CAMPEONATOS));
             BufferedWriter bwCC = new BufferedWriter(new FileWriter(ARQ_CAMP_CLUBE))) {
            for (Campeonato c : s.getCampeonatos()) {
                bwC.write(c.getNome() + ";" + c.getAno());
                bwC.newLine();
                for (Clube cl : c.getClubes()) {
                    bwCC.write(c.getNome() + ";" + cl.getNome());
                    bwCC.newLine();
                }
            }
        } catch (IOException e) { err("salvar campeonatos", e); }
    }

    private void carregarCampeonatos(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_CAMPEONATOS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                s.cadastrarCampeonato(p[0], Integer.parseInt(p[1]));
            }
        } catch (IOException e) { err("carregar campeonatos", e); }

        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_CAMP_CLUBE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                Campeonato camp = s.findCampeonato(p[0]);
                Clube clube     = s.findClube(p[1]);
                if (camp != null && clube != null) s.adicionarClubeAoCampeonato(camp, clube);
            }
        } catch (IOException e) { err("carregar camp_clubes", e); }
    }

    private void salvarGrupos(Sistema s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQ_GRUPOS))) {
            for (Grupo g : s.getGrupos()) {
                String campNome = g.getCampeonato() != null ? g.getCampeonato().getNome() : "";
                bw.write(g.getNome() + ";" + campNome);
                bw.newLine();
            }
        } catch (IOException e) { err("salvar grupos", e); }
    }

    private void carregarGrupos(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_GRUPOS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                Campeonato camp = s.findCampeonato(p[1]);
                s.cadastrarGrupo(p[0], camp);
            }
        } catch (IOException e) { err("carregar grupos", e); }
    }

    private void salvarParticipantes(Sistema s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQ_PARTIC))) {
            for (Participante p : s.getParticipantes()) {
                String grupoNome = p.getGrupo() != null ? p.getGrupo().getNome() : "";
                bw.write(p.getNome() + ";" + grupoNome + ";" + p.getPontuacaoTotal());
                bw.newLine();
            }
        } catch (IOException e) { err("salvar participantes", e); }
    }

    private void carregarParticipantes(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_PARTIC))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                Grupo grupo = s.findGrupo(p[1]);
                if (grupo != null) {
                    s.cadastrarParticipante(p[0], grupo);
                    Participante part = s.findParticipante(p[0]);
                    if (part != null) part.setPontuacaoTotal(Integer.parseInt(p[2]));
                }
            }
        } catch (IOException e) { err("carregar participantes", e); }
    }

    private void salvarPartidas(Sistema s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQ_PARTIDAS))) {
            for (Partida p : s.getPartidas()) {
                bw.write(p.getClubeMandante().getNome() + ";"
                        + p.getClubeVisitante().getNome() + ";"
                        + p.getDataHora().toString() + ";"
                        + p.getCampeonato().getNome() + ";"
                        + p.isRealizada() + ";"
                        + p.getGolsMandante() + ";"
                        + p.getGolsVisitante());
                bw.newLine();
            }
        } catch (IOException e) { err("salvar partidas", e); }
    }

    private void carregarPartidas(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_PARTIDAS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p  = linha.split(";");
                Clube mandante  = s.findClube(p[0]);
                Clube visitante = s.findClube(p[1]);
                LocalDateTime dt = LocalDateTime.parse(p[2]);
                Campeonato camp  = s.findCampeonato(p[3]);
                if (mandante == null || visitante == null || camp == null) continue;
                s.cadastrarPartida(mandante, visitante, dt, camp);
                if (Boolean.parseBoolean(p[4])) {
                    Partida partida = s.findPartida(p[0], p[1], p[2]);
                    if (partida != null) partida.registrarResultado(Integer.parseInt(p[5]), Integer.parseInt(p[6]));
                }
            }
        } catch (IOException e) { err("carregar partidas", e); }
    }

    private void salvarApostas(Sistema s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQ_APOSTAS))) {
            for (Aposta a : s.getApostas()) {
                bw.write(a.getParticipante().getNome() + ";"
                        + a.getPartida().getClubeMandante().getNome() + ";"
                        + a.getPartida().getClubeVisitante().getNome() + ";"
                        + a.getPartida().getDataHora().toString() + ";"
                        + a.getGolsMandantePalpite() + ";"
                        + a.getGolsVisitantePalpite());
                bw.newLine();
            }
        } catch (IOException e) { err("salvar apostas", e); }
    }

    private void carregarApostas(Sistema s) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQ_APOSTAS))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                Participante part = s.findParticipante(p[0]);
                Partida partida   = s.findPartida(p[1], p[2], p[3]);
                if (part == null || partida == null) continue;
                s.registrarAposta(part, partida, Integer.parseInt(p[4]), Integer.parseInt(p[5]));
            }
        } catch (IOException e) { err("carregar apostas", e); }
    }

    private void err(String acao, Exception e) {
        System.err.println("Erro ao " + acao + ": " + e.getMessage());
    }
}
