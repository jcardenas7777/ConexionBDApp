package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.ConnectionConfig;
import model.ConnectionResult;
import registry.DriverRegistry;
import service.ConnectionService;
import service.JdbcConnectionService;

/**
 * Capa de presentación Swing.
 */
public class ConexionTesterFrame extends JFrame {

    private static final Color VERDE = new Color(0, 128, 0);
    private static final Color ROJO = new Color(190, 0, 0);

    private final ConnectionService connectionService;

    private final JTextField txtUrl = new JTextField("jdbc:sqlite:inventario.db", 28);
    private final JTextField txtUsuario = new JTextField(28);
    private final JPasswordField txtPassword = new JPasswordField(28);
    private final JButton btnProbar = new JButton("Probar conexión");
    private final JTextArea txtResultado = new JTextArea(10, 40);

    // Inyección de dependencias (DIP)
    public ConexionTesterFrame(ConnectionService connectionService) {
        super("Probador de conexión JDBC (SOLID & Lambdas)");
        this.connectionService = connectionService;

        construirInterfaz();
        configurarVentana();
    }

    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelCampos.add(new JLabel("URL de conexión:"));
        panelCampos.add(txtUrl);
        panelCampos.add(new JLabel("Usuario:"));
        panelCampos.add(txtUsuario);
        panelCampos.add(new JLabel("Contraseña:"));
        panelCampos.add(txtPassword);

        JPanel panelCentro = new JPanel(new BorderLayout(8, 8));
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnProbar);
        panelCentro.add(panelBoton, BorderLayout.NORTH);

        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado de la prueba"));
        panelCentro.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(panelCampos, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        setContentPane(panelPrincipal);

        // Lambda pura para el listener del botón
        btnProbar.addActionListener(e -> ejecutarPrueba());
    }

    private void ejecutarPrueba() {
        ConnectionConfig config = new ConnectionConfig(
                txtUrl.getText().trim(),
                txtUsuario.getText().trim(),
                new String(txtPassword.getPassword())
        );

        ConnectionResult resultado = connectionService.probarConexion(config);
        mostrarResultado(resultado);
    }

    private void mostrarResultado(ConnectionResult res) {
        txtResultado.setText("");
        txtResultado.setForeground(res.exitoso() ? VERDE : ROJO);
        txtResultado.append(res.mensaje() + "\n");
        if (res.detalleTecnico() != null && !res.detalleTecnico().isBlank()) {
            txtResultado.append("  " + res.detalleTecnico() + "\n");
        }
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // Ensamble e inyección manual de dependencias
        DriverRegistry registry = new DriverRegistry();
        ConnectionService service = new JdbcConnectionService(registry);

        // Lambda pura para el hilo seguro de Swing
        SwingUtilities.invokeLater(() -> new ConexionTesterFrame(service).setVisible(true));
    }
}