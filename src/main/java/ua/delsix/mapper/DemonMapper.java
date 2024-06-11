package ua.delsix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.delsix.dto.DemonDto;
import ua.delsix.jpa.entity.Demon;

@Mapper(componentModel = "spring")
public interface DemonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "demonlist", ignore = true)
    void updateDemonFromDto(DemonDto demonDto, @MappingTarget Demon demon);
}
