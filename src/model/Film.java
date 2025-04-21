package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private Date releaseDate;
    private Duration duration;
}