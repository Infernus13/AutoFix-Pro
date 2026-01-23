package com.autofix.vista;

import com.autofix.dao.CitaDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import com.autofix.modelo.Cliente;
import com.autofix.dao.ClienteDAO;
import com.autofix.util.GeneradorFactura;
import com.autofix.dao.UsuarioDAO;

public class MainFrame extends JFrame {

    // Usuario logueado
    private Usuario usuarioActual;

    // DAOs
    private CitaDAO citaDAO;

    // Componentes que necesitamos actualizar
    private JLabel lblPendientes;
    private JLabel lblCompletadas;
    private JLabel lblIngresos;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private UsuariosPanel usuariosPanel;

    // Lista para guardar los IDs de las citas
    private List<Integer> idsCitas = new ArrayList<>();

    // Colores
    private static final Color COLOR_SIDEBAR = new Color(31, 41, 55);
    private static final Color COLOR_SIDEBAR_HOVER = new Color(55, 65, 81);
    private static final Color COLOR_ACTIVE = new Color(37, 99, 235);
    private static final Color COLOR_BG = new Color(249, 250, 251);
    private static final Color COLOR_TEXT_DARK = new Color(31, 41, 55);
    private static final Color COLOR_TEXT_GRAY = new Color(107, 114, 128);

    // Paneles de contenido
    private JPanel panelContenido;
    private JPanel dashboardPanel;
    private ClientesPanel clientesPanel;
    private ServiciosPanel serviciosPanel;
    private ReportesPanel reportesPanel;

    // Botones del sidebar (para cambiar estilos)
    private JButton btnDashboard;
    private JButton btnCitas;
    private JButton btnClientes;
    private JButton btnServicios;
    private JButton btnReportes;
    private JButton botonActivo;
    private JButton btnUsuarios;

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.citaDAO = new CitaDAO();

