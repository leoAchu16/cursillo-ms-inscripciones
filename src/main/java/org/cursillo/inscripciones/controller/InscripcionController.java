package org.cursillo.inscripciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.inscripciones.service.InscripcionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService service;

    /**
     * Crea una inscripcion.
     * 
     * @param dto El DTO con los datos de la inscripcion a crear.
     * @return La inscripcion creada.
     */
    @PostMapping
    public ResponseEntity<InscripcionDTO> create(@RequestBody @Valid InscripcionDTO dto) {
        InscripcionDTO creada = service.crearInscripcion(dto);
        log.info("POST /api/inscripciones - creando inscripcion");
        URI location = URI.create("/api/inscripciones/" + creada.getIdInscripcion());
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * Actualiza una inscripcion.
     * 
     * @param idInscripcion El ID de la inscripcion a actualizar.
     * @param dto           El DTO con los datos actualizados de la inscripcion.
     * @return Retorna 201 Created con la inscripcion actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InscripcionDTO> update(@PathVariable("id") Integer idInscripcion,
            @RequestBody @Valid InscripcionDTO dto) {
        InscripcionDTO actualizado = service.actualizarInscripcion(idInscripcion, dto);
        log.info("PUT /api/inscripciones/" + idInscripcion + " - actualizando inscripcion");
        URI location = URI.create("/api/inscripciones/" + actualizado.getIdInscripcion());
        return ResponseEntity.ok().location(location).body(actualizado);
    }

    /**
     * Obtiene una inscripcion por ID.
     * 
     * @param idInscripcion El ID de la inscripcion a obtener.
     * @return Retorna 200 OK con la inscripcion solicitada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionDTO> getById(@PathVariable("id") Integer idInscripcion) {
        InscripcionDTO inscripcion = service.obtenerPorId(idInscripcion);
        log.info("GET /api/inscripciones/" + idInscripcion + " - obteniendo inscripcion");
        URI location = URI.create("/api/inscripciones/" + inscripcion.getIdInscripcion());
        return ResponseEntity.ok().location(location).body(inscripcion);
    }

    /**
     * Lista todas las inscripciones.
     * 
     * @param nombreAlumno El nombre del alumno a buscar. Si se proporciona, se
     *                     buscaran las inscripciones del alumno.
     * @param pageable     La informacion de paginacion.
     * @return Retorna 200 OK con una pagina de inscripciones.
     */
    @GetMapping
    public ResponseEntity<Page<InscripcionDTO>> getAll(@RequestParam(required = false) String nombreAlumno,
            Pageable pageable) {

        Page<InscripcionDTO> page;
        if (nombreAlumno != null && !nombreAlumno.isBlank())
            page = service.buscar(nombreAlumno, pageable);
        else
            page = service.listarInscripciones(pageable);
        log.info("GET /api/inscripciones - listando inscripciones (pagina: {}, tamaño: {})", pageable.getPageNumber(),
                pageable.getPageSize());

        return ResponseEntity.ok(page);
    }

    /**
     * Elimina una inscripcion.
     * 
     * @param idInscripcion El ID de la inscripcion a eliminar.
     * @return Retorna 204 No Content si la inscripcion fue eliminada exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer idInscripcion) {
        service.eliminarInscripcion(idInscripcion);
        log.info("DELETE /api/inscripciones/" + idInscripcion + " - eliminando inscripcion");
        return ResponseEntity.noContent().build();
    }

}
