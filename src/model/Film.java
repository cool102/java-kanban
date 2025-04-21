package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;

@Data
@AllArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private Date releaseDate;
    private Duration duration;
}