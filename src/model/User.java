package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private Date birthdate;
}