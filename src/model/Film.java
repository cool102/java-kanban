package model;

import lombok.Data;

import java.time.Duration;
import java.util.Date;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private Date releaseDate;
    private Duration duration;
}