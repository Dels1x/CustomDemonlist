package ua.delsix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.jpa.entity.Demonlist;

@Mapper(componentModel = "spring")
public interface DemonlistMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDemonlistFromDto(DemonlistDto demonlistDto, @MappingTarget Demonlist demonlist);
}
