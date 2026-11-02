package com.mycompany.hospitalmetropolitanocrud.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "diagnosticos")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "receta", length = 255)
    private String receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    private CitaMedica citaMedica;

    public Diagnostico() {
    }

    public Diagnostico(String descripcion, String receta, CitaMedica citaMedica) {
        this.descripcion = descripcion;
        this.receta = receta;
        this.citaMedica = citaMedica;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getReceta() { return receta; }
    public void setReceta(String receta) { this.receta = receta; }

    public CitaMedica getCitaMedica() { return citaMedica; }
    public void setCitaMedica(CitaMedica citaMedica) { this.citaMedica = citaMedica; }
}