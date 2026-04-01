package model;

public class Clube extends EntidadeEsportiva {
    private String estado;

    public Clube() {
        super();
        this.estado = "";
    }

    public Clube(String nome, String estado) {
        super(nome);
        this.estado = estado;
    }

    @Override
    public String getCategoria() {
        return "Clube";
    }

    // Polimorfismo: sobreposição de exibirInfo
    @Override
    public String exibirInfo() {
        return "[Clube] " + getNome() + " - " + estado;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
