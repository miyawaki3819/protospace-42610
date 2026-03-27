package in.tech_camp.protospace.entity;

import lombok.Data;

@Data
public class PrototypeEntity {
    private Integer id;
    private String title;
    private String catchCopy;
    private String concept;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    private UserEntity user;
}