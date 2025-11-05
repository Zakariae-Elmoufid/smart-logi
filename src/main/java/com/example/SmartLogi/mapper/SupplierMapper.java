package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.SupplierRequestDTO;
import com.example.SmartLogi.dto.SupplierResponseDTO;
import com.example.SmartLogi.entities.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDTO dto);
    SupplierResponseDTO toDto(Supplier supplier);
    void updateEntityFromDto(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}
