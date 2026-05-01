package in.tech_camp.protospace.form;

import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace.validation.CreateValidationGroup1;
import in.tech_camp.protospace.validation.CreateValidationGroup2;
import in.tech_camp.protospace.validation.UpdateValidationGroup1;
import in.tech_camp.protospace.validation.UpdateValidationGroup2;
import in.tech_camp.protospace.validation.ValidImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrototypeForm {

    @NotBlank(message = "プロトタイプの名称を入力してください", groups = { CreateValidationGroup1.class,
            UpdateValidationGroup1.class })
    private String title;

    @NotBlank(message = "キャッチコピーを入力してください", groups = { CreateValidationGroup1.class,
            UpdateValidationGroup1.class })
    private String catchCopy;

    @NotBlank(message = "コンセプトを入力してください", groups = { CreateValidationGroup1.class,
            UpdateValidationGroup1.class })
    private String concept;

    @NotNull(message = "プロトタイプの画像を選択してください", groups = { CreateValidationGroup1.class })
    @ValidImage(message = "画像ファイルが無効です", groups = { CreateValidationGroup2.class, UpdateValidationGroup2.class })
    private MultipartFile imageFile;
}