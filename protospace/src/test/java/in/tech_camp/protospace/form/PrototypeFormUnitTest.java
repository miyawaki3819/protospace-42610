package in.tech_camp.protospace.form;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.validation.ValidationPriority1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class PrototypeFormUnitTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 全項目が入力されていればバリデーションを通過する() {
        var form = PrototypeFormFactory.build();
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(0, violations.size());
    }

    @Test
    void titleが空の場合バリデーションエラーが発生する() {
        var form = PrototypeFormFactory.build(f -> f.setTitle(""));
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(1, violations.size());
        assertEquals("プロトタイプの名称を入力してください", violations.iterator().next().getMessage());
    }

    @Test
    void catchCopyが空の場合バリデーションエラーが発生する() {
        var form = PrototypeFormFactory.build(f -> f.setCatchCopy(""));
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(1, violations.size());
        assertEquals("キャッチコピーを入力してください", violations.iterator().next().getMessage());
    }

    @Test
    void conceptが空の場合バリデーションエラーが発生する() {
        var form = PrototypeFormFactory.build(f -> f.setConcept(""));
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(1, violations.size());
        assertEquals("コンセプトを入力してください", violations.iterator().next().getMessage());
    }

    @Test
    void imageFileがnullの場合バリデーションエラーが発生する() {
        var form = PrototypeFormFactory.build(f -> f.setImageFile(null));
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(1, violations.size());
        assertEquals("ImageFile can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    void 画像のContentTypeがimageで始まらない場合バリデーションエラーが発生する() {
        var invalidFile = new MockMultipartFile("imageFile", "test.txt", "text/plain", "dummy".getBytes());
        var form = PrototypeFormFactory.build(f -> f.setImageFile(invalidFile));
        Set<ConstraintViolation<PrototypeForm>> violations = validator.validate(form, ValidationPriority1.class);
        assertEquals(1, violations.size());
        assertEquals("画像ファイルを選択してください", violations.iterator().next().getMessage());
    }
}
