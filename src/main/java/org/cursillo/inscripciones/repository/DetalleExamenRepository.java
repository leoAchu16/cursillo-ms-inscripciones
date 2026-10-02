package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Inscripciones.DetalleMateria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleExamenRepository extends JpaRepository<DetalleMateria, Integer> {
}
