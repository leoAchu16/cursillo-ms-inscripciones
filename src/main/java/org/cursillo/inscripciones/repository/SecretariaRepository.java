package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.Usuarios.Secretaria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretariaRepository extends JpaRepository<Secretaria, Integer> {
}
