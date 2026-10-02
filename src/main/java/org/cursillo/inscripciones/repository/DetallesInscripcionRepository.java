package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Inscripciones.DetallesInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallesInscripcionRepository extends JpaRepository<DetallesInscripcion, Integer> {
}
