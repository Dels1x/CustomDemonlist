package ua.delsix.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DiscordUserDto extends UserDto {
    private String id;
    private String username;
    private String avatar;
}
