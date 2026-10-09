package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Materias.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {
}
