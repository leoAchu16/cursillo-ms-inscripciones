package org.cursillo.inscripciones.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cursillo.commons.dto.DescuentoDTO;
import org.cursillo.commons.entities.descuentos.Descuento;
import org.cursillo.inscripciones.exception.BadRequestException;
import org.cursillo.inscripciones.exception.NotFoundException;
import org.cursillo.inscripciones.mapper.DescuentoMapper;
import org.cursillo.inscripciones.repository.DescuentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DescuentoService {

    private final DescuentoRepository descuentoRepo;
    private final DescuentoMapper descuentoMapper;

    /**
     * Crea un nuevo descuento.
     * 
     * @param dto El DTO con los datos del descuento.
     * @return El descuento creado.
     */
    @Transactional
    public DescuentoDTO crearDescuento(DescuentoDTO dto) {
        validarDescuento(dto);
        log.info("Creando descuento...");
        Descuento descuento = descuentoMapper.toEntity(dto);
        return descuentoMapper.toDto(descuentoRepo.save(descuento));
    }

    /**
     * Actualiza un descuento existente.
     * 
     * @param id  El ID del descuento a actualizar.
     * @param dto El DTO con los datos actualizados del descuento.
     * @return El descuento actualizado.
     */
    @Transactional
    public DescuentoDTO actualizarDescuento(Integer id, DescuentoDTO dto) {
        log.info("Actualizando descuento con id: {}", id);
        Descuento descuento = descuentoRepo.findByIdDescuentoAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("Descuento no encontrado"));

        descuento.setNombre(dto.getNombre());
        descuento.setPorcentaje(dto.getPorcentaje());
        descuento.setDescripcion(dto.getDescripcion());

        return descuentoMapper.toDto(descuentoRepo.save(descuento));
    }

    /**
     * Obtiene un descuento por su ID.
     * 
     * @param id El ID del descuento a obtener.
     * @return El descuento obtenido.
     */
    @Transactional
    public DescuentoDTO obtenerDescuentoPorId(Integer id) {
        log.info("Obteniendo descuento con id: {}", id);
        Descuento descuento = descuentoRepo.findByIdDescuentoAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("Descuento no encontrado."));
        return descuentoMapper.toDto(descuento);
    }

    /**
     * Lista los descuentos activos con paginacion.
     * 
     * @param pageable El objeto Pageable con la informacion de paginacion.
     * @return Un Page de DescuentoDTO con los descuentos activos.
     */
    @Transactional
    public Page<DescuentoDTO> listarDescuentos(String nombreDescuento, Pageable pageable) {
        if (nombreDescuento == null || nombreDescuento.isEmpty()) {
            log.info("Listando todos los descuentos con paginacion...");
            return descuentoRepo.findByActivoTrue(pageable)
                    .map(descuentoMapper::toDto);
        } else {
            return buscarPorNombre(nombreDescuento, pageable);
        }
    }

    /**
     * Elimina un descuento por su ID (borrado logico).
     * 
     * @param id El ID del descuento a eliminar.
     */
    @Transactional
    public void eliminarDescuento(Integer id) {
        log.info("Eliminando descuento con id: {}", id);
        Descuento descuento = descuentoRepo.findByIdDescuentoAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("Descuento no encontrado"));

        descuento.setActivo(false);
        descuentoRepo.save(descuento);
    }

    // funciones auxiliares

    private Page<DescuentoDTO> buscarPorNombre(String nombreDescuento, Pageable pageable) {
        log.info("Buscando descuentos por nombre: {}", nombreDescuento);
        return descuentoRepo.findByNombreContainingIgnoreCaseAndActivoTrue(nombreDescuento, pageable)
                .map(descuentoMapper::toDto);
    }

    private void validarDescuento(DescuentoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del descuento es obligatorio.");
        }
        if (dto.getPorcentaje() == null || dto.getPorcentaje() < 0 || dto.getPorcentaje() > 100) {
            throw new BadRequestException("El porcentaje debe ser un valor entre 0 y 100.");
        }
        if (descuentoRepo.existsByNombreAndActivoTrue(dto.getNombre())) {
            throw new BadRequestException("Ya existe un descuento con ese nombre.");
        }
    }
}