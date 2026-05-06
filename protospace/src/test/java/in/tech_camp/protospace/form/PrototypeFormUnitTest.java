package in.tech_camp.protospace.form;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.validation.PrototypeCreateValidation;
import in.tech_camp.protospace.validation.PrototypeUpdateValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class PrototypeFormUnitTest {

    private Validator validator;
    protected PrototypeForm form;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = PrototypeFormFactory.build();
    }

    @Nested
    class 正常系 {

        @Test
        void 全項目が入力されていればバリデーションを通過する() {
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(0, violations.size());
        }

        @Test
        void 編集時は画像が未選択でもバリデーションを通過する() {
            form.setImageFile(null);
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeUpdateValidation.Order.class);
            assertEquals(0, violations.size());
        }
    }

    @Nested
    class 異常系 {

        @Test
        void titleが空の場合バリデーションエラーが発生する() {
            form.setTitle("");
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("プロトタイプの名称を入力してください", violations.iterator().next().getMessage());
        }

        @Test
        void catchCopyが空の場合バリデーションエラーが発生する() {
            form.setCatchCopy("");
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("キャッチコピーを入力してください", violations.iterator().next().getMessage());
        }

        @Test
        void conceptが空の場合バリデーションエラーが発生する() {
            form.setConcept("");
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("コンセプトを入力してください", violations.iterator().next().getMessage());
        }

        @Test
        void imageFileがnullの場合バリデーションエラーが発生する() {
            form.setImageFile(null);
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("プロトタイプの画像を選択してください", violations.iterator().next().getMessage());
        }

        @Test
        void 画像のContentTypeがimageで始まらない場合バリデーションエラーが発生する() {
            var invalidFile = new MockMultipartFile("imageFile", "test.txt", "text/plain", "dummy".getBytes());
            form.setImageFile(invalidFile);
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeCreateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("画像ファイルを選択してください", violations.iterator().next().getMessage());
        }

        @Test
        void 編集時に不正なContentTypeを指定するとバリデーションエラーが発生する() {
            var invalidFile = new MockMultipartFile("imageFile", "test.txt", "text/plain", "dummy".getBytes());
            form.setImageFile(invalidFile);
            Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, PrototypeUpdateValidation.Order.class);
            assertEquals(1, violations.size());
            assertEquals("画像ファイルを選択してください", violations.iterator().next().getMessage());
        }
    }
}
