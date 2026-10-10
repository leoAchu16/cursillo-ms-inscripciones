package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Conceptos.Concepto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {
    Optional<Concepto> findByNombreIgnoreCase(String nombre);
}
