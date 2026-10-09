package org.cursillo.inscripciones.mapper;

import org.cursillo.commons.dto.DetallesInscripcionDTO;
import org.cursillo.commons.dto.TipoInscripcion;
import org.cursillo.commons.entities.Inscripciones.DetalleExamen;
import org.cursillo.commons.entities.Inscripciones.DetalleMateria;
import org.cursillo.commons.entities.Inscripciones.DetallesInscripcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface DetallesInscripcionMapper {

    // decide que tipo de detalle de inscripcion crear segun el tipo de inscripcion en el DTO
    @ObjectFactory
    default DetallesInscripcion crearDetalle(DetallesInscripcionDTO dto) {
        if (dto.getTipoInscripcion() == null)
            throw new IllegalArgumentException("El tipo de inscripcion no puede ser null");
        else {
            if (dto.getTipoInscripcion() == TipoInscripcion.MATERIA)
                return new DetalleMateria();
            else if (dto.getTipoInscripcion() == TipoInscripcion.EXAMEN)
                return new DetalleExamen();
            else
                throw new IllegalArgumentException("Tipo de inscripcion no reconocido: " + dto.getTipoInscripcion());
        }
    }

    @Mapping(target = "idDetalleInscripcion", ignore = true)
    @Mapping(target = "inscripcion", ignore = true)
    DetallesInscripcion toEntity(DetallesInscripcionDTO dto);

    // Metodo mapper manual Entidad a DTO, porque no se puede mapear una clase abstracta a un DTO
    default DetallesInscripcionDTO toDto(DetallesInscripcion detalle) {
        if (detalle == null) return null;

        DetallesInscripcionDTO dto = new DetallesInscripcionDTO()
                .idDetalleInscripcion(detalle.getIdDetalleInscripcion())
                .idInscripcion(detalle.getInscripcion() != null ? detalle.getInscripcion().getIdInscripcion() : null)
                .costo(detalle.getCosto());
        if (detalle instanceof DetalleMateria detalleMateria) {
            dto.setTipoInscripcion(TipoInscripcion.MATERIA);
            dto.setIdMateria(detalleMateria.getMateria().getIdMateria());
        }
        else if (detalle instanceof DetalleExamen detalleExamen) {
            dto.setTipoInscripcion(TipoInscripcion.EXAMEN);
            dto.setIdExamen(detalleExamen.getExamen().getIdExamen());
        }
        return dto;
    }


}
