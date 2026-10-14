package org.cursillo.inscripciones.mapper;

import org.cursillo.commons.dto.DescuentoDTO;
import org.cursillo.commons.entities.descuentos.Descuento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DescuentoMapper {

    DescuentoDTO toDto(Descuento entity);

    @Mapping(target = "idDescuento", ignore = true)
    @Mapping(target = "creacion", ignore = true)
    @Mapping(target = "actualizacion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Descuento toEntity(DescuentoDTO dto);
}
