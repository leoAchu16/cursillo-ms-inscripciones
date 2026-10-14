package org.cursillo.inscripciones.service;

import org.cursillo.commons.entities.Conceptos.Concepto;
import org.cursillo.commons.entities.Enums.TipoInscripcion;
import org.cursillo.commons.entities.Usuarios.Alumno;
import org.cursillo.inscripciones.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.cursillo.inscripciones.exception.BadRequestException;
import org.cursillo.commons.dto.DetallesInscripcionDTO;
import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.commons.entities.Enums.EstadoPago;
import org.cursillo.commons.entities.Inscripciones.DetalleExamen;
import org.cursillo.commons.entities.Inscripciones.DetalleMateria;
import org.cursillo.commons.entities.Inscripciones.DetallesInscripcion;
import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.cursillo.inscripciones.exception.BusinessException;
import org.cursillo.inscripciones.mapper.InscripcionMapper;
import org.cursillo.inscripciones.repository.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InscripcionService {
    private final InscripcionRepository inscripcionRepo;
    private final InscripcionMapper inscripcionMapper;

    private final MateriaRepository materiaRepo;
    private final ExamenRepository examenRepo;

    private final AlumnoRepository alumnoRepo;
    private final SecretariaRepository secretariaRepo;

    private final ConceptoRepository conceptoRepo;

    @Transactional
    public InscripcionDTO crearInscripcion(InscripcionDTO dto) {
        log.info("Iniciando creacion de inscripcion para el alumno con ID: {}", dto.getIdAlumno());

        // Validaciones basicas
        if (dto.getDetallesInscripcion() == null || dto.getDetallesInscripcion().isEmpty())
            throw new BadRequestException("La inscripcion debe tener al menos un detalle.");

        // Mapeo DTO a Entity
        Inscripcion inscripcion = inscripcionMapper.toEntity(dto);

        // setea alumno si encuentra un alumno con ese id
        inscripcion.setAlumno(alumnoRepo.findById(dto.getIdAlumno())
                .orElseThrow(() -> new NotFoundException("Alumno no encontrado. Id: " + dto.getIdAlumno())));

        // setea secretaria si encuentra una secretaria con ese id, si idSecretaria es
        // null, lo setea como null
        inscripcion.setSecretaria(dto.getIdSecretaria() != null ? secretariaRepo.findById(dto.getIdSecretaria())
                .orElseThrow(() -> new NotFoundException("Secretaria no encontrado. Id: " + dto.getIdSecretaria()))
                : null);

        Integer costoMateria = obtenerCostoConcepto(TipoInscripcion.MATERIA);
        Integer costoExamen = obtenerCostoConcepto(TipoInscripcion.EXAMEN);

        for (int i = 0; i < dto.getDetallesInscripcion().size(); i++) {
            DetallesInscripcionDTO detalleDTO = dto.getDetallesInscripcion().get(i);
            DetallesInscripcion detalle = inscripcion.getDetalles().get(i);

            if (detalle instanceof DetalleMateria detalleMateria) {
                if (detalleDTO.getIdMateria() == null)
                    throw new BadRequestException("Un detalle tipo MATERIA requiere idMateria");
                detalleMateria.setMateria(
                        materiaRepo.findById(detalleDTO.getIdMateria())
                                .orElseThrow(() -> new BadRequestException(
                                        "No existe una materia con ese id: " + detalleDTO.getIdMateria())));
                detalleMateria.setCosto(costoMateria);
            } else if (detalle instanceof DetalleExamen detalleExamen) {
                if (detalleDTO.getIdExamen() == null)
                    throw new BadRequestException("Un detalle tipo EXAMEN requiere idExamen.");
                detalleExamen.setExamen(examenRepo.findById(detalleDTO.getIdExamen())
                        .orElseThrow(() -> new BadRequestException(
                                "No existe un examen con ese id: " + detalleDTO.getIdExamen())));
                detalleExamen.setCosto(costoExamen);
            } else // maneja el caso en que el detalle no sea ni materia ni examen
                throw new BadRequestException("Tipo de detalle desconocido.");
        }
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstadoPago(EstadoPago.PENDIENTE);
        inscripcion.setTotal(inscripcion.getDetalles().stream()
                .mapToInt(DetallesInscripcion::getCosto).sum());
        
        log.debug("Total de inscripcion calculado: {}", inscripcion.getTotal());
        log.info("Guardando nueva inscripcion en la base de datos.");

        return inscripcionMapper.toDto(inscripcionRepo.save(inscripcion));
    }

    /**
     * Actualiza una inscripción existente.
     * 
     * @param dto El DTO con los datos actualizados de la inscripción.
     * @return La inscripción actualizada.
     */
    @Transactional
    public InscripcionDTO actualizarInscripcion(Integer id, InscripcionDTO dto) {
        log.info("Iniciando actualizacion de inscripcion con ID: {}", id);

        // validar que haya detalles en el dto
        if (dto.getDetallesInscripcion() == null || dto.getDetallesInscripcion().isEmpty())
            throw new BadRequestException("La inscripcion debe tener al menos un detalle.");

        // obtener inscripcion existente
        Inscripcion inscripcionExistente = inscripcionRepo.findByIdInscripcionAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("La inscripcion no existe."));

        if (inscripcionExistente.getEstadoPago() == EstadoPago.PAGADO)
            throw new BusinessException("No se puede modificar una inscripcion pagada.");

        Integer costoMateria = obtenerCostoConcepto(TipoInscripcion.MATERIA);
        Integer costoExamen = obtenerCostoConcepto(TipoInscripcion.EXAMEN);

        // Limpiamos los detalles actuales para reemplazarlos con los nuevos
        inscripcionExistente.getDetalles().clear();

        // Mapeamos los nuevos detalles usando el mapper
        Inscripcion inscripcionTemporal = inscripcionMapper.toEntity(dto);
        List<DetallesInscripcion> nuevosDetalles = inscripcionTemporal.getDetalles();

        for (int i = 0; i < dto.getDetallesInscripcion().size(); i++) {
            DetallesInscripcionDTO detalleDTO = dto.getDetallesInscripcion().get(i);
            DetallesInscripcion detalle = nuevosDetalles.get(i);

            if (detalle instanceof DetalleMateria detalleMateria) {
                if (detalleDTO.getIdMateria() == null)
                    throw new BadRequestException("Un detalle tipo MATERIA requiere idMateria");
                detalleMateria.setMateria(
                        materiaRepo.findById(detalleDTO.getIdMateria())
                                .orElseThrow(() -> new BadRequestException(
                                        "No existe una materia con ese id: " + detalleDTO.getIdMateria())));
                detalleMateria.setCosto(costoMateria);
            } else if (detalle instanceof DetalleExamen detalleExamen) {
                if (detalleDTO.getIdExamen() == null)
                    throw new BadRequestException("Un detalle tipo EXAMEN requiere idExamen.");
                detalleExamen.setExamen(examenRepo.findById(detalleDTO.getIdExamen())
                        .orElseThrow(() -> new BadRequestException(
                                "No existe un examen con ese id: " + detalleDTO.getIdExamen())));
                detalleExamen.setCosto(costoExamen);
            } else {
                throw new BadRequestException("Tipo de detalle desconocido.");
            }

            // Relacionamos con la inscripcion existente
            detalle.setInscripcion(inscripcionExistente);
            inscripcionExistente.getDetalles().add(detalle);
        }

        // Recalculamos el total
        inscripcionExistente.setTotal(inscripcionExistente.getDetalles().stream()
                .mapToInt(DetallesInscripcion::getCosto).sum());
        
        log.debug("Nuevo total de inscripcion calculado: {}", inscripcionExistente.getTotal());
        log.info("Guardando inscripcion actualizada en la base de datos.");

        return inscripcionMapper.toDto(inscripcionRepo.save(inscripcionExistente));
    }

    /**
     * Obtiene una inscripción por su ID.
     * 
     * @param id El ID de la inscripción a obtener.
     * @return La inscripción encontrada.
     */
    @Transactional
    public InscripcionDTO obtenerPorId(Integer id) {
        log.info("Buscando inscripcion por ID: {}", id);
        Inscripcion inscripcion = inscripcionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscripcion no encontrada."));
        return inscripcionMapper.toDto(inscripcion);
    }

    /**
     * Lista todas las inscripciones con paginación.
     * 
     * @param pageable La configuración de paginación.
     * @return Una página de inscripciones.
     */
    @Transactional
    public Page<InscripcionDTO> listarInscripciones(String nombre, Pageable pageable) {
        if(nombre != null && !nombre.isBlank()) {
            log.info("Listando inscripciones para alumnos con nombre que contenga: {}", nombre);
            return buscar(nombre, pageable);
        }
        log.info("Listando inscripciones con paginacion");
        return inscripcionRepo.findAllByActivoTrue(pageable).map(inscripcionMapper::toDto);
    }

    /**
     * Elimina una inscripción por su ID (borrado lógico).
     * 
     * @param id El ID de la inscripción a eliminar.
     */
    @Transactional
    public void eliminarInscripcion(Integer id) {
        log.info("Iniciando eliminacion logica de inscripcion con ID: {}", id);
        Inscripcion existente = inscripcionRepo.findByIdInscripcionAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("Inscripcion no encontrada. Id: " + id));
        existente.setActivo(false);
        inscripcionRepo.save(existente);
    }



    // funciones auxiliares

    /**
     * Busca inscripciones por el nombre de un alumno.
     * 
     * @param nombre   El nombre del alumno a buscar.
     * @param pageable La configuración de paginación.
     * @return Una página de inscripciones que coinciden con el criterio de
     *         búsqueda.
     */
    private Page<InscripcionDTO> buscar(String nombre, Pageable pageable) {
        log.info("Buscando inscripciones para alumnos con nombre que contenga: {}", nombre);
        List<Alumno> alumnos = alumnoRepo.findByNombreContainingIgnoreCase(nombre);
        if (alumnos.isEmpty())
            return Page.empty(pageable);
        return inscripcionRepo.findByAlumnoInAndActivoTrue(alumnos, pageable)
                .map(inscripcionMapper::toDto);
    }

    /**
     * Obtiene un concepto por su tipo.
     * 
     * @param tipoInscripcion El tipo de concepto a obtener.
     * @return El concepto encontrado.
     */
    private Integer obtenerCostoConcepto(TipoInscripcion tipoInscripcion) {
        String nombre = switch (tipoInscripcion) {
            case MATERIA -> "materia"; // o podria ser matricula, depende de como se llame en la bd
            case EXAMEN -> "examen";
        };
        Concepto concepto = conceptoRepo.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new NotFoundException("No se encontró un concepto con el nombre: " + nombre));
        return concepto.getMontoBase();
    }

    /**
     * Normaliza un string para que pueda ser comparado con otro.
     * 
     * @param s El string a normalizar.
     * @return El string normalizado.
     */
    private String normalizar(String s) {
        if (s == null)
            return null;
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // quita acentos
                .replaceAll("\\s+", "") // quita todos los espacios
                .toLowerCase(Locale.ROOT);
    }
}
