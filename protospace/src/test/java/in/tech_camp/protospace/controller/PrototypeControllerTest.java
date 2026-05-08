package in.tech_camp.protospace.controller;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.protospace.SecurityConfig;
import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.service.CustomUserDetailsService;

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

    protected PrototypeForm form;
    protected MockMultipartFile imageFile;

    @BeforeEach
    void setUpForm() {
        form = PrototypeFormFactory.build();
        imageFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes());
    }

    protected Authentication authWithUserId(int userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        CustomUserDetail userDetails = new CustomUserDetail(userEntity);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    protected static PrototypeEntity prototypeEntity(int id, int userId) {
        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(id);
        prototype.setTitle("タイトル");
        prototype.setCatchCopy("キャッチコピー");
        prototype.setConcept("コンセプト");
        UserEntity user = new UserEntity();
        user.setId(userId);
        prototype.setUser(user);
        prototype.setImageName("image.jpg");
        prototype.setImageType("image/jpeg");
        prototype.setImageData("image data".getBytes());
        return prototype;
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

            mockMvc.perform(get("/prototypes"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"));
        }

        @Test
        void 未ログイン状態ではトップページにログインボタンが表示される() throws Exception {
            when(prototypeRepository.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/prototypes"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"))
                    .andExpect(content().string(containsString("ログイン")))
                    .andExpect(content().string(containsString("新規登録")))
                    .andExpect(content().string(not(containsString("ログアウト"))))
                    .andExpect(content().string(not(containsString("New Proto"))))
                    .andExpect(content().string(not(containsString("こんにちは"))));
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

            mockMvc.perform(get("/prototypes").with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"));
        }

        @Test
        void ログイン状態ではトップページにログアウトとNewProtoボタンが表示される() throws Exception {
            when(prototypeRepository.findAll()).thenReturn(Collections.emptyList());
            UserEntity userEntity = new UserEntity();
            userEntity.setName("Test User");
            userEntity.setEmail("test@example.com");
            CustomUserDetail userDetails = new CustomUserDetail(userEntity);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            mockMvc.perform(get("/prototypes").with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attributeExists("prototypes"))
                    .andExpect(content().string(containsString("ログアウト")))
                    .andExpect(content().string(containsString("New Proto")))
                    .andExpect(content().string(not(containsString("ログイン"))))
                    .andExpect(content().string(not(containsString("新規登録"))))
                    .andExpect(content().string(containsString("こんにちは")))
                    .andExpect(content().string(containsString("Test User")));
        }

        @Test
        void トップページにプロトタイプ一覧が表示される() throws Exception {
            List<PrototypeEntity> list = List.of(prototypeEntity(1, 10));
            when(prototypeRepository.findAll()).thenReturn(list);

            mockMvc.perform(get("/prototypes"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/index"))
                    .andExpect(model().attribute("prototypes", list));
        }

        @Test
        void プロトタイプ詳細ページが表示される() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/detail"))
                    .andExpect(model().attribute("prototype", prototype));
        }

        @Test
        void 投稿者のみ編集削除ボタンが表示される() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1").with(authentication(authWithUserId(10))))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("編集する")))
                    .andExpect(content().string(containsString("削除する")));
        }

        @Test
        void 投稿者以外のユーザーには編集削除ボタンが表示されない() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1").with(authentication(authWithUserId(99))))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("編集する"))))
                    .andExpect(content().string(not(containsString("削除する"))));
        }

        @Test
        void 未ログイン状態ではプロトタイプ詳細ページに編集削除ボタンが表示されない() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/detail"))
                    .andExpect(model().attribute("prototype", prototype))
                    .andExpect(content().string(not(containsString("編集する"))))
                    .andExpect(content().string(not(containsString("削除する"))));
        }

        @Test
        void 存在しないプロトタイプの詳細はトップへリダイレクトされる() throws Exception {
            when(prototypeRepository.findById(999)).thenReturn(null);

            mockMvc.perform(get("/prototypes/999"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));
        }

        @Test
        void create_認証済みで投稿が成功しトップへリダイレクトされる() throws Exception {
            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(authentication(authWithUserId(1)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));

            verify(prototypeService).createFromForm(any(PrototypeForm.class), any(Integer.class));
        }

        @Test
        void create_投稿が成功しデータが正しくサービスに渡されて保存される() throws Exception {
            form = PrototypeFormFactory.build(f -> {
                f.setTitle("テストタイトル");
                f.setCatchCopy("キャッチコピー");
                f.setConcept("コンセプト説明");
            });
            MockMultipartFile customImageFile = new MockMultipartFile(
                    "imageFile",
                    "my-image.jpg",
                    "image/jpeg",
                    "image bytes".getBytes());
            form.setImageFile(customImageFile);

            mockMvc.perform(multipart("/prototypes")
                            .file(customImageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(authentication(authWithUserId(42)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));

            ArgumentCaptor<PrototypeForm> formCaptor = ArgumentCaptor.forClass(PrototypeForm.class);
            ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(prototypeService).createFromForm(formCaptor.capture(), userIdCaptor.capture());
            PrototypeForm capturedForm = formCaptor.getValue();
            assertThat(capturedForm.getTitle(), is("テストタイトル"));
            assertThat(capturedForm.getCatchCopy(), is("キャッチコピー"));
            assertThat(capturedForm.getConcept(), is("コンセプト説明"));
            assertThat(userIdCaptor.getValue(), is(42));
            assertThat(capturedForm.getImageFile().getOriginalFilename(), is("my-image.jpg"));
            assertThat(capturedForm.getImageFile().getContentType(), is("image/jpeg"));
            assertArrayEquals("image bytes".getBytes(), capturedForm.getImageFile().getBytes());
        }

        @Test
        void 投稿者は編集ページへ遷移できる() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1/edit").with(authentication(authWithUserId(10))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/edit"))
                    .andExpect(model().attributeExists("prototypeForm"))
                    .andExpect(content().string(containsString("保存する")));
        }

        @Test
        void update_有効な入力で編集すると詳細ページへリダイレクトされる() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(post("/prototypes/1")
                            .param("title", "更新後タイトル")
                            .param("catchCopy", "更新後キャッチ")
                            .param("concept", "更新後コンセプト")
                            .with(authentication(authWithUserId(10)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes/1"));

            verify(prototypeService).updateFromForm(any(PrototypeEntity.class), any(PrototypeForm.class));
        }

        @Test
        void delete_投稿者が削除すると一覧へリダイレクトされレコードが削除される() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(post("/prototypes/1/delete")
                            .with(authentication(authWithUserId(10)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));

            verify(prototypeRepository).deleteById(1);
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
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/new"));
        }

        @Test
        void 未認証では編集ページへアクセスするとログインへリダイレクトされる() throws Exception {
            mockMvc.perform(get("/prototypes/1/edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/users/login"));
        }

        @Test
        void 投稿者以外は編集ページへアクセスするとトップへリダイレクトされる() throws Exception {
            PrototypeEntity prototype = prototypeEntity(1, 10);
            when(prototypeRepository.findById(1)).thenReturn(prototype);

            mockMvc.perform(get("/prototypes/1/edit").with(authentication(authWithUserId(99))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));
        }

        @Test
        void delete_存在しないプロトタイプは一覧へリダイレクトされる() throws Exception {
            when(prototypeRepository.findById(999)).thenReturn(null);

            mockMvc.perform(post("/prototypes/999/delete")
                            .with(authentication(authWithUserId(10)))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/prototypes"));

            verify(prototypeRepository, never()).deleteById(any());
        }

        @Test
        void update_バリデーションエラー時は編集ページに戻る() throws Exception {
            mockMvc.perform(post("/prototypes/1")
                            .param("title", "")
                            .param("catchCopy", "キャッチ")
                            .param("concept", "コンセプト")
                            .with(authentication(authWithUserId(10)))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("prototypes/edit"))
                    .andExpect(model().attributeHasFieldErrors("prototypeForm", "title"))
                    .andExpect(model().attributeExists("errorMessages"))
                    .andExpect(model().attribute("prototypeId", 1))
                    .andExpect(model().attributeExists("prototype"));

            verify(prototypeService, never()).updateFromForm(any(PrototypeEntity.class), any(PrototypeForm.class));
        }

        @Test
        void create_未認証で投稿するとログインへリダイレクトされる() throws Exception {
            mockMvc.perform(multipart("/prototypes")
                            .file(imageFile)
                            .param("title", form.getTitle())
                            .param("catchCopy", form.getCatchCopy())
                            .param("concept", form.getConcept())
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/users/login"));

            verify(prototypeService, never()).createFromForm(any(PrototypeForm.class), any(Integer.class));
        }

        @Test
        void create_バリデーションエラー時は新規作成フォームに戻る() throws Exception {
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

            verify(prototypeService, never()).createFromForm(any(PrototypeForm.class), any(Integer.class));
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
