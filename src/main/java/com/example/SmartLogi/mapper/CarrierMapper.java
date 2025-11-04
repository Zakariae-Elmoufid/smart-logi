package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.CarrierRequestDTO;
import com.example.SmartLogi.dto.CarrierResponseDTO;
import com.example.SmartLogi.entities.Carrier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
     Carrier toEntity(CarrierRequestDTO dto);
    CarrierResponseDTO toDTO(Carrier carrier);
}
