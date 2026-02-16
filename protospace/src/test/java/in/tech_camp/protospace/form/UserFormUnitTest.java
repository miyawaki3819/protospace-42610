package in.tech_camp.protospace.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace.factory.UserFormFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class UserFormUnitTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nicknameとemailとpasswordとpasswordConfirmationが存在すれば登録できる() {
        var userForm = UserFormFactory.build();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(0, violations.size());
    }

    @Test
    public void プロフィールと所属と役職が入力されていればバリデーションを通過する() {
        var userForm = UserFormFactory.build(f -> {
            f.setProfile("エンジニアです。よろしくお願いします。");
            f.setOccupation("開発部");
            f.setPosition("シニアエンジニア");
        });
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(0, violations.size());
    }

    @Test
    public void nicknameが空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setName(""));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Name is required", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setEmail(""));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> {
            f.setPassword("");
            f.setPasswordConfirmation("");
        });
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        // password: @NotBlank + @Size(min=6), passwordConfirmation: @NotBlank
        assertEquals(3, violations.size());
    }

    @Test
    public void passwordとpasswordConfirmationが不一致ではバリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setPasswordConfirmation("different_password"));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Passwords do not match", violations.iterator().next().getMessage());
    }

    @Test
    public void emailはアットマークを含まないとバリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setEmail("invalid-email"));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Please enter a valid email address", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが5文字以下ではバリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> {
            f.setPassword("12345");
            f.setPasswordConfirmation("12345");
        });
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 6 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上ではバリデーションエラーが発生する() {
        String longPassword = "a".repeat(130);
        var userForm = UserFormFactory.build(f -> {
            f.setPassword(longPassword);
            f.setPasswordConfirmation(longPassword);
        });
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(0, violations.size());
    }

    @Test
    public void プロフィールが空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setProfile(""));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Profile is required", violations.iterator().next().getMessage());
    }

    @Test
    public void 所属が空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setOccupation(""));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Occupation is required", violations.iterator().next().getMessage());
    }

    @Test
    public void 役職が空の場合バリデーションエラーが発生する() {
        var userForm = UserFormFactory.build(f -> f.setPosition(""));
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("Position is required", violations.iterator().next().getMessage());
    }
}
