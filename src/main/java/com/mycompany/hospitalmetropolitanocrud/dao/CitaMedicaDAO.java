package com.mycompany.hospitalmetropolitanocrud.dao;

import com.mycompany.hospitalmetropolitanocrud.modelo.CitaMedica;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class CitaMedicaDAO {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("HospitalMetropolitanoPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // ==========================================
    // OPERACIONES BÁSICAS CRUD (Ciclo de Vida JPA)
    // ==========================================

    public void crear(CitaMedica cita) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cita); // Pasa de Transient a Persistent
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void actualizar(CitaMedica cita) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cita); // Sincroniza estado Detached con la BD
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            CitaMedica cita = em.find(CitaMedica.class, id);
            if (cita != null) {
                em.remove(cita); // Pasa a estado Removed
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // =======================================================
    // CONSULTAS JPQL SEGÚN REQUERIMIENTOS DE LA UNIDAD 2
    // =======================================================

    // 1. JPQL Básica: Listado completo de la entidad
    public List<CitaMedica> listarTodas() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT c FROM CitaMedica c ORDER BY c.id ASC";
            return em.createQuery(jpql, CitaMedica.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 2. JPQL con Parámetro Nombrado (WHERE :estado): Filtrado por estado
    public List<CitaMedica> buscarPorEstado(String estado) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT c FROM CitaMedica c WHERE c.estado = :estado";
            return em.createQuery(jpql, CitaMedica.class)
                     .setParameter("estado", estado)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // 3. JPQL con JOIN FETCH optimizado: Carga Cita, Paciente, Médico y Diagnósticos en una sola lectura
    public List<CitaMedica> listarConDetallesCompletos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM CitaMedica c " +
                          "JOIN FETCH c.paciente " +
                          "JOIN FETCH c.medico " +
                          "LEFT JOIN FETCH c.diagnosticos " +
                          "ORDER BY c.id ASC";
            return em.createQuery(jpql, CitaMedica.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 4. JPQL de Agregación: Conteo de citas asignadas a un médico
    public Long contarCitasPorMedico(int medicoId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM CitaMedica c WHERE c.medico.id = :medicoId";
            return em.createQuery(jpql, Long.class)
                     .setParameter("medicoId", medicoId)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    // 5. JPQL de Proyección Escalar: Consulta de columnas específicas (menos consumo de memoria)
    public List<Object[]> obtenerResumenCitas() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT c.id, c.fecha, c.hora, c.paciente.nombre, c.medico.nombre FROM CitaMedica c";
            return em.createQuery(jpql, Object[].class).getResultList();
        } finally {
            em.close();
        }
    }
}