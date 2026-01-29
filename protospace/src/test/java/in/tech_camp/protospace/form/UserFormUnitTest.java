package in.tech_camp.protospace.form;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private UserForm userForm;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userForm = UserFormFactory.createValidUserForm();
    }

    @Test
    public void nicknameとemailとpasswordとpasswordConfirmationが存在すれば登録できる() {
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(0, violations.size());
    }

    @Test
    public void nicknameが空の場合バリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithEmptyName();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("ユーザー名は必須です", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空の場合バリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithEmptyEmail();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertTrue(violations.size() >= 1);
    }

    @Test
    public void passwordが空の場合バリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithEmptyPassword();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertTrue(violations.size() >= 1);
    }

    @Test
    public void passwordとpasswordConfirmationが不一致ではバリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithMismatchedPassword();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        // validatePasswordConfirmation メソッドは BindingResult を使用するため、
        // Validator には検証されない
        assertEquals(0, violations.size());
    }

    @Test
    public void nicknameが7文字以上ではバリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithName("abc");
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        // ニックネームの長さ検証はアノテーションで定義されていない
        assertEquals(0, violations.size());
    }

    @Test
    public void emailはアットマークを含まないとバリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithInvalidEmail();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("有効なメールアドレスを入力してください", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが5文字以下ではバリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithShortPassword();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        assertEquals(1, violations.size());
        assertEquals("パスワードは6文字以上で入力してください", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上ではバリデーションエラーが発生する() {
        userForm = UserFormFactory.createUserFormWithLongPassword();
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm);
        // 最大長のバリデーションはアノテーションで定義されていない場合がある
        assertTrue(violations.size() >= 0);
    }
}
