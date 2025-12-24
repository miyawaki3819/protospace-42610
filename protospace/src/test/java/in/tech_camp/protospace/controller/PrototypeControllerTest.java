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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PrototypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class PrototypeControllerTest {

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
    void index_正常系_未ログイン状態でトップページが表示される() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeDoesNotExist("user"));

        verify(userMapper, never()).findById(anyLong());
    }

    @Test
    void index_正常系_ログイン状態でトップページが表示されユーザー情報が含まれる() throws Exception {
        when(userMapper.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/").sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", testUser));

        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void index_正常系_セッションにuserIdがあるが該当ユーザーが存在しない場合でもエラーにならない() throws Exception {
        when(userMapper.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/").sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeDoesNotExist("user"));

        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void newPrototype_異常系_未ログイン状態でログイン必須機能にアクセスした場合ログインページへリダイレクトされる() throws Exception {
        mockMvc.perform(get("/prototypes/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }
}

