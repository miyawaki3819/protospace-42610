package in.tech_camp.protospace.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
class CommentFormUnitTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    class 正常系 {

        @Test
        void textが入力されていればバリデーションを通過する() {
            CommentForm form = new CommentForm();
            form.setText("This is a valid comment.");

            Set<ConstraintViolation<CommentForm>> violations = validator.validate(form);
            assertEquals(0, violations.size());
        }
    }

    @Nested
    class 異常系 {

        @Test
        void textが空の場合はバリデーションエラーになる() {
            CommentForm form = new CommentForm();
            form.setText("");

            Set<ConstraintViolation<CommentForm>> violations = validator.validate(form);
            assertEquals(1, violations.size());
            assertEquals("text", violations.iterator().next().getPropertyPath().toString());
        }
    }
}
