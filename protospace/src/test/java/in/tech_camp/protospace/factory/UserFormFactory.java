package in.tech_camp.protospace.factory;

import in.tech_camp.protospace.form.UserForm;

public class UserFormFactory {
    
    /**
     * デフォルトの有効な UserForm インスタンスを生成します
     * 
     * @return 有効なデータが設定された UserForm
     */
    public static UserForm createValidUserForm() {
        UserForm userForm = new UserForm();
        userForm.setEmail("test@example.com");
        userForm.setPassword("password123");
        userForm.setPasswordConfirmation("password123");
        userForm.setName("テストユーザー");
        userForm.setProfile("テストプロフィール");
        userForm.setOccupation("テスト所属");
        userForm.setPosition("テスト役職");
        return userForm;
    }

    /**
     * メールアドレスが空の UserForm インスタンスを生成します
     * 
     * @return メールアドレスが空の UserForm
     */
    public static UserForm createUserFormWithEmptyEmail() {
        UserForm userForm = createValidUserForm();
        userForm.setEmail("");
        return userForm;
    }

    /**
     * パスワードが空の UserForm インスタンスを生成します
     * 
     * @return パスワードが空の UserForm
     */
    public static UserForm createUserFormWithEmptyPassword() {
        UserForm userForm = createValidUserForm();
        userForm.setPassword("");
        return userForm;
    }

    /**
     * ユーザー名が空の UserForm インスタンスを生成します
     * 
     * @return ユーザー名が空の UserForm
     */
    public static UserForm createUserFormWithEmptyName() {
        UserForm userForm = createValidUserForm();
        userForm.setName("");
        return userForm;
    }

    /**
     * メールアドレス形式が不正な UserForm インスタンスを生成します
     * 
     * @return メールアドレス形式が不正な UserForm
     */
    public static UserForm createUserFormWithInvalidEmail() {
        UserForm userForm = createValidUserForm();
        userForm.setEmail("invalid-email");
        return userForm;
    }

    /**
     * パスワードが短すぎる UserForm インスタンスを生成します
     * 
     * @return パスワードが5文字以下の UserForm
     */
    public static UserForm createUserFormWithShortPassword() {
        UserForm userForm = createValidUserForm();
        userForm.setPassword("12345");
        userForm.setPasswordConfirmation("12345");
        return userForm;
    }

    /**
     * パスワードが長すぎる UserForm インスタンスを生成します
     * 
     * @return パスワードが129文字以上の UserForm
     */
    public static UserForm createUserFormWithLongPassword() {
        UserForm userForm = createValidUserForm();
        String longPassword = "a".repeat(130);
        userForm.setPassword(longPassword);
        userForm.setPasswordConfirmation(longPassword);
        return userForm;
    }

    /**
     * パスワード確認が一致しない UserForm インスタンスを生成します
     * 
     * @return パスワード確認が不一致の UserForm
     */
    public static UserForm createUserFormWithMismatchedPassword() {
        UserForm userForm = createValidUserForm();
        userForm.setPassword("password123");
        userForm.setPasswordConfirmation("different_password");
        return userForm;
    }

    /**
     * 指定されたメールアドレスで UserForm インスタンスを生成します
     * 
     * @param email メールアドレス
     * @return 指定されたメールアドレスの UserForm
     */
    public static UserForm createUserFormWithEmail(String email) {
        UserForm userForm = createValidUserForm();
        userForm.setEmail(email);
        return userForm;
    }

    /**
     * 指定されたパスワードで UserForm インスタンスを生成します
     * 
     * @param password パスワード
     * @return 指定されたパスワードの UserForm
     */
    public static UserForm createUserFormWithPassword(String password) {
        UserForm userForm = createValidUserForm();
        userForm.setPassword(password);
        userForm.setPasswordConfirmation(password);
        return userForm;
    }

    /**
     * 指定されたユーザー名で UserForm インスタンスを生成します
     * 
     * @param name ユーザー名
     * @return 指定されたユーザー名の UserForm
     */
    public static UserForm createUserFormWithName(String name) {
        UserForm userForm = createValidUserForm();
        userForm.setName(name);
        return userForm;
    }
}
