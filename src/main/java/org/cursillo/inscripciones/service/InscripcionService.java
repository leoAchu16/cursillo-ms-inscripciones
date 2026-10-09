package org.cursillo.inscripciones.service;

import org.cursillo.commons.entities.Conceptos.Concepto;
import org.cursillo.commons.entities.Enums.TipoInscripcion;
import org.cursillo.commons.entities.Usuarios.Alumno;
import org.cursillo.inscripciones.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

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

        // Validaciones basicas
        if (dto.getIdAlumno() == null)
            throw new BadRequestException("El alumno es obligatorio.");
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
            } else if (detalle instanceof DetalleExamen detalleExamen) {
                if (detalleDTO.getIdExamen() == null)
                    throw new BadRequestException("Un detalle tipo EXAMEN requiere idExamen.");
                detalleExamen.setExamen(examenRepo.findById(detalleDTO.getIdExamen())
                        .orElseThrow(() -> new BadRequestException(
                                "No existe un examen con ese id: " + detalleDTO.getIdExamen())));
            } else
                throw new BadRequestException("Tipo de detalle desconocido.");// maneja el caso en que el detalle no sea
                                                                              // ni materia ni examen
        }
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstadoPago(dto.getIdSecretaria() != null ? EstadoPago.PAGADO : EstadoPago.PENDIENTE);
        inscripcion.setTotal(inscripcion.getDetalles().stream()
                .mapToInt(DetallesInscripcion::getCosto).sum());

        return inscripcionMapper.toDto(inscripcionRepo.save(inscripcion));
    }

    /**
     * Actualiza una inscripción existente.
     * 
     * @param dto El DTO con los datos actualizados de la inscripción.
     * @return La inscripción actualizada.
     */
    @Transactional
    public InscripcionDTO actualizarInscripcion(InscripcionDTO dto) {
        throw new UnsupportedOperationException("Aun falta implementar.");
    }

    /**
     * Obtiene una inscripción por su ID.
     * 
     * @param id El ID de la inscripción a obtener.
     * @return La inscripción encontrada.
     */
    @Transactional
    public InscripcionDTO obtenerPorId(Integer id) {
        throw new UnsupportedOperationException("Aun falta implementar.");
    }

    /**
     * Lista todas las inscripciones con paginación.
     * 
     * @param pageable La configuración de paginación.
     * @return Una página de inscripciones.
     */
    @Transactional
    public Page<InscripcionDTO> listarInscripciones(Pageable pageable) {
        return inscripcionRepo.findAll(pageable).map(inscripcion -> inscripcionMapper.toDto(inscripcion));
    }

    /**
     * Elimina una inscripción por su ID (borrado lógico).
     * 
     * @param id El ID de la inscripción a eliminar.
     */
    @Transactional
    public void eliminarInscripcion(Integer id) {
        Inscripcion existente = inscripcionRepo.findByIdInscripcionAndActivoTrue(id)
                .orElseThrow(() -> new NotFoundException("Inscripcion no encontrada. Id: " + id));
        existente.setActivo(false);
        inscripcionRepo.save(existente);
    }

    /**
     * Busca inscripciones por el nombre de un alumno.
     * 
     * @param nombre   El nombre del alumno a buscar.
     * @param pageable La configuración de paginación.
     * @return Una página de inscripciones que coinciden con el criterio de
     *         búsqueda.
     */
    @Transactional
    public Page<InscripcionDTO> buscar(String nombre, Pageable pageable) {
        List<Alumno> alumnos = alumnoRepo.findByNombreContaining(nombre);
        if (alumnos.isEmpty())
            throw new NotFoundException("No se encontraron alumnos con el nombre: " + nombre);
        return inscripcionRepo.findByAlumnoIn(alumnos, pageable)
                .map(inscripcion -> inscripcionMapper.toDto(inscripcion));
    }

    // funciones aux

    /**
     * Obtiene un concepto por su tipo.
     * 
     * @param tipoInscripcion El tipo de concepto a obtener.
     * @return El concepto encontrado.
     */
    private Concepto obtenerConcepto(TipoInscripcion tipoInscripcion) {
        String nombre = switch (tipoInscripcion) {
            case MATERIA -> "materia";
            case EXAMEN -> "examen";
        };
        return conceptoRepo.findByNombre(normalizar(nombre))
                .orElseThrow(() -> new NotFoundException("No se encontró un concepto con el nombre: " + nombre));
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
