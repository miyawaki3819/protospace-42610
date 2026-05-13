package in.tech_camp.protospace.controller;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.protospace.SecurityConfig;
import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.entity.CommentEntity;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.service.CustomUserDetailsService;

@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockBean
    protected in.tech_camp.protospace.repository.CommentRepository commentRepository;

    @MockBean
    protected in.tech_camp.protospace.repository.PrototypeRepository prototypeRepository;

    @MockBean
    protected in.tech_camp.protospace.repository.UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(commentRepository.findByPrototypeId(any())).thenReturn(Collections.emptyList());
    }

    private Authentication authWithUserId(int userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        CustomUserDetail userDetails = new CustomUserDetail(userEntity);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private PrototypeEntity prototypeEntity(int id, int userId) {
        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(id);
        UserEntity user = new UserEntity();
        user.setId(userId);
        prototype.setUser(user);
        return prototype;
    }

    @Nested
    class 正常系 {
        @Test
        void コメント投稿で詳細へリダイレクトされる() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(post("/prototypes/1/comments")
                            .param("text", "いいね")
                            .with(authentication(authWithUserId(5)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes/1"));

            ArgumentCaptor<CommentEntity> captor = ArgumentCaptor.forClass(CommentEntity.class);
            verify(commentRepository).insert(captor.capture());
            assertThat(captor.getValue().getText(), is("いいね"));
            assertThat(captor.getValue().getPrototypeId(), is(1));
            assertThat(captor.getValue().getUser().getId(), is(5));
        }
    }

    @Nested
    class 異常系 {
        @Test
        void コメントが空の場合は詳細ページに戻る() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(post("/prototypes/1/comments")
                            .param("text", "")
                            .with(authentication(authWithUserId(5)))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/detail"))
                    .andExpect(model().attributeHasFieldErrors("commentForm", "text"));

            verify(commentRepository, never()).insert(any(CommentEntity.class));
        }

        @Test
        void コメント投稿時プロトタイプが無い場合は一覧へリダイレクト() throws Exception {
            when(prototypeRepository.findById(999)).thenReturn(null);

            mockMvc.perform(post("/prototypes/999/comments")
                            .param("text", "test")
                            .with(authentication(authWithUserId(1)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));

            verify(commentRepository, never()).insert(any(CommentEntity.class));
        }

        @Test
        void 未認証でコメント投稿するとログインへリダイレクトされる() throws Exception {
            mockMvc.perform(post("/prototypes/1/comments")
                            .param("text", "test")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/users/login"));

            verify(commentRepository, never()).insert(any(CommentEntity.class));
        }
    }
}
