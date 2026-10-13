package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.descuentos.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescuentoRepository extends JpaRepository<Descuento, Integer> {

}
