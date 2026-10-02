package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
}
