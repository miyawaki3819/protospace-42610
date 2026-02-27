package in.tech_camp.protospace.entity;

import java.util.List;

import lombok.Data;

@Data
public class UserEntity {
    private Integer id;
    private String email;
    private String password;
    private String name;
    private String profile;
    private String occupation;
    private String position;
    private List<PrototypeEntity> prototypes;
}