package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Examenes.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {
}
