package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.SecurityConfig;
import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.service.CustomUserDetailsService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PrototypeController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PrototypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    class 正常系 {

        @Test
        void index_未ログインでトップページが表示される() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        void index_ログイン状態でトップページが表示される() throws Exception {
            UserEntity userEntity = new UserEntity();
            userEntity.setName("Test User");
            userEntity.setEmail("test@example.com");
            CustomUserDetail userDetails = new CustomUserDetail(userEntity);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            mockMvc.perform(get("/").with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }
    }

    @Nested
    class 異常系 {
        // index は GET / のみで認証不要のため、現時点で異常系のテストはなし
    }
}
