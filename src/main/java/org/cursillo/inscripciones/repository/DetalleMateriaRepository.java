package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Inscripciones.DetalleMateria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleMateriaRepository extends JpaRepository<DetalleMateria, Integer> {
}
