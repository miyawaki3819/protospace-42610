package in.tech_camp.protospace.factory;

import in.tech_camp.protospace.form.UserForm;
import net.datafaker.Faker;

import java.util.function.Consumer;

/**
 * テスト用の UserForm を生成するファクトリ。
 * 基本的なテスト用データが1つのみ生成されるようにし、
 * build(Consumer) で各テストに必要な属性だけ上書きできるようにする。
 */
public class UserFormFactory {

    private static final Faker FAKER = new Faker();

    /**
     * デフォルトの有効な UserForm を1件生成する。
     */
    public static UserForm build() {
        return build(form -> {});
    }

    /**
     * デフォルトの有効な UserForm を生成し、customizer で属性を上書きする。
     *
     * @param customizer 上書きする属性を指定する Consumer
     * @return 生成された UserForm
     */
    public static UserForm build(Consumer<UserForm> customizer) {
        UserForm form = createDefault();
        customizer.accept(form);
        return form;
    }

    @SuppressWarnings("removal")
    private static UserForm createDefault() {
        String raw = FAKER.internet().password();
        String password = raw.length() >= 6 ? raw : raw + "Ab1";
        UserForm form = new UserForm();
        form.setEmail(FAKER.internet().emailAddress());
        form.setPassword(password);
        form.setPasswordConfirmation(password);
        form.setName(FAKER.name().fullName());
        form.setProfile(FAKER.lorem().paragraph());
        form.setOccupation(FAKER.company().name());
        form.setPosition(FAKER.job().title());
        return form;
    }
}
