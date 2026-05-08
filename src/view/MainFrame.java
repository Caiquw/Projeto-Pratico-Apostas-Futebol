package view;

import controller.Sistema;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {

    private final Sistema sistema = Sistema.getInstancia();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final boolean isAdmin;
    private JTextArea taLog;

    public MainFrame(boolean isAdmin) {
        super("Sistema de Apostas - Campeonato de Futebol" + (isAdmin ? " [ADMIN]" : " [USUÁRIO]"));
        this.isAdmin = isAdmin;
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarClubes();
        construirUI();
        setVisible(true);
    }

    private void inicializarClubes() {
        String[][] clubes = {
            {"Corinthians","SP"}, {"Palmeiras","SP"}, {"São Paulo","SP"}, {"Santos","SP"},
            {"Flamengo","RJ"},   {"Vasco","RJ"},      {"Fluminense","RJ"},{"Botafogo","RJ"},
            {"Atlético Mineiro","MG"}, {"Cruzeiro","MG"}, {"Grêmio","RS"}, {"Internacional","RS"}
        };
        for (String[] c : clubes) sistema.cadastrarClube(c[0], c[1]);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel painelBotoes = new JPanel(new GridLayout(0, 1, 4, 4));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        JButton btnClubes       = new JButton("Cadastrar Clube");
        JButton btnCampeonato   = new JButton("Cadastrar Campeonato");
        JButton btnAddClube     = new JButton("Add Clube ao Camp.");
        JButton btnPartida      = new JButton("Cadastrar Partida");
        JButton btnGrupo        = new JButton("Cadastrar Grupo");
        JButton btnParticipante = new JButton("Cadastrar Participante");
        JButton btnAposta       = new JButton("Registrar Aposta");

        painelBotoes.add(btnClubes);
        painelBotoes.add(btnCampeonato);
        painelBotoes.add(btnAddClube);
        painelBotoes.add(btnPartida);
        painelBotoes.add(btnGrupo);
        painelBotoes.add(btnParticipante);
        painelBotoes.add(btnAposta);

        if (isAdmin) {
            JButton btnResultado = new JButton("Registrar Resultado");
            painelBotoes.add(btnResultado);
            btnResultado.addActionListener(e -> registrarResultado());
        }

        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(taLog);
        scroll.setBorder(BorderFactory.createTitledBorder("Saída do Sistema"));

        JButton btnClass  = new JButton("Ver Classificação");
        JButton btnLogoff = new JButton("Logoff");
        btnClass.addActionListener(e -> verClassificacao());
        btnLogoff.addActionListener(e -> { dispose(); new LoginFrame(); });
        JPanel rodape = new JPanel();
        rodape.add(btnClass);
        rodape.add(btnLogoff);

        add(painelBotoes, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        btnClubes.addActionListener(e -> cadastrarClube());
        btnCampeonato.addActionListener(e -> cadastrarCampeonato());
        btnAddClube.addActionListener(e -> adicionarClubeAoCampeonato());
        btnPartida.addActionListener(e -> cadastrarPartida());
        btnGrupo.addActionListener(e -> cadastrarGrupo());
        btnParticipante.addActionListener(e -> cadastrarParticipante());
        btnAposta.addActionListener(e -> registrarAposta());

        log("Bem-vindo! Perfil: " + (isAdmin ? "Administrador" : "Usuário"));
    }

    private void cadastrarClube() {
        String nome   = JOptionPane.showInputDialog(this, "Nome do clube:");
        if (nome == null || nome.isBlank()) return;
        String estado = JOptionPane.showInputDialog(this, "Estado (sigla):");
        if (estado == null || estado.isBlank()) return;
        String res = sistema.cadastrarClube(nome.trim(), estado.trim().toUpperCase());
        if ("ok".equals(res)) log("Clube cadastrado: " + nome.trim() + " - " + estado.trim().toUpperCase());
        else log("Erro: " + res);
    }

    private void cadastrarCampeonato() {
        String nome   = JOptionPane.showInputDialog(this, "Nome do campeonato:");
        if (nome == null || nome.isBlank()) return;
        String anoStr = JOptionPane.showInputDialog(this, "Ano:");
        if (anoStr == null || anoStr.isBlank()) return;
        try {
            String res = sistema.cadastrarCampeonato(nome.trim(), Integer.parseInt(anoStr.trim()));
            if ("ok".equals(res)) log("Campeonato cadastrado: " + nome.trim());
            else log("Erro: " + res);
        } catch (NumberFormatException ex) { log("Erro: ano inválido."); }
    }

    private void adicionarClubeAoCampeonato() {
        if (sistema.getCampeonatos().isEmpty() || sistema.getClubes().isEmpty()) {
            log("Cadastre ao menos um campeonato antes."); return;
        }
        Campeonato camp = selecionar("Campeonato:", sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;
        Clube clube = selecionar("Clube:", sistema.getClubes().toArray(), sistema.getClubes().get(0));
        if (clube == null) return;
        String res = sistema.adicionarClubeAoCampeonato(camp, clube);
        if ("ok".equals(res)) log("Clube " + clube + " adicionado ao campeonato " + camp);
        else log("Erro: " + res);
    }

    private void cadastrarPartida() {
        if (sistema.getCampeonatos().isEmpty()) { log("Cadastre um campeonato primeiro."); return; }
        Campeonato camp = selecionar("Campeonato:", sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;
        if (camp.getClubes().size() < 2) { log("O campeonato precisa de ao menos 2 clubes."); return; }
        Clube mandante  = selecionar("Mandante:", camp.getClubes().toArray(), camp.getClubes().get(0));
        if (mandante == null) return;
        Clube visitante = selecionar("Visitante:", camp.getClubes().toArray(), camp.getClubes().get(0));
        if (visitante == null) return;
        String dtStr = JOptionPane.showInputDialog(this, "Data e hora (dd/MM/yyyy HH:mm):");
        if (dtStr == null || dtStr.isBlank()) return;
        try {
            LocalDateTime dt = LocalDateTime.parse(dtStr.trim(), DT_FMT);
            String res = sistema.cadastrarPartida(mandante, visitante, dt, camp);
            if ("ok".equals(res)) log("Partida cadastrada: " + mandante + " x " + visitante + " em " + dtStr);
            else log("Erro: " + res);
        } catch (DateTimeParseException ex) { log("Erro: use o formato dd/MM/yyyy HH:mm"); }
    }

    private void cadastrarGrupo() {
        if (sistema.getCampeonatos().isEmpty()) { log("Cadastre um campeonato primeiro."); return; }
        String nome = JOptionPane.showInputDialog(this, "Nome do grupo:");
        if (nome == null || nome.isBlank()) return;
        Campeonato camp = selecionar("Campeonato:", sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;
        String res = sistema.cadastrarGrupo(nome.trim(), camp);
        if ("ok".equals(res)) log("Grupo criado: " + nome.trim());
        else log("Erro: " + res);
    }

    private void cadastrarParticipante() {
        if (sistema.getGrupos().isEmpty()) { log("Crie um grupo primeiro."); return; }
        String nome = JOptionPane.showInputDialog(this, "Nome do participante:");
        if (nome == null || nome.isBlank()) return;
        Grupo grupo = selecionar("Ingressar em qual grupo?", sistema.getGrupos().toArray(), sistema.getGrupos().get(0));
        if (grupo == null) return;
        String res = sistema.cadastrarParticipante(nome.trim(), grupo);
        if ("ok".equals(res)) log("Participante " + nome.trim() + " cadastrado no grupo " + grupo);
        else log("Erro: " + res);
    }

    private void registrarAposta() {
        if (sistema.getGrupos().isEmpty())   { log("Nenhum grupo cadastrado."); return; }
        if (sistema.getPartidas().isEmpty()) { log("Nenhuma partida cadastrada."); return; }
        Grupo grupo = selecionar("Grupo:", sistema.getGrupos().toArray(), sistema.getGrupos().get(0));
        if (grupo == null) return;
        List<Participante> membros = grupo.getParticipantes();
        if (membros.isEmpty()) { log("O grupo " + grupo + " não tem participantes."); return; }
        Participante p = selecionar("Participante:", membros.toArray(), membros.get(0));
        if (p == null) return;
        Partida partida = selecionar("Partida:", sistema.getPartidas().toArray(), sistema.getPartidas().get(0));
        if (partida == null) return;
        if (sistema.jaApostou(p, partida)) { log("Erro: " + p + " já apostou nesta partida."); return; }
        String gMStr = JOptionPane.showInputDialog(this, "Gols " + partida.getClubeMandante() + ":");
        String gVStr = JOptionPane.showInputDialog(this, "Gols " + partida.getClubeVisitante() + ":");
        if (gMStr == null || gVStr == null) return;
        try {
            String res = sistema.registrarAposta(p, partida, Integer.parseInt(gMStr.trim()), Integer.parseInt(gVStr.trim()));
            if ("ok".equals(res)) log("Aposta de " + p + ": " + partida.getClubeMandante() + " " + gMStr.trim() + " x " + gVStr.trim() + " " + partida.getClubeVisitante());
            else log("Erro: " + res);
        } catch (NumberFormatException ex) { log("Erro: número inválido."); }
    }

    private void registrarResultado() {
        if (sistema.getPartidas().isEmpty()) { log("Nenhuma partida cadastrada."); return; }
        Partida partida = selecionar("Partida:", sistema.getPartidas().toArray(), sistema.getPartidas().get(0));
        if (partida == null) return;
        if (partida.isRealizada()) { log("Resultado já registrado."); return; }
        String gMStr = JOptionPane.showInputDialog(this, "Gols " + partida.getClubeMandante() + ":");
        String gVStr = JOptionPane.showInputDialog(this, "Gols " + partida.getClubeVisitante() + ":");
        if (gMStr == null || gVStr == null) return;
        try {
            sistema.registrarResultado(partida, Integer.parseInt(gMStr.trim()), Integer.parseInt(gVStr.trim()));
            log("Resultado: " + partida.getClubeMandante() + " " + gMStr.trim() + " x " + gVStr.trim() + " " + partida.getClubeVisitante());
            log("Pontos distribuídos.");
        } catch (NumberFormatException ex) { log("Erro: número inválido."); }
    }

    private void verClassificacao() {
        if (sistema.getGrupos().isEmpty()) { log("Nenhum grupo cadastrado."); return; }
        Grupo grupo = selecionar("Grupo:", sistema.getGrupos().toArray(), sistema.getGrupos().get(0));
        if (grupo == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== CLASSIFICAÇÃO: ").append(grupo.getNome().toUpperCase()).append(" ===\n");
        sb.append(String.format("%-4s %-20s %s%n", "Pos", "Nome", "Pontos"));
        sb.append("------------------------------------\n");
        int pos = 1;
        for (Participante p : grupo.getClassificacao())
            sb.append(String.format("%-4d %-20s %d%n", pos++, p.getNome(), p.getPontuacaoTotal()));
        log(sb.toString());
    }

    @SuppressWarnings("unchecked")
    private <T> T selecionar(String msg, Object[] opcoes, T padrao) {
        return (T) JOptionPane.showInputDialog(this, msg, "Seleção",
                JOptionPane.PLAIN_MESSAGE, null, opcoes, padrao);
    }

    private void log(String msg) {
        taLog.append(msg + "\n");
        taLog.setCaretPosition(taLog.getDocument().getLength());
    }
}
