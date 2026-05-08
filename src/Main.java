import controller.Sistema;
import controller.PersistenciaArquivo;
import model.Persistencia;
import view.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //Persistencia persistencia = new PersistenciaArquivo();
        // Para usar JDBC, troque a linha acima por:
         Persistencia persistencia = new controller.PersistenciaJDBC();

        persistencia.carregar(Sistema.getInstancia());

        SwingUtilities.invokeLater(LoginFrame::new);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            persistencia.salvar(Sistema.getInstancia())
        ));
    }
}
