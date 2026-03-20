package in.tech_camp.protospace.form;

import org.springframework.web.multipart.MultipartFile;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatchCopy() {
        return catchCopy;
    }

    public void setCatchCopy(String catchCopy) {
        this.catchCopy = catchCopy;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}