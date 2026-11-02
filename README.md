# Sistema de Gestión de Citas Médicas - Hospital Metropolitano (Unidad 2)

Trabajo desarrollado en **Java (Swing)** implementando la persistencia mediante **Jakarta Persistence (JPA)**, el motor ORM **Hibernate** y base de datos relacional **PostgreSQL**. Esta segunda entrega amplía el dominio a un modelo estructurado de cinco entidades conectadas mediante relaciones uno a muchos y consultas optimizadas con **JPQL**.

---

## 📌 Contexto del Dominio y Modelo de Datos
El sistema automatiza el circuito de atención ambulatoria para el **Hospital Metropolitano**, cubriendo el flujo clínico desde la adscripción médica hasta el registro diagnóstico:

* **Departamento:** Especialidades clínicas del hospital (ej. Cardiología, Pediatría) que agrupan al personal médico (`1:N` con Médico).
* **Medico:** Profesional tratante asignado a un departamento y responsable del agendamiento de atenciones (`1:N` con CitaMédica).
* **Paciente:** Usuario del sistema con identificación, datos personales y de contacto (`1:N` con CitaMédica).
* **CitaMedica:** Entidad central que coordina el turno, fecha, hora y estado de la consulta médica (`N:1` con Paciente, `N:1` con Médico y `1:N` con Diagnóstico).
* **Diagnostico:** Conclusión clínica y receta prescrita al paciente tras la atención de la cita (`N:1` con CitaMédica).

---

## 🔍 Consultas JPQL Implementadas (CitaMedicaDAO)
El acceso a datos cumple con los lineamientos de la Unidad 2, aplicando consultas orientadas a objetos:
1. **Selección Básica:** Listado ordenado completo de citas (`SELECT c FROM CitaMedica c`).
2. **Parámetros Nombrados:** Filtrado por estado de la cita previniendo inyecciones SQL (`WHERE c.estado = :estado`).
3. **Optimización con JOIN FETCH:** Mitigación del problema $N+1$ cargando en una sola sentencia la cita, paciente, médico y diagnósticos (`JOIN FETCH c.paciente JOIN FETCH c.medico LEFT JOIN FETCH c.diagnosticos`).
4. **Funciones de Agregación:** Conteo de citas programadas por médico (`SELECT COUNT(c) FROM CitaMedica c WHERE c.medico.id = :id`).
5. **Proyección Escalar:** Selección selectiva de campos para optimizar el consumo de memoria.

---

## 🛠️ Tecnologías y Dependencias
* **Lenguaje:** Java 17 LTS
* **Entorno / IDE:** Apache NetBeans 
* **Gestor de Dependencias:** Apache Maven (definido en `pom.xml`)
* **ORM:** Hibernate ORM Core 7.4.12.Final
* **Estándar:** Jakarta Persistence API 3.1.0
* **Base de Datos:** PostgreSQL
* **Driver JDBC:** PostgreSQL Driver 42.7.12
* **Modelado Conceptual:** StarUML (archivo `Diagrama UML 2.jpg` incluido en la raíz)

---

## 🏛️ Estructura del Repositorio

```text
hospitalMetropolitanoUnidad2/
├── src/main/
│   ├── java/com/mycompany/hospitalmetropolitanocrud/
│   │   ├── modelo/                  # Entidades JPA (Departamento, Medico, Paciente, CitaMedica, Diagnostico)
│   │   ├── dao/                     # Acceso a datos y consultas JPQL (CitaMedicaDAO.java)
│   │   ├── vista/                   # Interfaz gráfica Swing (VentanaHospital.java)
│   │   └── HospitalMetropolitanoCrud.java # Clase principal de arranque
│   └── resources/
│       └── META-INF/
│           └── persistence.xml      # Unidad de persistencia (HospitalMetropolitanoPU)
├── Diagrama UML 2.jpg               # Modelo conceptual de 5 clases y relaciones 1:N
└── pom.xml                          # Dependencias y configuración Maven
```

---

## ⚙️ Configuración y Despliegue Local

1. **Base de Datos:**
   * Crear la base de datos `hospital_db` en PostgreSQL.
   * Validar en `src/main/resources/META-INF/persistence.xml` que las credenciales coincidan con el entorno local.
   * La propiedad `hibernate.hbm2ddl.auto = update` mapeará automáticamente las 5 tablas con sus claves foráneas[cite: 4].

2. **Compilación y Ejecución:**
   * Abrir el proyecto en Apache NetBeans.
   * Ejecutar un ciclo de compilación (*Clean and Build*).
   * Iniciar la aplicación ejecutando la clase principal `HospitalMetropolitanoCrud.java` (`F6` o `Shift + F6`).
