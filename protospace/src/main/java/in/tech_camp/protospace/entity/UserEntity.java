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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<PrototypeEntity> getPrototypes() {
        return prototypes;
    }

    public void setPrototypes(List<PrototypeEntity> prototypes) {
        this.prototypes = prototypes;
    }
}