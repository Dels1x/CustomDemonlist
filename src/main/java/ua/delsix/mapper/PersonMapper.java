package ua.delsix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.delsix.dto.UserDto;
import ua.delsix.jpa.entity.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "pfpUrl", expression = "java(getPfpUrl(dto))"),
            @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "discordId", source="id")
    })
    Person toEntity(UserDto dto);

    default String getPfpUrl(UserDto dto) {
        return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", dto.getId(), dto.getAvatar());
    }
}
