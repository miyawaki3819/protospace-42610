package in.tech_camp.protospace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.factory.PrototypeFormFactory;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.repository.PrototypeRepository;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.PrototypeService;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDatabaseConfiguration.class)
class PrototypeCreateIntegrationTest {

    @Autowired
    private PrototypeService prototypeService;

    @Autowired
    private PrototypeRepository prototypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Nested
    class 正常系 {

        @Test
        void 実際のユーザーとして投稿でき外部キーが正しく設定される() throws Exception {
            UserEntity user = insertTestUser("prototype-test@example.com");

            int countBefore = prototypeRepository.findAll().size();

            PrototypeForm form = PrototypeFormFactory.build();
            PrototypeEntity entity = form.toEntity(user.getId());
            prototypeService.createPrototype(entity);

            int countAfter = prototypeRepository.findAll().size();
            assertEquals(countBefore + 1, countAfter, "投稿後にプロトタイプの件数が1件増えること");
        }
    }

    @Nested
    class 異常系 {
        // 投稿の異常系はコントローラテストでカバー
    }

    private UserEntity insertTestUser(String email) {
        var user = new UserEntity();
        user.setName("テストユーザー");
        user.setEmail(email);
        user.setPassword("password123");
        user.setProfile("プロフィール");
        user.setOccupation("所属");
        user.setPosition("役職");
        userRepository.insert(user);
        return userRepository.findByEmail(email);
    }
}
