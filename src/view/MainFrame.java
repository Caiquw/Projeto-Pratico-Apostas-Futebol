package view;

import controller.Sistema;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainFrame extends JFrame {

    private final Sistema sistema = Sistema.getInstancia();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTextArea taLog;

    public MainFrame() {
        super("Sistema de Apostas - Campeonato de Futebol");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarClubes();
        construirUI();
        setVisible(true);
    }

    private void inicializarClubes() {
        Clube[] clubes = {
                new Clube("Corinthians", "SP"),
                new Clube("Palmeiras", "SP"),
                new Clube("São Paulo", "SP"),
                new Clube("Santos", "SP"),
                new Clube("Flamengo", "RJ"),
                new Clube("Vasco", "RJ"),
                new Clube("Fluminense", "RJ"),
                new Clube("Botafogo", "RJ"),
                new Clube("Atlético Mineiro", "MG"),
                new Clube("Cruzeiro", "MG"),
                new Clube("Grêmio", "RS"),
                new Clube("Internacional", "RS")
        };
        for (Clube c : clubes) {
            sistema.cadastrarClube(c);
        }
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        // Painel de botões à esquerda
        JPanel painelBotoes = new JPanel(new GridLayout(8, 1, 4, 4));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        JButton btnClube        = new JButton("Cadastrar Clube");
        JButton btnCampeonato   = new JButton("Cadastrar Campeonato");
        JButton btnAddClube     = new JButton("Add Clube ao Camp.");
        JButton btnPartida      = new JButton("Cadastrar Partida");
        JButton btnGrupo        = new JButton("Cadastrar Grupo");
        JButton btnParticipante = new JButton("Cadastrar Participante");
        JButton btnAposta       = new JButton("Registrar Aposta");
        JButton btnResultado    = new JButton("Registrar Resultado");

        painelBotoes.add(btnClube);
        painelBotoes.add(btnCampeonato);
        painelBotoes.add(btnAddClube);
        painelBotoes.add(btnPartida);
        painelBotoes.add(btnGrupo);
        painelBotoes.add(btnParticipante);
        painelBotoes.add(btnAposta);
        painelBotoes.add(btnResultado);

        // Área de log/saída
        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(taLog);
        scroll.setBorder(BorderFactory.createTitledBorder("Saída do Sistema"));

        // Rodapé
        JButton btnClass = new JButton("Ver Classificação do Grupo");
        btnClass.addActionListener(e -> verClassificacao());
        JPanel rodape = new JPanel();
        rodape.add(btnClass);

        add(painelBotoes, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        // Ações dos botões
        btnClube.addActionListener(e -> cadastrarClube());
        btnCampeonato.addActionListener(e -> cadastrarCampeonato());
        btnAddClube.addActionListener(e -> adicionarClubeAoCampeonato());
        btnPartida.addActionListener(e -> cadastrarPartida());
        btnGrupo.addActionListener(e -> cadastrarGrupo());
        btnParticipante.addActionListener(e -> cadastrarParticipante());
        btnAposta.addActionListener(e -> registrarAposta());
        btnResultado.addActionListener(e -> registrarResultado());

        log("Bem-vindo! Use os botões para operar o sistema.");
    }

    // =========================================================
    // AÇÕES
    // =========================================================

    private void cadastrarClube() {
        String nome = JOptionPane.showInputDialog(this, "Nome do clube:");
        if (nome == null || nome.isBlank()) return;
        String estado = JOptionPane.showInputDialog(this, "Estado do clube:");
        if (estado == null || estado.isBlank()) return;

        String res = sistema.cadastrarClube(new Clube(nome.trim(), estado.trim()));
        if ("ok".equals(res)) log("Clube cadastrado: " + nome + " (" + estado + ")");
        else log("Erro: " + res);
    }

    private void cadastrarCampeonato() {
        String nome = JOptionPane.showInputDialog(this, "Nome do campeonato:");
        if (nome == null || nome.isBlank()) return;
        String anoStr = JOptionPane.showInputDialog(this, "Ano:");
        if (anoStr == null || anoStr.isBlank()) return;
        try {
            int ano = Integer.parseInt(anoStr.trim());
            String res = sistema.cadastrarCampeonato(new Campeonato(nome.trim(), ano));
            if ("ok".equals(res)) log("Campeonato cadastrado: " + nome + " " + ano);
            else log("Erro: " + res);
        } catch (NumberFormatException ex) {
            log("Erro: ano inválido.");
        }
    }

    private void adicionarClubeAoCampeonato() {
        if (sistema.getCampeonatos().isEmpty() || sistema.getClubes().isEmpty()) {
            log("Cadastre ao menos um campeonato e um clube antes."); return;
        }
        Campeonato camp = (Campeonato) JOptionPane.showInputDialog(this,
                "Selecione o campeonato:", "Campeonato",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;

        Clube clube = (Clube) JOptionPane.showInputDialog(this,
                "Selecione o clube:", "Clube",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getClubes().toArray(), sistema.getClubes().get(0));
        if (clube == null) return;

        String res = camp.adicionarClube(clube);
        if ("ok".equals(res)) log("Clube " + clube + " adicionado ao campeonato " + camp);
        else log("Erro: " + res);
    }

    private void cadastrarPartida() {
        if (sistema.getCampeonatos().isEmpty()) { log("Cadastre um campeonato primeiro."); return; }

        Campeonato camp = (Campeonato) JOptionPane.showInputDialog(this,
                "Campeonato:", "Nova Partida",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;
        if (camp.getClubes().size() < 2) { log("O campeonato precisa de ao menos 2 clubes."); return; }

        Clube mandante = (Clube) JOptionPane.showInputDialog(this,
                "Clube mandante:", "Nova Partida",
                JOptionPane.PLAIN_MESSAGE, null,
                camp.getClubes().toArray(), camp.getClubes().get(0));
        if (mandante == null) return;

        Clube visitante = (Clube) JOptionPane.showInputDialog(this,
                "Clube visitante:", "Nova Partida",
                JOptionPane.PLAIN_MESSAGE, null,
                camp.getClubes().toArray(), camp.getClubes().get(0));
        if (visitante == null) return;

        String dtStr = JOptionPane.showInputDialog(this, "Data e hora (dd/MM/yyyy HH:mm):");
        if (dtStr == null || dtStr.isBlank()) return;
        try {
            LocalDateTime dt = LocalDateTime.parse(dtStr.trim(), DT_FMT);
            String res = sistema.cadastrarPartida(new Partida(mandante, visitante, dt, camp));
            if ("ok".equals(res)) log("Partida cadastrada: " + mandante + " x " + visitante + " em " + dtStr);
            else log("Erro: " + res);
        } catch (DateTimeParseException ex) {
            log("Erro: formato inválido. Use dd/MM/yyyy HH:mm");
        }
    }

    private void cadastrarGrupo() {
        if (sistema.getCampeonatos().isEmpty()) { log("Cadastre um campeonato primeiro."); return; }
        String nome = JOptionPane.showInputDialog(this, "Nome do grupo:");
        if (nome == null || nome.isBlank()) return;

        Campeonato camp = (Campeonato) JOptionPane.showInputDialog(this,
                "Campeonato:", "Novo Grupo",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getCampeonatos().toArray(), sistema.getCampeonatos().get(0));
        if (camp == null) return;

        String res = sistema.cadastrarGrupo(new Grupo(nome.trim(), camp));
        if ("ok".equals(res)) log("Grupo criado: " + nome);
        else log("Erro: " + res);
    }

    private void cadastrarParticipante() {
        if (sistema.getGrupos().isEmpty()) { log("Crie um grupo primeiro."); return; }
        String nome = JOptionPane.showInputDialog(this, "Nome do participante:");
        if (nome == null || nome.isBlank()) return;
        String login = JOptionPane.showInputDialog(this, "Login:");
        if (login == null || login.isBlank()) return;

        Participante p = new Participante(nome.trim(), login.trim());
        String res = sistema.cadastrarParticipante(p);
        if (!"ok".equals(res)) { log("Erro: " + res); return; }

        Grupo grupo = (Grupo) JOptionPane.showInputDialog(this,
                "Ingressar em qual grupo?", "Grupo",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getGrupos().toArray(), sistema.getGrupos().get(0));
        if (grupo == null) { log("Participante cadastrado sem grupo."); return; }

        String resGrupo = grupo.adicionarParticipante(p);
        if ("ok".equals(resGrupo)) log("Participante " + nome + " cadastrado no grupo " + grupo);
        else log("Participante cadastrado, mas erro ao entrar no grupo: " + resGrupo);
    }

    private void registrarAposta() {
        if (sistema.getParticipantes().isEmpty() || sistema.getPartidas().isEmpty()) {
            log("Cadastre participantes e partidas antes."); return;
        }
        Participante p = (Participante) JOptionPane.showInputDialog(this,
                "Participante:", "Registrar Aposta",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getParticipantes().toArray(), sistema.getParticipantes().get(0));
        if (p == null) return;

        Partida partida = (Partida) JOptionPane.showInputDialog(this,
                "Partida:", "Registrar Aposta",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getPartidas().toArray(), sistema.getPartidas().get(0));
        if (partida == null) return;

        String gMStr = JOptionPane.showInputDialog(this, "Gols do mandante (" + partida.getClubeMandante() + "):");
        String gVStr = JOptionPane.showInputDialog(this, "Gols do visitante (" + partida.getClubeVisitante() + "):");
        if (gMStr == null || gVStr == null) return;
        try {
            int gM = Integer.parseInt(gMStr.trim());
            int gV = Integer.parseInt(gVStr.trim());
            String res = sistema.registrarAposta(new Aposta(p, partida, gM, gV));
            if ("ok".equals(res)) log("Aposta de " + p + ": " + partida.getClubeMandante() + " " + gM + " x " + gV + " " + partida.getClubeVisitante());
            else log("Erro: " + res);
        } catch (NumberFormatException ex) {
            log("Erro: número de gols inválido.");
        }
    }

    private void registrarResultado() {
        if (sistema.getPartidas().isEmpty()) { log("Nenhuma partida cadastrada."); return; }

        Partida partida = (Partida) JOptionPane.showInputDialog(this,
                "Partida:", "Registrar Resultado",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getPartidas().toArray(), sistema.getPartidas().get(0));
        if (partida == null) return;
        if (partida.isRealizada()) { log("Resultado já registrado para esta partida."); return; }

        String gMStr = JOptionPane.showInputDialog(this, "Gols do mandante (" + partida.getClubeMandante() + "):");
        String gVStr = JOptionPane.showInputDialog(this, "Gols do visitante (" + partida.getClubeVisitante() + "):");
        if (gMStr == null || gVStr == null) return;
        try {
            int gM = Integer.parseInt(gMStr.trim());
            int gV = Integer.parseInt(gVStr.trim());
            sistema.registrarResultado(partida, gM, gV);
            log("Resultado: " + partida.getClubeMandante() + " " + gM + " x " + gV + " " + partida.getClubeVisitante());
            log("Pontos distribuídos aos participantes.");
        } catch (NumberFormatException ex) {
            log("Erro: número de gols inválido.");
        }
    }

    private void verClassificacao() {
        if (sistema.getGrupos().isEmpty()) { log("Nenhum grupo cadastrado."); return; }

        Grupo grupo = (Grupo) JOptionPane.showInputDialog(this,
                "Selecione o grupo:", "Classificação",
                JOptionPane.PLAIN_MESSAGE, null,
                sistema.getGrupos().toArray(), sistema.getGrupos().get(0));
        if (grupo == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== CLASSIFICAÇÃO: ").append(grupo.getNome().toUpperCase()).append(" ===\n");
        sb.append(String.format("%-4s %-20s %s%n", "Pos", "Nome", "Pontos"));
        sb.append("------------------------------------\n");
        int pos = 1;
        for (Participante p : grupo.getClassificacao()) {
            sb.append(String.format("%-4d %-20s %d%n", pos++, p.getNome(), p.getPontuacaoTotal()));
        }
        log(sb.toString());
    }

    // =========================================================
    private void log(String msg) {
        taLog.append(msg + "\n");
        taLog.setCaretPosition(taLog.getDocument().getLength());
    }
}