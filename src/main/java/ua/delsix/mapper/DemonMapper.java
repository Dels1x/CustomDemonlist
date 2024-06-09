package ua.delsix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ua.delsix.dto.DemonDto;
import ua.delsix.jpa.entity.Demon;

@Mapper(componentModel = "spring")
public interface DemonMapper {
    void updateDemonFromDto(DemonDto demonDto, @MappingTarget Demon demon);
}
