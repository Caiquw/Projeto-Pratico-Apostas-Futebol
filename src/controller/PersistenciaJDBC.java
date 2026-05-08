package controller;

import model.*;
import java.sql.*;
import java.time.LocalDateTime;

public class PersistenciaJDBC implements Persistencia {

    private static final String URL = "jdbc:sqlite:apostas.db";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    @Override
    public void salvar(Sistema s) {
        try (Connection conn = conectar()) {
            criarTabelas(conn);
            salvarClubes(conn, s);
            salvarCampeonatos(conn, s);
            salvarGrupos(conn, s);
            salvarParticipantes(conn, s);
            salvarPartidas(conn, s);
            salvarApostas(conn, s);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar no banco: " + e.getMessage());
        }
    }

    @Override
    public void carregar(Sistema s) {
        try (Connection conn = conectar()) {
            criarTabelas(conn);
            carregarClubes(conn, s);
            carregarCampeonatos(conn, s);
            carregarGrupos(conn, s);
            carregarParticipantes(conn, s);
            carregarPartidas(conn, s);
            carregarApostas(conn, s);
        } catch (SQLException e) {
            System.err.println("Erro ao carregar do banco: " + e.getMessage());
        }
    }

    private void criarTabelas(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS clubes (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, estado TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS campeonatos (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, ano INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS camp_clube (campeonato_id INTEGER, clube_id INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS grupos (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, campeonato_id INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS participantes (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, grupo_id INTEGER, pontuacao INTEGER DEFAULT 0)");
            st.execute("CREATE TABLE IF NOT EXISTS partidas (id INTEGER PRIMARY KEY AUTOINCREMENT, mandante_id INTEGER, visitante_id INTEGER, data_hora TEXT, campeonato_id INTEGER, realizada INTEGER DEFAULT 0, gols_mandante INTEGER DEFAULT 0, gols_visitante INTEGER DEFAULT 0)");
            st.execute("CREATE TABLE IF NOT EXISTS apostas (id INTEGER PRIMARY KEY AUTOINCREMENT, participante_id INTEGER, partida_id INTEGER, gols_mandante_palpite INTEGER, gols_visitante_palpite INTEGER)");
        }
    }

    private void salvarClubes(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM clubes");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO clubes (nome, estado) VALUES (?, ?)");
        for (Clube c : s.getClubes()) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEstado());
            ps.executeUpdate();
        }
    }

    private void carregarClubes(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT nome, estado FROM clubes");
        while (rs.next()) s.cadastrarClube(rs.getString("nome"), rs.getString("estado"));
    }

    private void salvarCampeonatos(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM campeonatos");
        conn.createStatement().execute("DELETE FROM camp_clube");
        PreparedStatement psC  = conn.prepareStatement("INSERT INTO campeonatos (nome, ano) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement psCC = conn.prepareStatement("INSERT INTO camp_clube (campeonato_id, clube_id) VALUES (?, (SELECT id FROM clubes WHERE nome = ?))");
        for (Campeonato c : s.getCampeonatos()) {
            psC.setString(1, c.getNome());
            psC.setInt(2, c.getAno());
            psC.executeUpdate();
            ResultSet gen = psC.getGeneratedKeys();
            int campId = gen.getInt(1);
            for (Clube cl : c.getClubes()) {
                psCC.setInt(1, campId);
                psCC.setString(2, cl.getNome());
                psCC.executeUpdate();
            }
        }
    }

    private void carregarCampeonatos(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT id, nome, ano FROM campeonatos");
        while (rs.next()) s.cadastrarCampeonato(rs.getString("nome"), rs.getInt("ano"));

        ResultSet rsCC = conn.createStatement().executeQuery(
            "SELECT ca.nome AS camp, cl.nome AS clube FROM camp_clube cc " +
            "JOIN campeonatos ca ON cc.campeonato_id = ca.id " +
            "JOIN clubes cl ON cc.clube_id = cl.id");
        while (rsCC.next()) {
            Campeonato camp = s.findCampeonato(rsCC.getString("camp"));
            Clube clube     = s.findClube(rsCC.getString("clube"));
            if (camp != null && clube != null) s.adicionarClubeAoCampeonato(camp, clube);
        }
    }

    private void salvarGrupos(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM grupos");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO grupos (nome, campeonato_id) VALUES (?, (SELECT id FROM campeonatos WHERE nome = ?))");
        for (Grupo g : s.getGrupos()) {
            ps.setString(1, g.getNome());
            ps.setString(2, g.getCampeonato() != null ? g.getCampeonato().getNome() : "");
            ps.executeUpdate();
        }
    }

    private void carregarGrupos(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT g.nome AS grupo, c.nome AS camp FROM grupos g JOIN campeonatos c ON g.campeonato_id = c.id");
        while (rs.next()) {
            Campeonato camp = s.findCampeonato(rs.getString("camp"));
            s.cadastrarGrupo(rs.getString("grupo"), camp);
        }
    }

