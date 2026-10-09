package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Usuarios.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    //esto busca alumnos por nombre
    List<Alumno> findByNombreContaining(String nombre);
}
