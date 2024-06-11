package ua.delsix.mapper;

import org.mapstruct.*;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.jpa.entity.Demonlist;

@Mapper(componentModel = "spring")
public interface DemonlistMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "demons", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDemonlistFromDto(DemonlistDto demonlistDto, @MappingTarget Demonlist demonlist);
}
