package in.tech_camp.protospace.form;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.validation.ValidImage;
import in.tech_camp.protospace.validation.ValidationPriority1;
import in.tech_camp.protospace.validation.ValidationPriority2;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrototypeForm {

    @NotBlank(message = "プロトタイプの名称を入力してください", groups = { ValidationPriority1.class })
    private String title;

    @NotBlank(message = "キャッチコピーを入力してください", groups = { ValidationPriority1.class })
    private String catchCopy;

    @NotBlank(message = "コンセプトを入力してください", groups = { ValidationPriority1.class })
    private String concept;

    @NotNull(message = "ImageFile can't be blank", groups = {ValidationPriority1.class})
    @ValidImage(message = "Invalid image file", groups = {ValidationPriority1.class, ValidationPriority2.class})
    private MultipartFile imageFile;

    public PrototypeEntity toEntity(Integer userId) throws IOException {
        PrototypeEntity entity = new PrototypeEntity();
        entity.setTitle(title);
        entity.setCatchCopy(catchCopy);
        entity.setConcept(concept);
        entity.setUserId(userId);
        if (imageFile != null && !imageFile.isEmpty()) {
            entity.setImageName(imageFile.getOriginalFilename());
            entity.setImageType(imageFile.getContentType());
            entity.setImageData(imageFile.getBytes());
        }
        return entity;
    }
}