package org.cursillo.inscripciones.controller;

import java.net.URI;

import org.cursillo.commons.dto.DescuentoDTO;
import org.cursillo.inscripciones.service.DescuentoService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/descuentos")
@RequiredArgsConstructor
public class DescuentoController {

    private final DescuentoService service;

    /**
     * Crea un descuento.
     * 
     * @param dto El DTO con los datos del descuento a crear.
     * @return Retorna 201 Created con el descuento creado.
     */
    @PostMapping
    public ResponseEntity<DescuentoDTO> create(@RequestBody @Valid DescuentoDTO dto) {
        DescuentoDTO creada = service.crearDescuento(dto);
        log.info("POST /descuentos - creando descuento");
        URI location = URI.create("/descuentos/" + creada.getIdDescuento());
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * Actualiza un descuento.
     * 
     * @param id  El ID del descuento a actualizar.
     * @param dto El DTO con los datos del descuento a actualizar.
     * @return Retorna 200 OK con el descuento actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DescuentoDTO> update(@PathVariable Integer id, @RequestBody @Valid DescuentoDTO dto) {
        DescuentoDTO actualizado = service.actualizarDescuento(id, dto);
        log.info("PUT /descuentos/{} - actualizando descuento", id);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Obtiene un descuento por su ID.
     * 
     * @param id El ID del descuento a obtener.
     * @return Retorna 200 OK con el descuento obtenido.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DescuentoDTO> getById(@PathVariable Integer id) {
        DescuentoDTO descuento = service.obtenerDescuentoPorId(id);
        log.info("GET /descuentos/{} - obteniendo descuento", id);
        URI location = URI.create("/descuentos/" + descuento.getIdDescuento());
        return ResponseEntity.ok().location(location).body(descuento);
    }

    /**
     * Lista todos los descuentos.
     * 
     * @param pageable La informacion de paginacion.
     * @return Retorna 200 OK con una pagina de descuentos.
     */
    @GetMapping
    public ResponseEntity<Page<DescuentoDTO>> getAll(@RequestParam(required = false) String nombreDescuento,
            @ParameterObject Pageable pageable) {
        Page<DescuentoDTO> page = service.listarDescuentos(nombreDescuento, pageable);
        log.info("GET /descuentos - listando descuentos (pagina: {}, tamaño: {})", pageable.getPageNumber(),
                pageable.getPageSize());
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.eliminarDescuento(id);
        log.info("DELETE /descuentos/{} - eliminando descuento", id);
        return ResponseEntity.noContent().build();
    }
}
