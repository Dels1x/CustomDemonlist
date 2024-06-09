package ua.delsix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.jpa.entity.Demonlist;

@Mapper(componentModel = "spring")
public interface DemonlistMapper {
    void updateDemonlistFromDto(DemonlistDto demonlistDto, @MappingTarget Demonlist demonlist);
}
