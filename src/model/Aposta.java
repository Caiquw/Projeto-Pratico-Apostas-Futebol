package model;

public class Aposta {
    private Participante participante;
    private Partida partida;
    private int golsMandantePalpite;
    private int golsVisitantePalpite;
    private int pontuacao;


    public Aposta() {
        this.pontuacao = 0;
    }


    public Aposta(Participante participante, Partida partida, int golsMandante, int golsVisitante) {
        this();
        this.participante = participante;
        this.partida = partida;
        this.golsMandantePalpite = golsMandante;
        this.golsVisitantePalpite = golsVisitante;
    }

    public int calcularPontuacao() {
        if (!partida.isRealizada()) return 0;

        String resultadoReal = partida.getResultado();
        String resultadoPalpite = calcularResultadoPalpite();

        if (!resultadoReal.equals(resultadoPalpite)) {
            pontuacao = 0;
        } else if (golsMandantePalpite == partida.getGolsMandante()
                && golsVisitantePalpite == partida.getGolsVisitante()) {
            pontuacao = 10; // placar exato
        } else {
            pontuacao = 5;  // resultado correto
        }
        return pontuacao;
    }

    private String calcularResultadoPalpite() {
        if (golsMandantePalpite > golsVisitantePalpite)
            return partida.getClubeMandante().getNome();
        if (golsVisitantePalpite > golsMandantePalpite)
            return partida.getClubeVisitante().getNome();
        return "Empate";
    }

    public String exibirInfo() {
        return participante.getNome() + " apostou "
            + partida.getClubeMandante() + " " + golsMandantePalpite
            + " x " + golsVisitantePalpite + " " + partida.getClubeVisitante()
            + " | Pontos: " + pontuacao;
    }

    public Participante getParticipante() { return participante; }
    public Partida getPartida() { return partida; }
    public int getGolsMandantePalpite() { return golsMandantePalpite; }
    public int getGolsVisitantePalpite() { return golsVisitantePalpite; }
    public int getPontuacao() { return pontuacao; }
}
