package org.cursillo.inscripciones.repository;

import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.cursillo.commons.entities.Usuarios.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    //esto busca inscripciones por id y activo true
    Optional<Inscripcion> findByIdInscripcionAndActivoTrue(Integer idInscripcion);

    //esto busca inscripciones de un alumno o alumnos (segun la lista de alumnos) con paginacion
    Page<Inscripcion> findByAlumnoInAndActivoTrue(List<Alumno> alumnos, Pageable pageable);

    Page<Inscripcion> findAllByActivoTrue(Pageable pageable);
}
