package com.mycompany.hospitalmetropolitanocrud.vista;

import com.mycompany.hospitalmetropolitanocrud.dao.CitaMedicaDAO;
import com.mycompany.hospitalmetropolitanocrud.modelo.CitaMedica;
import com.mycompany.hospitalmetropolitanocrud.modelo.Departamento;
import com.mycompany.hospitalmetropolitanocrud.modelo.Diagnostico;
import com.mycompany.hospitalmetropolitanocrud.modelo.Medico;
import com.mycompany.hospitalmetropolitanocrud.modelo.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaHospital extends JFrame {

    private final CitaMedicaDAO dao = new CitaMedicaDAO();
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("HospitalMetropolitanoPU");

    // Campos del formulario
    private JTextField txtId, txtPacienteCedula, txtPacienteNombre, txtPacienteApellido, txtPacienteTelefono;
    private JTextField txtFecha, txtHora, txtDiagnostico, txtReceta;
    private JComboBox<String> cbEspecialidad, cbMedico, cbEstado;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JButton btnAgendar, btnActualizar, btnCancelar, btnConsultaJPQL;

    public VentanaHospital() {
        setTitle("Hospital Metropolitano - Gestión Integral de Citas y Diagnósticos (JPA / JPQL)");
        setSize(1180, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
        cargarDatosTabla();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Formulario (Izquierda) ---
        JPanel panelForm = new JPanel(new GridLayout(13, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de Cita, Paciente y Diagnóstico"));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtPacienteCedula = new JTextField();
        txtPacienteNombre = new JTextField();
        txtPacienteApellido = new JTextField();
        txtPacienteTelefono = new JTextField();
        txtFecha = new JTextField("2026-11-05");
        txtHora = new JTextField("10:00");
        txtDiagnostico = new JTextField();
        txtReceta = new JTextField();

        cbEspecialidad = new JComboBox<>(new String[]{"Cardiología", "Pediatría", "Traumatología", "Medicina General"});
        cbMedico = new JComboBox<>(new String[]{"Dr. Carlos Pérez", "Dra. Ana Rivas", "Dr. Luis Morales"});
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "Confirmada", "Atendida", "Cancelada"});

        panelForm.add(new JLabel("ID Cita:"));
        panelForm.add(txtId);
        panelForm.add(new JLabel("Cédula Paciente:"));
        panelForm.add(txtPacienteCedula);
        panelForm.add(new JLabel("Nombre Paciente:"));
        panelForm.add(txtPacienteNombre);
        panelForm.add(new JLabel("Apellido Paciente:"));
        panelForm.add(txtPacienteApellido);
        panelForm.add(new JLabel("Teléfono Paciente:"));
        panelForm.add(txtPacienteTelefono);
        panelForm.add(new JLabel("Especialidad:"));
        panelForm.add(cbEspecialidad);
        panelForm.add(new JLabel("Médico:"));
        panelForm.add(cbMedico);
        panelForm.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panelForm.add(txtFecha);
        panelForm.add(new JLabel("Hora (HH:MM):"));
        panelForm.add(txtHora);
        panelForm.add(new JLabel("Estado:"));
        panelForm.add(cbEstado);
        panelForm.add(new JLabel("Diagnóstico Clínico:"));
        panelForm.add(txtDiagnostico);
        panelForm.add(new JLabel("Receta / Medicación:"));
        panelForm.add(txtReceta);

        // --- Botones de Acción ---
        btnAgendar = new JButton("Agendar Cita");
        btnActualizar = new JButton("Actualizar");
        btnCancelar = new JButton("Cancelar (Delete)");
        btnConsultaJPQL = new JButton("Probar JPQL Fetch");

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        panelBotones.add(btnAgendar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnConsultaJPQL);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.add(panelForm, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // --- Tabla de Resultados (Derecha) ---
        modeloTabla = new DefaultTableModel(new String[]{
            "ID", "Cédula", "Paciente", "Teléfono", "Médico", "Fecha", "Hora", "Estado", "Diagnóstico", "Receta"
        }, 0);
        tablaCitas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaCitas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Citas y Diagnósticos Registrados (JPQL List)"));

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);
        add(panelPrincipal);

        // --- Eventos ---
        btnAgendar.addActionListener(e -> accionGuardar());
        btnActualizar.addActionListener(e -> accionActualizar());
        btnCancelar.addActionListener(e -> accionEliminar());
        btnConsultaJPQL.addActionListener(e -> accionProbarJPQL());

        tablaCitas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaCitas.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtPacienteCedula.setText(modeloTabla.getValueAt(fila, 1).toString());
                
                String nombreCompleto = modeloTabla.getValueAt(fila, 2).toString();
                String[] partes = nombreCompleto.split(" ", 2);
                txtPacienteNombre.setText(partes.length > 0 ? partes[0] : "");
                txtPacienteApellido.setText(partes.length > 1 ? partes[1] : "");
                
                txtPacienteTelefono.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtFecha.setText(modeloTabla.getValueAt(fila, 5).toString());
                txtHora.setText(modeloTabla.getValueAt(fila, 6).toString());
                cbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 7).toString());
                txtDiagnostico.setText(modeloTabla.getValueAt(fila, 8) != null ? modeloTabla.getValueAt(fila, 8).toString() : "");
                txtReceta.setText(modeloTabla.getValueAt(fila, 9) != null ? modeloTabla.getValueAt(fila, 9).toString() : "");
            }
        });
    }

    private Paciente obtenerOCrearPaciente(EntityManager em, String cedula, String nombre, String apellido, String telefono) {
        List<Paciente> lista = em.createQuery("SELECT p FROM Paciente p WHERE p.cedula = :cedula", Paciente.class)
                                 .setParameter("cedula", cedula)
                                 .getResultList();
        if (!lista.isEmpty()) {
            Paciente p = lista.get(0);
            p.setNombre(nombre);
            p.setApellido(apellido);
            p.setTelefono(telefono);
            return p;
        }
        Paciente p = new Paciente(cedula, nombre, apellido, telefono);
        em.persist(p);
        return p;
    }

    private Medico obtenerOCrearMedico(EntityManager em, String nombreCompleto, String nombreDepto) {
        List<Medico> lista = em.createQuery("SELECT m FROM Medico m WHERE m.nombre = :nom", Medico.class)
                               .setParameter("nom", nombreCompleto)
                               .getResultList();
        if (!lista.isEmpty()) return lista.get(0);

        List<Departamento> deptos = em.createQuery("SELECT d FROM Departamento d WHERE d.nombre = :dnom", Departamento.class)
                                      .setParameter("dnom", nombreDepto)
                                      .getResultList();
        Departamento d = deptos.isEmpty() ? new Departamento(nombreDepto, 1) : deptos.get(0);
        if (deptos.isEmpty()) em.persist(d);

        Medico m = new Medico(nombreCompleto, "Doc", "LIC-" + System.currentTimeMillis() % 1000, d);
        em.persist(m);
        return m;
    }

    private void accionGuardar() {
        if (txtPacienteCedula.getText().trim().isEmpty() || txtPacienteNombre.getText().trim().isEmpty() || txtPacienteApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete cédula, nombre y apellido del paciente.");
            return;
        }

        String telefono = txtPacienteTelefono.getText().trim().isEmpty() ? "S/N" : txtPacienteTelefono.getText().trim();
        String descDiag = txtDiagnostico.getText().trim().isEmpty() ? "Consulta general" : txtDiagnostico.getText().trim();
        String descReceta = txtReceta.getText().trim().isEmpty() ? "Sin medicación" : txtReceta.getText().trim();

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Paciente pac = obtenerOCrearPaciente(
                em,
                txtPacienteCedula.getText().trim(),
                txtPacienteNombre.getText().trim(),
                txtPacienteApellido.getText().trim(),
                telefono
            );
            Medico med = obtenerOCrearMedico(em, cbMedico.getSelectedItem().toString(), cbEspecialidad.getSelectedItem().toString());

            CitaMedica cita = new CitaMedica(txtFecha.getText().trim(), txtHora.getText().trim(), cbEstado.getSelectedItem().toString(), pac, med);
            
            // Creación y vinculación bidireccional del Diagnóstico (1:N)
            Diagnostico diag = new Diagnostico(descDiag, descReceta, cita);
            cita.getDiagnosticos().add(diag);

            em.persist(cita); // Persiste en cascada cita y diagnóstico
            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Cita y diagnóstico registrados correctamente mediante JPA.");
            limpiarCampos();
            cargarDatosTabla();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    private void accionActualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para actualizar.");
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CitaMedica cita = em.find(CitaMedica.class, id);
            if (cita != null) {
                cita.setFecha(txtFecha.getText().trim());
                cita.setHora(txtHora.getText().trim());
                cita.setEstado(cbEstado.getSelectedItem().toString());
                
                if (cita.getPaciente() != null) {
                    cita.getPaciente().setNombre(txtPacienteNombre.getText().trim());
                    cita.getPaciente().setApellido(txtPacienteApellido.getText().trim());
                    cita.getPaciente().setTelefono(txtPacienteTelefono.getText().trim());
                }
                
                // Actualiza o crea el diagnóstico asociado
                if (!cita.getDiagnosticos().isEmpty()) {
                    Diagnostico diag = cita.getDiagnosticos().get(0);
                    diag.setDescripcion(txtDiagnostico.getText().trim());
                    diag.setReceta(txtReceta.getText().trim());
                } else if (!txtDiagnostico.getText().trim().isEmpty()) {
                    Diagnostico nuevoDiag = new Diagnostico(txtDiagnostico.getText().trim(), txtReceta.getText().trim(), cita);
                    cita.getDiagnosticos().add(nuevoDiag);
                }
                
                em.merge(cita);
            }
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(this, "Cita y diagnóstico actualizados con éxito.");
            limpiarCampos();
            cargarDatosTabla();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    private void accionEliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para cancelar.");
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        dao.eliminar(id);
        JOptionPane.showMessageDialog(this, "Cita procesada con éxito.");
        limpiarCampos();
        cargarDatosTabla();
    }

    private void accionProbarJPQL() {
        List<CitaMedica> lista = dao.listarConDetallesCompletos();
        JOptionPane.showMessageDialog(this, "Consulta JPQL JOIN FETCH ejecutada con éxito.\nTotal citas cargadas con relaciones: " + lista.size());
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<CitaMedica> lista = dao.listarConDetallesCompletos();
        for (CitaMedica c : lista) {
            String pacienteNombreCompleto = (c.getPaciente() != null) 
                ? c.getPaciente().getNombre() + " " + c.getPaciente().getApellido() 
                : "";
            
            String descripcionDiag = (!c.getDiagnosticos().isEmpty()) 
                ? c.getDiagnosticos().get(0).getDescripcion() 
                : "Sin diagnóstico";
                
            String recetaDiag = (!c.getDiagnosticos().isEmpty()) 
                ? c.getDiagnosticos().get(0).getReceta() 
                : "Sin receta";

            modeloTabla.addRow(new Object[]{
                c.getId(),
                c.getPaciente() != null ? c.getPaciente().getCedula() : "",
                pacienteNombreCompleto,
                c.getPaciente() != null ? c.getPaciente().getTelefono() : "",
                c.getMedico() != null ? c.getMedico().getNombre() : "",
                c.getFecha(),
                c.getHora(),
                c.getEstado(),
                descripcionDiag,
                recetaDiag
            });
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtPacienteCedula.setText("");
        txtPacienteNombre.setText("");
        txtPacienteApellido.setText("");
        txtPacienteTelefono.setText("");
        txtDiagnostico.setText("");
        txtReceta.setText("");
        tablaCitas.clearSelection();
    }
}