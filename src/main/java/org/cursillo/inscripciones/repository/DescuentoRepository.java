package org.cursillo.inscripciones.repository;

import org.cursillo.commons.entities.descuentos.Descuento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DescuentoRepository extends JpaRepository<Descuento, Integer> {
    Optional<Descuento> findByIdDescuentoAndActivoTrue(Integer idDescuento);

    Page<Descuento> findByActivoTrue(Pageable pageable);

    Page<Descuento> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    boolean existsByNombreAndActivoTrue(String nombre);
}
