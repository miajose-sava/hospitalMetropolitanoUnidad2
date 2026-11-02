package com.mycompany.hospitalmetropolitanocrud.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citas_medicas")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha", nullable = false, length = 20)
    private String fecha;

    @Column(name = "hora", nullable = false, length = 20)
    private String hora;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @OneToMany(mappedBy = "citaMedica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diagnostico> diagnosticos = new ArrayList<>();

    public CitaMedica() {
    }

    public CitaMedica(String fecha, String hora, String estado, Paciente paciente, Medico medico) {
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.paciente = paciente;
        this.medico = medico;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public List<Diagnostico> getDiagnosticos() { return diagnosticos; }
    public void setDiagnosticos(List<Diagnostico> diagnosticos) { this.diagnosticos = diagnosticos; }
}