        configurarVentana();
        crearComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestion - AutoFix Pro");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearComponentes() {
        add(crearTopBar(), BorderLayout.NORTH);
        add(crearSidebar(), BorderLayout.WEST);
        add(crearContenidoPrincipal(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    private JPanel crearTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_SIDEBAR);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Sistema de Gestion - AutoFix Pro");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        topBar.add(titleLabel, BorderLayout.WEST);
        return topBar;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));

        sidebar.add(crearPanelUsuario());
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        // Crear botones de navegacion
        btnDashboard = crearBotonNav("Dashboard");
        btnCitas = crearBotonNav("Citas");
        btnClientes = crearBotonNav("Clientes");
        btnServicios = crearBotonNav("Servicios");
        btnReportes = crearBotonNav("Reportes");
        btnUsuarios = crearBotonNav("Usuarios");

        // Acciones de navegacion
        btnDashboard.addActionListener(e -> mostrarPanel("dashboard"));
        btnCitas.addActionListener(e -> mostrarPanel("citas"));
        btnClientes.addActionListener(e -> mostrarPanel("clientes"));
        btnServicios.addActionListener(e -> mostrarPanel("servicios"));
        btnReportes.addActionListener(e -> mostrarPanel("reportes"));
        btnUsuarios.addActionListener(e -> mostrarPanel("usuarios"));

        // Solo mostrar Dashboard si es administrador
        if ("administrador".equals(usuarioActual.getRol())) {
            sidebar.add(btnDashboard);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        // Solo mostrar Citas si es trabajador (admin usa Dashboard)
        if (!"administrador".equals(usuarioActual.getRol())) {
            sidebar.add(btnCitas);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        sidebar.add(btnClientes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnServicios);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        // Solo mostrar Reportes si es administrador
        if ("administrador".equals(usuarioActual.getRol())) {
            sidebar.add(btnReportes);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        // Solo mostrar Usuarios si es administrador
        if ("administrador".equals(usuarioActual.getRol())) {
            sidebar.add(btnUsuarios);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Boton cerrar sesion
        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(230, 40));
        btnLogout.setBackground(new Color(220, 38, 38));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> cerrarSesion());

        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Activar panel por defecto según rol
        if ("administrador".equals(usuarioActual.getRol())) {
            botonActivo = btnDashboard;
            actualizarBotonActivo(btnDashboard);
        } else {
            botonActivo = btnCitas;
            actualizarBotonActivo(btnCitas);
        }
        return sidebar;
    }

    private JPanel crearPanelUsuario() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        userPanel.setBackground(COLOR_SIDEBAR);
        userPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SIDEBAR_HOVER));
        userPanel.setMaximumSize(new Dimension(260, 90));

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(59, 130, 246));
                g2.fillOval(0, 0, 55, 55);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                String iniciales = obtenerIniciales(usuarioActual.getNombre());
                FontMetrics fm = g2.getFontMetrics();
                int x = (55 - fm.stringWidth(iniciales)) / 2;
                int y = ((55 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(iniciales, x, y);
            }
        };
        avatar.setPreferredSize(new Dimension(55, 55));
        avatar.setOpaque(false);

        // Info usuario
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(COLOR_SIDEBAR);

        JLabel nameLabel = new JLabel(usuarioActual.getNombre());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel roleLabel = new JLabel(capitalizar(usuarioActual.getRol()));
        roleLabel.setForeground(new Color(156, 163, 175));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(roleLabel);

        userPanel.add(avatar);
        userPanel.add(infoPanel);

        return userPanel;
    }

    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(COLOR_SIDEBAR);
        btn.setForeground(new Color(209, 213, 219));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != botonActivo) {
                    btn.setBackground(COLOR_SIDEBAR_HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != botonActivo) {
                    btn.setBackground(COLOR_SIDEBAR);
                    btn.setForeground(new Color(209, 213, 219));
                }
            }
        });

        return btn;
    }

    private JPanel crearContenidoPrincipal() {
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(COLOR_BG);

        // Crear y guardar el dashboard
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(COLOR_BG);
        dashboardPanel.add(crearToolbar(), BorderLayout.NORTH);
        dashboardPanel.add(crearPanelCentral(), BorderLayout.CENTER);

        // Si es admin, mostrar dashboard. Si no, mostrar citas
        if ("administrador".equals(usuarioActual.getRol())) {
            panelContenido.add(dashboardPanel, BorderLayout.CENTER);
        } else {
            // Trabajadores ven directamente sus citas
            panelContenido.add(dashboardPanel, BorderLayout.CENTER);
        }

        return panelContenido;
    }

    private JPanel crearToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                new EmptyBorder(18, 25, 18, 25)
        ));

        JLabel titleLabel = new JLabel("Gestion de Citas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT_DARK);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        JButton newBtn = new JButton("+ Nueva Cita");
        newBtn.setBackground(COLOR_ACTIVE);
        newBtn.setForeground(Color.WHITE);
        newBtn.setFocusPainted(false);
        newBtn.setBorderPainted(false);
        newBtn.setFont(new Font("Arial", Font.BOLD, 13));
        newBtn.setPreferredSize(new Dimension(140, 38));
        newBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightPanel.add(newBtn);
        newBtn.addActionListener(e -> abrirNuevaCita());

        toolbar.add(titleLabel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel crearPanelCentral() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Tarjetas de estadisticas
        mainPanel.add(crearPanelEstadisticas(), BorderLayout.NORTH);

        // Tabla de citas
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel crearPanelEstadisticas() {
        int numTarjetas = "administrador".equals(usuarioActual.getRol()) ? 3 : 2;
        JPanel statsPanel = new JPanel(new GridLayout(1, numTarjetas, 18, 0));
        statsPanel.setBackground(COLOR_BG);

        // Crear tarjetas (los valores se actualizan en cargarDatos)
        lblPendientes = new JLabel("0");
        lblCompletadas = new JLabel("0");
        lblIngresos = new JLabel("0.00 €");

        statsPanel.add(crearTarjeta("Pendientes", lblPendientes, new Color(234, 179, 8)));
        statsPanel.add(crearTarjeta("Completadas", lblCompletadas, new Color(34, 197, 94)));
        // Solo mostrar ingresos a administradores
        if ("administrador".equals(usuarioActual.getRol())) {
            statsPanel.add(crearTarjeta("Ingresos", lblIngresos, new Color(168, 85, 247)));
        }

        return statsPanel;
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JLabel labelText = new JLabel(titulo);
        labelText.setForeground(COLOR_TEXT_GRAY);
        labelText.setFont(new Font("Arial", Font.PLAIN, 13));

        lblValor.setForeground(COLOR_TEXT_DARK);
        lblValor.setFont(new Font("Arial", Font.BOLD, 32));

        leftPanel.add(labelText);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftPanel.add(lblValor);

        // Icono con color
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillOval(5, 5, 50, 50);
                g2.setColor(color);
                g2.fillOval(15, 15, 30, 30);
            }
        };
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setOpaque(false);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(iconPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel crearPanelTabla() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));

        String[] columnas = {"Cliente", "Vehiculo", "Fecha", "Empleado", "Servicio", "Estado", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCitas = new JTable(modeloTabla);

        // Menu contextual (clic derecho)
        JPopupMenu menuContextual = new JPopupMenu();

        JMenuItem itemEditar = new JMenuItem("Editar cita");
        itemEditar.addActionListener(e -> editarCitaSeleccionada());
        menuContextual.add(itemEditar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar cita");
        itemEliminar.addActionListener(e -> eliminarCitaSeleccionada());
        menuContextual.add(itemEliminar);

        menuContextual.addSeparator();

        JMenuItem itemCompletar = new JMenuItem("Marcar como completada");
        itemCompletar.addActionListener(e -> cambiarEstadoCita("completada"));
        menuContextual.add(itemCompletar);

        JMenuItem itemPendiente = new JMenuItem("Marcar como pendiente");
        itemPendiente.addActionListener(e -> cambiarEstadoCita("pendiente"));
        menuContextual.add(itemPendiente);

        JMenuItem itemEnProceso = new JMenuItem("Marcar en proceso");
        itemEnProceso.addActionListener(e -> cambiarEstadoCita("en_proceso"));
        menuContextual.add(itemEnProceso);

        tablaCitas.setComponentPopupMenu(menuContextual);

        // Seleccionar fila al hacer clic derecho y doble clic para ver detalles
        tablaCitas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tablaCitas.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tablaCitas.getRowCount()) {
                    tablaCitas.setRowSelectionInterval(row, row);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaCitas.getSelectedRow();
                    if (fila >= 0) {
                        int idCita = idsCitas.get(fila);
                        VerCitaDialog dialog = new VerCitaDialog(MainFrame.this, idCita);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        tablaCitas.setRowHeight(45);
        tablaCitas.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaCitas.setGridColor(new Color(243, 244, 246));
        tablaCitas.setSelectionBackground(new Color(219, 234, 254));
        tablaCitas.setSelectionForeground(COLOR_TEXT_DARK);

        // Header
        tablaCitas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaCitas.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaCitas.getTableHeader().setForeground(COLOR_TEXT_GRAY);
        tablaCitas.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaCitas.getColumnCount(); i++) {
            tablaCitas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderizador para estado con colores (columna 5)
        tablaCitas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);

                String estado = value.toString();
                switch (estado) {
                    case "completada":
                        label.setForeground(new Color(34, 197, 94));
                        break;
                    case "en_proceso":
                        label.setForeground(new Color(59, 130, 246));
                        break;
                    case "pendiente":
                        label.setForeground(new Color(234, 179, 8));
                        break;
                    case "cancelada":
                        label.setForeground(new Color(239, 68, 68));
                        break;
                    default:
                        label.setForeground(COLOR_TEXT_DARK);
                }
                label.setFont(new Font("Arial", Font.BOLD, 12));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel crearBarraEstado() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(COLOR_SIDEBAR);
        statusBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel leftLabel = new JLabel("Sistema conectado a base de datos MySQL");
        leftLabel.setForeground(new Color(156, 163, 175));
        leftLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 0));
        rightPanel.setBackground(COLOR_SIDEBAR);

        JLabel userLabel = new JLabel("Usuario: " + usuarioActual.getEmail());
        userLabel.setForeground(new Color(156, 163, 175));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel statusLabel = new JLabel("● Online");
        statusLabel.setForeground(new Color(34, 197, 94));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        rightPanel.add(userLabel);
        rightPanel.add(statusLabel);

        statusBar.add(leftLabel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }

    // ========== METODOS DE DATOS ==========

    private void cargarDatos() {
        cargarEstadisticas();
        cargarTablaCitas();
    }

    private void cargarEstadisticas() {
        lblPendientes.setText(String.valueOf(citaDAO.contarPendientes()));
        lblCompletadas.setText(String.valueOf(citaDAO.contarCompletadas()));
        lblIngresos.setText(String.format("%.2f €", citaDAO.calcularIngresos()));
    }

    private void cargarTablaCitas() {
        modeloTabla.setRowCount(0);
        idsCitas.clear();

        List<Cita> citas;

        // Si es administrador, ver todas las citas
        // Si es trabajador, ver solo sus citas
        if ("administrador".equals(usuarioActual.getRol())) {
            citas = citaDAO.obtenerTodas();
        } else {
            citas = citaDAO.obtenerPorUsuario(usuarioActual.getId());
        }

        for (Cita cita : citas) {
            String vehiculo = "";
            if (cita.getMatricula() != null && cita.getModeloCoche() != null) {
                vehiculo = cita.getMatricula() + " - " + cita.getModeloCoche();
            } else if (cita.getMatricula() != null) {
                vehiculo = cita.getMatricula();
            }

            idsCitas.add(cita.getId());

            Object[] fila = {
                    cita.getNombreCliente(),
                    vehiculo,
                    cita.getFecha().toString(),
                    cita.getNombreUsuario(),
                    cita.getNombreServicio(),
                    cita.getEstado(),
                    String.format("%.2f €", cita.getPrecioFinal())
            };
            modeloTabla.addRow(fila);
        }
    }

    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Esta seguro de que desea cerrar sesion?",
                "Cerrar Sesion",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        }
    }

    // ========== UTILIDADES ==========

    private String obtenerIniciales(String nombre) {
        String[] partes = nombre.split(" ");
        if (partes.length >= 2) {
            return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
        }
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    private void abrirNuevaCita() {
        NuevaCitaDialog dialog = new NuevaCitaDialog(this);
        dialog.setVisible(true);

        // Si se guardó, recargar datos
        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void editarCitaSeleccionada() {
        int fila = tablaCitas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = idsCitas.get(fila);
        EditarCitaDialog dialog = new EditarCitaDialog(this, idCita);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void eliminarCitaSeleccionada() {
        int fila = tablaCitas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = idsCitas.get(fila);
        String cliente = modeloTabla.getValueAt(fila, 0).toString();
        String estado = modeloTabla.getValueAt(fila, 5).toString();

        // No permitir eliminar citas completadas
        if ("completada".equals(estado)) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se puede eliminar una cita completada.\nEl registro de ingresos debe mantenerse.",
                    "Operacion no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la cita de " + cliente + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            if (citaDAO.eliminar(idCita)) {
                JOptionPane.showMessageDialog(this, "Cita eliminada", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoCita(String nuevoEstado) {
        int fila = tablaCitas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = idsCitas.get(fila);

        if (citaDAO.actualizarEstado(idCita, nuevoEstado)) {
            cargarDatos();

            // Si se completa la cita, preguntar si generar factura
            if ("completada".equals(nuevoEstado)) {
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "Cita completada. ¿Desea generar la factura?",
                        "Generar Factura",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    generarFacturaCita(idCita);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarFacturaCita(int idCita) {
        // Obtener cita
        Cita cita = citaDAO.obtenerPorId(idCita);

        if (cita == null) {
            JOptionPane.showMessageDialog(this, "No se encontro la cita", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener cliente
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.obtenerPorId(cita.getIdCliente());

        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener nombre del empleado
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario empleado = usuarioDAO.obtenerPorId(cita.getIdUsuario());
        String nombreEmpleado = empleado != null ? empleado.getNombre() : "No asignado";

        // Generar factura
        String rutaFactura = GeneradorFactura.generarFactura(cita, cliente, nombreEmpleado);

        if (rutaFactura != null) {
            int abrir = JOptionPane.showConfirmDialog(
                    this,
                    "Factura generada correctamente:\n" + rutaFactura + "\n\n¿Desea abrir el archivo?",
                    "Factura Generada",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (abrir == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(new java.io.File(rutaFactura));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al generar la factura", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarBotonActivo(JButton nuevoBoton) {
        // Desactivar el anterior
        if (botonActivo != null) {
            botonActivo.setBackground(COLOR_SIDEBAR);
            botonActivo.setForeground(new Color(209, 213, 219));
        }

        // Activar el nuevo
        botonActivo = nuevoBoton;
        botonActivo.setBackground(COLOR_ACTIVE);
        botonActivo.setForeground(Color.WHITE);
    }

    private void mostrarPanel(String panel) {
        panelContenido.removeAll();

        switch (panel) {
            case "dashboard":
                actualizarBotonActivo(btnDashboard);
                panelContenido.add(dashboardPanel, BorderLayout.CENTER);
                cargarDatos();
                break;
            case "citas":
                actualizarBotonActivo(btnCitas);
                panelContenido.add(dashboardPanel, BorderLayout.CENTER);
                cargarDatos();
                break;
            case "clientes":
                actualizarBotonActivo(btnClientes);
                if (clientesPanel == null) {
                    clientesPanel = new ClientesPanel();
                }
                clientesPanel.cargarDatos();
                panelContenido.add(clientesPanel, BorderLayout.CENTER);
                break;
            case "servicios":
                actualizarBotonActivo(btnServicios);
                if (serviciosPanel == null) {
                    serviciosPanel = new ServiciosPanel();
                }
                serviciosPanel.cargarDatos();
                panelContenido.add(serviciosPanel, BorderLayout.CENTER);
                break;
            case "reportes":
                actualizarBotonActivo(btnReportes);
                if (reportesPanel == null) {
                    reportesPanel = new ReportesPanel();
                }
                reportesPanel.cargarReporte();
                panelContenido.add(reportesPanel, BorderLayout.CENTER);
                break;
            case "usuarios":
                actualizarBotonActivo(btnUsuarios);
                if (usuariosPanel == null) {
                    usuariosPanel = new UsuariosPanel();
                }
                usuariosPanel.cargarDatos();
                panelContenido.add(usuariosPanel, BorderLayout.CENTER);
                break;
        }

        panelContenido.revalidate();
        panelContenido.repaint();
    }
}