package ua.delsix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiscordUserDto {
    private String id;
    private String username;
    private String avatar;
    private String email;
}
