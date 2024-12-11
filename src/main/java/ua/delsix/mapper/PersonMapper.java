package ua.delsix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.jpa.entity.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "pfpUrl", expression = "java(getPfpUrl(dto))"),
            @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "accessToken", ignore = true),
            @Mapping(target = "refreshToken", ignore = true),
            @Mapping(target = "tokenExpiry", ignore = true),
            @Mapping(target = "id", ignore = true) // Ignore ID as it is generated
    })
    Person toEntity(DiscordUserDto dto);

    default String getPfpUrl(DiscordUserDto dto) {
        return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", dto.getId(), dto.getAvatar());
    }
}
