package ua.delsix.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleUserDto extends UserDto {
    private String sub;
    private String name;
    private String picture;
}
