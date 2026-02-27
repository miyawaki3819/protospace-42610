package in.tech_camp.protospace.factory;

import java.util.function.Consumer;

import org.springframework.mock.web.MockMultipartFile;

import in.tech_camp.protospace.form.PrototypeForm;
import net.datafaker.Faker;

/**
 * テスト用の PrototypeForm を生成するファクトリ。
 * 画像は実際のファイルではなく MockMultipartFile でダミーを設定する。
 * 基本的なテスト用データが1つのみ生成されるようにし、
 * build(Consumer) で各テストに必要な属性だけ上書きできるようにする。
 *
 * <p>単体テストにおける注意：画像を空にしてテストする場合は、
 * 「""」（空文字）ではなく {@code null} を使用すること。
 * 画像に対して "" を用いるとエラーが発生する。</p>
 */
public class PrototypeFormFactory {

    private static final Faker FAKER = new Faker();

    /**
     * デフォルトの有効な PrototypeForm を1件生成する。
     * 画像は MockMultipartFile のダミーが設定される。
     */
    public static PrototypeForm build() {
        return build(form -> {});
    }

    /**
     * デフォルトの有効な PrototypeForm を生成し、customizer で属性を上書きする。
     *
     * @param customizer 上書きする属性を指定する Consumer
     * @return 生成された PrototypeForm
     */
    public static PrototypeForm build(Consumer<PrototypeForm> customizer) {
        PrototypeForm form = createDefault();
        customizer.accept(form);
        return form;
    }

    private static PrototypeForm createDefault() {
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes());

        PrototypeForm form = new PrototypeForm();
        form.setTitle(FAKER.lorem().sentence(3));
        form.setCatchCopy(FAKER.lorem().sentence());
        form.setConcept(FAKER.lorem().paragraph());
        form.setImageFile(imageFile);
        return form;
    }
}
