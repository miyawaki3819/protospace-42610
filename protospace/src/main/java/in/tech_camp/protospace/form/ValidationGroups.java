package in.tech_camp.protospace.form;

/**
 * バリデーションの実行順序を指定するためのグループ。
 * カリキュラム「バリデーションの順位づけを指定しよう」に沿い、
 * 第1グループ（必須チェック）→ 第2グループ（形式・長さ）→ 第3グループ（相関チェック）の順で実行する。
 */
public final class ValidationGroups {

    private ValidationGroups() {}

    /** 第1グループ: 必須入力チェック（NotBlank 等） */
    public interface First {}

    /** 第2グループ: 形式・長さチェック（Size, Email 等） */
    public interface Second {}

    /** 第3グループ: 相関チェック（パスワード一致等） */
    public interface Third {}
}
