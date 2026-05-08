package view;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // credenciais fixas
    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_SENHA = "admin123";
    private static final String USER_LOGIN  = "user";
    private static final String USER_SENHA  = "user123";

    private JTextField tfLogin;
    private JPasswordField tfSenha;

    public LoginFrame() {
        super("Login - Sistema de Apostas");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
        setVisible(true);
    }

    private void construirUI() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        tfLogin = new JTextField();
        tfSenha = new JPasswordField();
        JButton btnEntrar = new JButton("Entrar");

        painel.add(new JLabel("Login:"));  painel.add(tfLogin);
        painel.add(new JLabel("Senha:"));  painel.add(tfSenha);
        painel.add(new JLabel());          painel.add(btnEntrar);

        JLabel lblDica = new JLabel("Admin: admin/admin123 | User: user/user123", SwingConstants.CENTER);
        lblDica.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        lblDica.setForeground(Color.GRAY);

        add(painel, BorderLayout.CENTER);
        add(lblDica, BorderLayout.SOUTH);

        btnEntrar.addActionListener(e -> tentarLogin());

        tfSenha.addActionListener(e -> tentarLogin());
    }

    private void tentarLogin() {
        String login = tfLogin.getText().trim();
        String senha = new String(tfSenha.getPassword()).trim();

        if (ADMIN_LOGIN.equals(login) && ADMIN_SENHA.equals(senha)) {
            dispose();
            new MainFrame(true);
        } else if (USER_LOGIN.equals(login) && USER_SENHA.equals(senha)) {
            dispose();
            new MainFrame(false);
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            tfSenha.setText("");
        }
    }
}
