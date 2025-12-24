package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.entity.User;
import in.tech_camp.protospace.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setEncryptedPassword("encrypted_dummy_password");
        testUser.setName("テストユーザー");
        testUser.setProfile("テストプロフィール");
        testUser.setOccupation("テスト所属");
        testUser.setPosition("テスト役職");
    }

    @Test
    void signUp_正常系_新規登録ページが表示される() throws Exception {
        mockMvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"))
                .andExpect(model().attributeExists("userForm"));
    }

    @Test
    void create_正常系_ユーザー登録が成功しトップページにリダイレクトされる() throws Exception {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userMapper).insert(any(User.class));

        mockMvc.perform(post("/user")
                        .param("email", "test@example.com")
                        .param("password", "encrypted_dummy_password")
                        .param("passwordConfirmation", "encrypted_dummy_password")
                        .param("name", "テストユーザー")
                        .param("profile", "テストプロフィール")
                        .param("occupation", "テスト所属")
                        .param("position", "テスト役職"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void create_異常系_パスワードとパスワード再入力が一致しない場合() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "test@example.com")
                        .param("password", "encrypted_dummy_password")
                        .param("passwordConfirmation", "different_password")
                        .param("name", "テストユーザー")
                        .param("profile", "テストプロフィール")
                        .param("occupation", "テスト所属")
                        .param("position", "テスト役職"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void create_異常系_必須項目が空の場合() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "")
                        .param("password", "")
                        .param("passwordConfirmation", "")
                        .param("name", "")
                        .param("profile", "")
                        .param("occupation", "")
                        .param("position", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void login_正常系_ログインページが表示される() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    void login_正常系_ログインが成功しトップページにリダイレクトされる() throws Exception {
        when(userMapper.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/user/login")
                        .param("email", "test@example.com")
                        .param("password", "encrypted_dummy_password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    void login_異常系_ユーザーが存在しない場合() throws Exception {
        when(userMapper.findByEmail("nonexistent@example.com")).thenReturn(null);

        mockMvc.perform(post("/user/login")
                        .param("email", "nonexistent@example.com")
                        .param("password", "encrypted_dummy_password"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        verify(userMapper, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void login_異常系_パスワードが一致しない場合() throws Exception {
        when(userMapper.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/user/login")
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    void login_異常系_必須項目が空の場合() throws Exception {
        mockMvc.perform(post("/user/login")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        verify(userMapper, never()).findByEmail(anyString());
    }

    @Test
    void logout_正常系_ログアウトが成功しトップページにリダイレクトされる() throws Exception {
        mockMvc.perform(get("/user/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_異常系_ログイン中にログインページへアクセスした場合トップページへリダイレクトされる() throws Exception {
        mockMvc.perform(get("/user/login").sessionAttr("userId", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void create_異常系_メールアドレス形式が不正な場合() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "invalid-email")
                        .param("password", "encrypted_dummy_password")
                        .param("passwordConfirmation", "encrypted_dummy_password")
                        .param("name", "テストユーザー")
                        .param("profile", "テストプロフィール")
                        .param("occupation", "テスト所属")
                        .param("position", "テスト役職"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void create_異常系_パスワードが規定文字数未満の場合() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "test@example.com")
                        .param("password", "12345")
                        .param("passwordConfirmation", "12345")
                        .param("name", "テストユーザー")
                        .param("profile", "テストプロフィール")
                        .param("occupation", "テスト所属")
                        .param("position", "テスト役職"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void login_異常系_メールアドレス形式が不正な場合() throws Exception {
        mockMvc.perform(post("/user/login")
                        .param("email", "invalid-email")
                        .param("password", "encrypted_dummy_password"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        verify(userMapper, never()).findByEmail(anyString());
    }

    @Test
    void detail_異常系_未ログインでユーザー情報ページにアクセスした場合ログインページへリダイレクトされる() throws Exception {
        mockMvc.perform(get("/user/detail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void create_異常系_重複したemailを登録しようとした場合バリデーションエラーが発生する() throws Exception {
        // 既存のユーザーが存在する場合をシミュレート
        when(userMapper.findByEmail("existing@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/user")
                        .param("email", "existing@example.com")
                        .param("password", "encrypted_dummy_password")
                        .param("passwordConfirmation", "encrypted_dummy_password")
                        .param("name", "テストユーザー")
                        .param("profile", "テストプロフィール")
                        .param("occupation", "テスト所属")
                        .param("position", "テスト役職"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        verify(userMapper, times(1)).findByEmail("existing@example.com");
        verify(userMapper, never()).insert(any(User.class));
    }
}

