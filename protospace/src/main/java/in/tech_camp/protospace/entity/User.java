package in.tech_camp.protospace.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String encryptedPassword;
    private String name;
    private String profile;
    private String occupation;
    private String position;
}

