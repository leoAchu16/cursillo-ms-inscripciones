package org.cursillo.inscripciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.inscripciones.service.InscripcionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/inscripciones")
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
        log.info("POST /inscripciones - creando inscripcion");
        InscripcionDTO creada = service.crearInscripcion(dto);
        URI location = URI.create("/inscripciones/" + creada.getIdInscripcion());
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * Actualiza una inscripcion.
     * 
     * @param idInscripcion El ID de la inscripcion a actualizar.
     * @param dto           El DTO con los datos actualizados de la inscripcion.
     * @return Retorna 200 Created con la inscripcion actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InscripcionDTO> update(@PathVariable("id") Integer idInscripcion, @RequestBody @Valid InscripcionDTO dto) {
        log.info("PUT /inscripciones/" + idInscripcion + " - actualizando inscripcion");
        InscripcionDTO actualizado = service.actualizarInscripcion(idInscripcion, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Obtiene una inscripcion por ID.
     * 
     * @param idInscripcion El ID de la inscripcion a obtener.
     * @return Retorna 200 OK con la inscripcion solicitada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionDTO> getById(@PathVariable("id") Integer idInscripcion) {
        log.info("GET /inscripciones/" + idInscripcion + " - obteniendo inscripcion");
        InscripcionDTO inscripcion = service.obtenerPorId(idInscripcion);
        return ResponseEntity.ok(inscripcion);
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
    public ResponseEntity<Page<InscripcionDTO>> getAll(@RequestParam(required = false) String nombreAlumno, @ParameterObject Pageable pageable) {
        log.info("GET /inscripciones - listando inscripciones (pagina: {}, tamaño: {})", pageable.getPageNumber(), pageable.getPageSize());
        Page<InscripcionDTO> page = service.listarInscripciones(nombreAlumno, pageable);

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
        log.info("DELETE /inscripciones/" + idInscripcion + " - eliminando inscripcion");
        service.eliminarInscripcion(idInscripcion);
        return ResponseEntity.noContent().build();
    }

}
