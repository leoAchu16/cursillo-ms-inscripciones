package org.cursillo.inscripciones.mapper;

import org.cursillo.commons.dto.InscripcionDTO;
import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = DetallesInscripcionMapper.class)
public interface InscripcionMapper {

    @Mapping(target = "idInscripcion", ignore = true)
    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "secretaria", ignore = true)
    @Mapping(target = "descuento", ignore = true)
    @Mapping(target = "detalles", source = "detallesInscripcion")
    Inscripcion toEntity(InscripcionDTO dto);

    @Mapping(target = "idAlumno", source = "alumno.idUsuario")
    @Mapping(target = "idSecretaria", source = "secretaria.idUsuario")
    @Mapping(target = "idDescuento", source = "descuento.idDescuento")
    @Mapping(target = "detallesInscripcion", source = "detalles")
    InscripcionDTO toDto(Inscripcion inscripcion);

    @AfterMapping
    default void vincularDetallesInscripcion(@MappingTarget Inscripcion inscripcion) {
        if (inscripcion.getDetalles() != null) {
            inscripcion.getDetalles().forEach(detalle -> detalle.setInscripcion(inscripcion));
        }
    }
}
