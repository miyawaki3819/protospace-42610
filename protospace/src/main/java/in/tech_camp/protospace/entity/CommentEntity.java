package in.tech_camp.protospace.entity;

import lombok.Data;

@Data
public class CommentEntity {
    private Integer id;
    private String text;
    private UserEntity user;
    private PrototypeEntity prototype;
    private Integer prototypeId;
}
