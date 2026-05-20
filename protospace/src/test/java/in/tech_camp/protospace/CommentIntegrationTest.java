package in.tech_camp.protospace;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.tech_camp.protospace.entity.CommentEntity;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.factory.CommentFormFactory;
import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.factory.UserFormFactory;
import in.tech_camp.protospace.form.CommentForm;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.CommentRepository;
import in.tech_camp.protospace.repository.PrototypeRepository;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.PrototypeService;
import in.tech_camp.protospace.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = ProtospaceApplication.class)
@AutoConfigureMockMvc
public class CommentIntegrationTest {
  private UserForm userForm;
  private UserEntity userEntity;

  private PrototypeForm prototypeForm;
  private PrototypeEntity prototypeEntity;

  private CommentForm commentForm;

  private int initialCount;
  private int afterCount;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PrototypeService prototypeService;

  @Autowired
  private PrototypeRepository prototypeRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() throws Exception {
    userForm = UserFormFactory.build(f -> f.setName("CommentIntegrationUser"));
    userService.createUser(userForm);
    userEntity = userRepository.findByEmail(userForm.getEmail());

    prototypeEntity = new PrototypeEntity();
    prototypeEntity.setTitle("CommentIntegrationPrototypeTitle");
    prototypeEntity.setCatchCopy("CommentIntegrationCatchCopy");
    prototypeEntity.setConcept("CommentIntegrationConcept");
    prototypeEntity.setImageName("test.jpg");
    prototypeEntity.setImageType("image/jpeg");
    prototypeEntity.setImageData("dummy".getBytes());
    UserEntity user = new UserEntity();
    user.setId(userEntity.getId());
    prototypeEntity.setUser(user);
    prototypeRepository.insert(prototypeEntity);

    commentForm = CommentFormFactory.createComment();
    commentForm.setText("CommentIntegrationCommentBody");
  }

  @Test
  public void 未ログインではプロトタイプ詳細にコメント投稿フォームが表示されない() throws Exception {
    MvcResult result = mockMvc.perform(get("/prototypes/{id}", prototypeEntity.getId()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(prototypeEntity.getTitle())))
        .andReturn();
    String content = result.getResponse().getContentAsString();
    System.out.println("Content: " + content);
    assert !content.contains("送信する");
    assert !content.contains("id=\"comment_text\"");
  }

  @Test
  public void ログインしたユーザーはツイート詳細ページでコメント投稿できる() throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
            .userParameter("email")
            .user(userForm.getEmail())
            .password(userForm.getPassword()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/prototypes"))
        .andReturn();

    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
    assertNotNull(session);

    UserEntity userFromDb = userRepository.findByEmail(userForm.getEmail());

    // ツイート詳細ページに遷移する
    mockMvc.perform(get("/prototypes/{id}", prototypeEntity.getId()).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(prototypeEntity.getTitle())))
        .andExpect(content().string(containsString("id=\"comment_text\"")))
        .andExpect(content().string(containsString("送信する")));

    List<CommentEntity> commentsListBeforePost = commentRepository.findByPrototypeId(prototypeEntity.getId());
    initialCount = commentsListBeforePost.size();

    // フォームに情報を入力し投稿する
    mockMvc.perform(post("/prototypes/{id}/comments", prototypeEntity.getId()).session(session)
            .param("text", commentForm.getText())
            .with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/prototypes/" + prototypeEntity.getId()));

    // コメントを送信すると、Commentモデルのカウントが1上がる
    List<CommentEntity> commentsListAfterPost = commentRepository.findByPrototypeId(prototypeEntity.getId());
    afterCount = commentsListAfterPost.size();
    assertEquals(initialCount + 1, afterCount);

    CommentEntity lastComment = commentsListAfterPost.get(commentsListAfterPost.size() - 1);
    assertEquals(userFromDb.getId(), lastComment.getUser().getId());
    assertEquals(prototypeEntity.getId(), lastComment.getPrototype().getId());
    assertEquals(commentForm.getText().trim(), lastComment.getText());
    assertEquals(userFromDb.getName(), lastComment.getUser().getName());

    // 詳細ページに再度アクセスして、コメント内容を確認
    mockMvc.perform(get("/prototypes/{id}", prototypeEntity.getId()).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(commentForm.getText())))
        .andExpect(content().string(containsString(userFromDb.getName())));
  }
}
