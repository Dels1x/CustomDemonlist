package ua.delsix.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link ua.delsix.jpa.entity.Demon}
 */
@Data
public class DemonDto implements Serializable {
    private String name;
    private String author;
    private Integer attemptsCount;
    private Integer enjoymentRating;
    private String difficulty;
    private LocalDate dateOfCompletion;
    private String gddpDifficulty;
    private String nlwTier;
    private String gddlTier;
    private Integer aredlPlacement;
    private Integer placement;
    private Integer initialPlacement;
}
