package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    // esto busca inscripciones por id y activo true
    Optional<Inscripcion> findByIdInscripcionAndActivoTrue(Integer idInscripcion);

    // esto busca inscripciones de alumnos por nombre con paginacion
    Page<Inscripcion> findByAlumnoNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    Page<Inscripcion> findAllByActivoTrue(Pageable pageable);
}
