package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.SecurityConfig;
import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.service.CustomUserDetailsService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PrototypeController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PrototypeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockBean
    protected in.tech_camp.protospace.service.PrototypeService prototypeService;

    @MockBean
    protected in.tech_camp.protospace.repository.PrototypeRepository prototypeRepository;

    @MockBean
    protected in.tech_camp.protospace.repository.UserRepository userRepository;

    protected Authentication authWithUserId(int userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        CustomUserDetail userDetails = new CustomUserDetail(userEntity);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    protected static PrototypeEntity prototypeEntity(int id, int userId) {
        PrototypeEntity e = new PrototypeEntity();
        e.setId(id);
        e.setTitle("タイトル");
        e.setCatchCopy("キャッチコピー");
        e.setConcept("コンセプト");
        e.setUserId(userId);
        e.setImageName("image.jpg");
        e.setImageType("image/jpeg");
        e.setImageData("image data".getBytes());
        return e;
    }

    @Nested
    class 正常系 {

        @Test
        void 認証済みでは新規作成フォームが表示される() throws Exception {
            mockMvc.perform(get("/prototypes/new").with(authentication(authWithUserId(1))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/new"))
                    .andExpect(model().attributeExists("prototypeForm"));
        }

        @Test
        void 未ログインでトップページが表示される() throws Exception {
            when(prototypeRepository.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/prototypes/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"));
        }

        @Test
        void ログイン状態でトップページが表示される() throws Exception {
            when(prototypeRepository.findAll()).thenReturn(Collections.emptyList());
            UserEntity userEntity = new UserEntity();
            userEntity.setName("Test User");
            userEntity.setEmail("test@example.com");
            CustomUserDetail userDetails = new CustomUserDetail(userEntity);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            mockMvc.perform(get("/prototypes/").with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"));
        }

        @Test
        void トップページにプロトタイプ一覧が表示される() throws Exception {
            List<PrototypeEntity> list = List.of(prototypeEntity(1, 10));
            when(prototypeRepository.findAll()).thenReturn(list);

            mockMvc.perform(get("/prototypes/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attribute("prototypes", list));
        }

        @Test
        void create_認証済みで投稿が成功しトップへリダイレクトされる() throws Exception {
            var form = PrototypeFormFactory.build();
            MockMultipartFile imageFile = new MockMultipartFile(
                    "imageFile",
                    "test.jpg",
                    "image/jpeg",
                    "dummy".getBytes());

            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(authentication(authWithUserId(1)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes/"));

            verify(prototypeService).createPrototype(any(PrototypeEntity.class));
        }

        @Test
        void create_投稿が成功しデータが正しくサービスに渡されて保存される() throws Exception {
            var form = PrototypeFormFactory.build(f -> {
                f.setTitle("テストタイトル");
                f.setCatchCopy("キャッチコピー");
                f.setConcept("コンセプト説明");
            });
            MockMultipartFile imageFile = new MockMultipartFile(
                    "imageFile",
                    "my-image.jpg",
                    "image/jpeg",
                    "image bytes".getBytes());
            form.setImageFile(imageFile);

            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(authentication(authWithUserId(42)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes/"));

            ArgumentCaptor<PrototypeEntity> entityCaptor = ArgumentCaptor.forClass(PrototypeEntity.class);
            verify(prototypeService).createPrototype(entityCaptor.capture());
            PrototypeEntity captured = entityCaptor.getValue();
            assertThat(captured.getTitle(), is("テストタイトル"));
            assertThat(captured.getCatchCopy(), is("キャッチコピー"));
            assertThat(captured.getConcept(), is("コンセプト説明"));
            assertThat(captured.getUserId(), is(42));
            assertThat(captured.getImageName(), is("my-image.jpg"));
            assertThat(captured.getImageType(), is("image/jpeg"));
            assertArrayEquals("image bytes".getBytes(), captured.getImageData());
        }

        @Test
        void 画像取得で存在するidを指定すると200と画像データが返る() throws Exception {
            PrototypeEntity entity = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(entity);

            mockMvc.perform(get("/prototypes/1/image"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "image/jpeg"))
                    .andExpect(content().bytes("image data".getBytes()));
        }
    }

    @Nested
    class 異常系 {

        @Test
        void 未認証では新規作成フォームへアクセスするとログインへリダイレクトされる() throws Exception {
            mockMvc.perform(get("/prototypes/new"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/users/login"));
        }

        @Test
        void create_未認証で投稿するとログインへリダイレクトされる() throws Exception {
            var form = PrototypeFormFactory.build();
            MockMultipartFile imageFile = new MockMultipartFile(
                    "imageFile", "test.jpg", "image/jpeg", "dummy".getBytes());

            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/users/login"));

            verify(prototypeService, never()).createPrototype(any(PrototypeEntity.class));
        }

        @Test
        void create_バリデーションエラー時は新規作成フォームに戻る() throws Exception {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "imageFile", "test.jpg", "image/jpeg", "dummy".getBytes());

            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", "")
                            .param("catchCopy", "キャッチ")
                            .param("concept", "コンセプト")
                            .with(authentication(authWithUserId(1)))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/new"))
                    .andExpect(model().attributeHasFieldErrors("prototypeForm", "title"));

            verify(prototypeService, never()).createPrototype(any(PrototypeEntity.class));
        }

        @Test
        void 画像取得で存在しないidを指定すると404が返る() throws Exception {
            when(prototypeRepository.findById(999)).thenReturn(null);

            mockMvc.perform(get("/prototypes/999/image"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void 画像取得で画像データがnullの場合は404が返る() throws Exception {
            PrototypeEntity entity = prototypeEntity(1, 10);
            entity.setImageData(null);
            when(prototypeRepository.findById(1)).thenReturn(entity);

            mockMvc.perform(get("/prototypes/1/image"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void 画像取得で例外発生時は500が返る() throws Exception {
            when(prototypeRepository.findById(1)).thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/prototypes/1/image"))
                    .andExpect(status().isInternalServerError());
        }
    }
}
