package model;

public interface Persistencia {
    void salvar(controller.Sistema sistema);
    void carregar(controller.Sistema sistema);
}
