package org.cursillo.inscripciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.inscripciones.service.InscripcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService service;

    @PostMapping
    public ResponseEntity<InscripcionDTO> create(@RequestBody @Valid InscripcionDTO dto){
        InscripcionDTO creada = service.crearInscripcion(dto);
        log.info("POST /inscripciones - creando inscripcion para alumno con id:  {}", dto.getIdAlumno());
        URI location = URI.create("/inscripciones" + creada.getIdInscripcion());
        return ResponseEntity.created(location).body(creada);
    }
}
