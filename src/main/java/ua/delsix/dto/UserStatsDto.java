package ua.delsix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private int totalDemonlists;
    private int totalDemons;
    private int publicLists;
    private int totalLikes;
}
