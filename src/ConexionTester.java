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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 * ConexionTester: ventana Swing que permite ingresar la configuración de una
 * base de datos (URL, usuario y contraseña) y probar la conexión mediante JDBC.
 *
 * Unidad 3 - Desarrollo de interfaces gráficas y conexión a bases de datos.
 * No utiliza frameworks externos (solo JDK; el driver JDBC se carga por nombre).
 */
public class ConexionTester extends JFrame {
 
    private static final long serialVersionUID = 1L;
 
    private static final Color VERDE = new Color(0, 128, 0);
    private static final Color ROJO = new Color(190, 0, 0);
 
    private final JTextField txtUrl;
    private final JTextField txtUsuario;
    private final JPasswordField txtPassword;
    private final JButton btnProbar;
    private final JTextArea txtResultado;
 
    public ConexionTester() {
        super("Probador de conexión JDBC");
 
        txtUrl = new JTextField("jdbc:sqlite:inventario.db", 28);
        txtUsuario = new JTextField(28);
        txtPassword = new JPasswordField(28);
        btnProbar = new JButton("Probar conexión");
        txtResultado = new JTextArea(10, 40);
 
        construirInterfaz();
        configurarVentana();
    }
 
    /** Arma el JPanel principal con BorderLayout y un GridLayout para los campos. */
    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
 
        // Zona NORTE: formulario con GridLayout (3 filas x 2 columnas)
        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelCampos.add(new JLabel("URL de conexión:"));
        panelCampos.add(txtUrl);
        panelCampos.add(new JLabel("Usuario:"));
        panelCampos.add(txtUsuario);
        panelCampos.add(new JLabel("Contraseña:"));
        panelCampos.add(txtPassword);
 
        // Zona CENTRO: botón + resultado
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
 
        // ActionListener del botón
        btnProbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarConexion();
            }
        });
    }
 
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
 
    /**
     * Lógica del botón "Probar conexión".
     * - Carga el driver según la URL (ClassNotFoundException si no está en el classpath).
     * - Abre la conexión con DriverManager dentro de un try-with-resources,
     *   por lo que se cierra automáticamente tanto si funciona como si falla.
     */
    private void probarConexion() {
        String url = txtUrl.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        txtResultado.setText("");

        if (url.isEmpty()) {
            mostrarMensaje("Debe ingresar la URL de conexión.", ROJO);
            return;
        }

        try {
            Class.forName(obtenerClaseDriver(url));

            try (Connection conexion = DriverManager.getConnection(url, usuario, password)) {
                DatabaseMetaData meta = conexion.getMetaData();
                mostrarMensaje("Conexión exitosa.", VERDE);
                agregarLinea("  Motor : " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion(), VERDE);
                agregarLinea("  Driver: " + meta.getDriverName()
                        + " " + meta.getDriverVersion(), VERDE);
            }

        } catch (ClassNotFoundException e) {
            mostrarMensaje("Fallo del driver: no se encontró la clase del driver JDBC.", ROJO);
            agregarLinea("  Detalle técnico: " + e.getMessage(), ROJO);
            agregarLinea("  Agregue el .jar del driver al classpath.", ROJO);
        } catch (SQLException e) {
            mostrarMensaje("Fallo de conexión.", ROJO);
            agregarLinea("  Detalle técnico: " + e.getMessage(), ROJO);
        } catch (Throwable e) {
            // Captura cualquier otro tipo de error no contemplado
            mostrarMensaje("Error inesperado al conectar.", ROJO);
            agregarLinea("  Detalle técnico: " + e.toString(), ROJO);
        }
    }
    /** Devuelve el nombre de la clase del driver a partir del prefijo de la URL JDBC. */
    private String obtenerClaseDriver(String url) {
        String u = url.toLowerCase();
        if (u.startsWith("jdbc:sqlite:")) {
            return "org.sqlite.JDBC";
        } else if (u.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (u.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        } else if (u.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        } else if (u.startsWith("jdbc:sqlserver:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        return "org.sqlite.JDBC"; // por defecto SQLite, según el enunciado
    }
 
    private void mostrarMensaje(String texto, Color color) {
        txtResultado.setText("");
        agregarLinea(texto, color);
    }
 
    /**
     * Agrega una línea al JTextArea. Como JTextArea tiene un único color de texto,
     * se colorea el área completa según el resultado (verde = éxito, rojo = error).
     */
    private void agregarLinea(String texto, Color color) {
        txtResultado.setForeground(color);
        txtResultado.append(texto + "\n");
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConexionTester().setVisible(true);
            }
        });
    }
}

