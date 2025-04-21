package model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private Date birthdate;

    public User() {
    }
}