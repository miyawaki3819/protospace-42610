package in.tech_camp.protospace.form;

import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace.validation.PrototypeCreateValidation;
import in.tech_camp.protospace.validation.PrototypeUpdateValidation;
import in.tech_camp.protospace.validation.ValidImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrototypeForm {

    @NotBlank(message = "プロトタイプの名称を入力してください", groups = { PrototypeCreateValidation.Group1.class,
            PrototypeUpdateValidation.Group1.class })
    private String title;

    @NotBlank(message = "キャッチコピーを入力してください", groups = { PrototypeCreateValidation.Group1.class,
            PrototypeUpdateValidation.Group1.class })
    private String catchCopy;

    @NotBlank(message = "コンセプトを入力してください", groups = { PrototypeCreateValidation.Group1.class,
            PrototypeUpdateValidation.Group1.class })
    private String concept;

    @NotNull(message = "プロトタイプの画像を選択してください", groups = { PrototypeCreateValidation.Group1.class })
    @ValidImage(message = "画像ファイルが無効です", groups = { PrototypeCreateValidation.Group2.class,
            PrototypeUpdateValidation.Group2.class })
    private MultipartFile imageFile;
}