package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.SecurityService;
import in.tech_camp.protospace.service.UserService;
import in.tech_camp.protospace.factory.UserFormFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserController userController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
    }

    @Nested
    class 正常系 {

        @Test
        void signUp_新規登録ページが表示される() {
            String result = userController.signUpForm(model);
            assertThat(result, is("users/signUp"));
            assertThat(model.asMap(), hasKey("userForm"));
        }

        @Test
        void create_ユーザー登録が成功し自動ログインされてトップページにリダイレクトされる() {
            UserForm userForm = UserFormFactory.build();
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            when(userRepository.existsByEmail(userForm.getEmail())).thenReturn(false);

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("redirect:/"));

            verify(userRepository, times(1)).existsByEmail(userForm.getEmail());
            verify(userService, times(1)).createUserWithEncryptedPassword(any(UserEntity.class));
            verify(securityService, times(1)).autoLogin(userForm.getEmail());
        }

        @Test
        void login_ログインページが表示される() {
            String result = userController.showLogin();
            assertThat(result, is("users/login"));
        }
    }

    @Nested
    class 異常系 {

        @Test
        void create_パスワードとパスワード再入力が一致しない場合() {
            UserForm userForm = UserFormFactory.build(f -> f.setPasswordConfirmation("different_password"));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.reject("passwordConfirmationValid");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_メールアドレス形式が不正な場合() {
            UserForm userForm = UserFormFactory.build(f -> f.setEmail("invalid-email"));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("email", "null", "Invalid email format");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_パスワードが規定文字数未満の場合() {
            UserForm userForm = UserFormFactory.build(f -> {
                f.setPassword("12345");
                f.setPasswordConfirmation("12345");
            });
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("password", "null", "Password too short");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_重複したemailを登録しようとした場合バリデーションエラーが発生する() {
            UserForm userForm = UserFormFactory.build(f -> f.setEmail("existing@example.com"));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userRepository, times(1)).existsByEmail("existing@example.com");
            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
            verify(securityService, never()).autoLogin(anyString());
        }

        @Test
        void create_必須項目が空の場合() {
            UserForm userForm = UserFormFactory.build(f -> {
                f.setName("");
                f.setProfile("");
                f.setOccupation("");
                f.setPosition("");
            });
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("name", "null", "Name is required");
            bindingResult.rejectValue("profile", "null", "Profile is required");
            bindingResult.rejectValue("occupation", "null", "Occupation is required");
            bindingResult.rejectValue("position", "null", "Position is required");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_ユーザーの新規登録にはプロフィールが必須であること() {
            UserForm userForm = UserFormFactory.build(f -> f.setProfile(""));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("profile", "null", "Profile is required");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_ユーザーの新規登録には所属が必須であること() {
            UserForm userForm = UserFormFactory.build(f -> f.setOccupation(""));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("occupation", "null", "Occupation is required");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }

        @Test
        void create_ユーザーの新規登録には役職が必須であること() {
            UserForm userForm = UserFormFactory.build(f -> f.setPosition(""));
            BindingResult bindingResult = new BeanPropertyBindingResult(userForm, "userForm");
            bindingResult.rejectValue("position", "null", "Position is required");

            String result = userController.createUser(userForm, bindingResult, model);
            assertThat(result, is("users/signUp"));

            verify(userService, never()).createUserWithEncryptedPassword(any(UserEntity.class));
        }
    }
}
