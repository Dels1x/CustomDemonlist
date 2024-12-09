package ua.delsix.mapper;

import org.mapstruct.*;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.jpa.entity.Demonlist;

@Mapper(componentModel = "spring")
public interface DemonlistMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDemonlistFromDto(DemonlistDto demonlistDto, @MappingTarget Demonlist demonlist);
}
