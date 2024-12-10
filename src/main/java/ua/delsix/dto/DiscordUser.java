package ua.delsix.dto;

import lombok.Data;

@Data
public class DiscordUser {
    private String id;
    private String username;
    private String avatar;
    private String email;
}