    private void salvarParticipantes(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM participantes");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO participantes (nome, grupo_id, pontuacao) VALUES (?, (SELECT id FROM grupos WHERE nome = ?), ?)");
        for (Participante p : s.getParticipantes()) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getGrupo() != null ? p.getGrupo().getNome() : "");
            ps.setInt(3, p.getPontuacaoTotal());
            ps.executeUpdate();
        }
    }

    private void carregarParticipantes(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT p.nome, g.nome AS grupo, p.pontuacao FROM participantes p JOIN grupos g ON p.grupo_id = g.id");
        while (rs.next()) {
            Grupo grupo = s.findGrupo(rs.getString("grupo"));
            if (grupo == null) continue;
            s.cadastrarParticipante(rs.getString("nome"), grupo);
            Participante part = s.findParticipante(rs.getString("nome"));
            if (part != null) part.setPontuacaoTotal(rs.getInt("pontuacao"));
        }
    }

    private void salvarPartidas(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM partidas");
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO partidas (mandante_id, visitante_id, data_hora, campeonato_id, realizada, gols_mandante, gols_visitante) " +
            "VALUES ((SELECT id FROM clubes WHERE nome=?), (SELECT id FROM clubes WHERE nome=?), ?, (SELECT id FROM campeonatos WHERE nome=?), ?, ?, ?)");
        for (Partida p : s.getPartidas()) {
            ps.setString(1, p.getClubeMandante().getNome());
            ps.setString(2, p.getClubeVisitante().getNome());
            ps.setString(3, p.getDataHora().toString());
            ps.setString(4, p.getCampeonato().getNome());
            ps.setInt(5, p.isRealizada() ? 1 : 0);
            ps.setInt(6, p.getGolsMandante());
            ps.setInt(7, p.getGolsVisitante());
            ps.executeUpdate();
        }
    }

    private void carregarPartidas(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT m.nome AS mandante, v.nome AS visitante, p.data_hora, c.nome AS camp, " +
            "p.realizada, p.gols_mandante, p.gols_visitante " +
            "FROM partidas p " +
            "JOIN clubes m ON p.mandante_id = m.id " +
            "JOIN clubes v ON p.visitante_id = v.id " +
            "JOIN campeonatos c ON p.campeonato_id = c.id");
        while (rs.next()) {
            Clube mandante  = s.findClube(rs.getString("mandante"));
            Clube visitante = s.findClube(rs.getString("visitante"));
            Campeonato camp = s.findCampeonato(rs.getString("camp"));
            String dtStr    = rs.getString("data_hora");
            if (mandante == null || visitante == null || camp == null) continue;
            s.cadastrarPartida(mandante, visitante, LocalDateTime.parse(dtStr), camp);
            if (rs.getInt("realizada") == 1) {
                Partida partida = s.findPartida(rs.getString("mandante"), rs.getString("visitante"), dtStr);
                if (partida != null) partida.registrarResultado(rs.getInt("gols_mandante"), rs.getInt("gols_visitante"));
            }
        }
    }

    private void salvarApostas(Connection conn, Sistema s) throws SQLException {
        conn.createStatement().execute("DELETE FROM apostas");
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO apostas (participante_id, partida_id, gols_mandante_palpite, gols_visitante_palpite) " +
            "VALUES ((SELECT id FROM participantes WHERE nome=?), " +
            "(SELECT id FROM partidas WHERE mandante_id=(SELECT id FROM clubes WHERE nome=?) AND visitante_id=(SELECT id FROM clubes WHERE nome=?) AND data_hora=?), ?, ?)");
        for (Aposta a : s.getApostas()) {
            ps.setString(1, a.getParticipante().getNome());
            ps.setString(2, a.getPartida().getClubeMandante().getNome());
            ps.setString(3, a.getPartida().getClubeVisitante().getNome());
            ps.setString(4, a.getPartida().getDataHora().toString());
            ps.setInt(5, a.getGolsMandantePalpite());
            ps.setInt(6, a.getGolsVisitantePalpite());
            ps.executeUpdate();
        }
    }

    private void carregarApostas(Connection conn, Sistema s) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT p.nome AS participante, m.nome AS mandante, v.nome AS visitante, " +
            "pa.data_hora, a.gols_mandante_palpite, a.gols_visitante_palpite " +
            "FROM apostas a " +
            "JOIN participantes p ON a.participante_id = p.id " +
            "JOIN partidas pa ON a.partida_id = pa.id " +
            "JOIN clubes m ON pa.mandante_id = m.id " +
            "JOIN clubes v ON pa.visitante_id = v.id");
        while (rs.next()) {
            Participante part = s.findParticipante(rs.getString("participante"));
            Partida partida   = s.findPartida(rs.getString("mandante"), rs.getString("visitante"), rs.getString("data_hora"));
            if (part == null || partida == null) continue;
            s.registrarAposta(part, partida, rs.getInt("gols_mandante_palpite"), rs.getInt("gols_visitante_palpite"));
        }
    }
}
