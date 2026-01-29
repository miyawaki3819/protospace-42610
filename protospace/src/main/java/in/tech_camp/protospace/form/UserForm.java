package in.tech_camp.protospace.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.BindingResult;

@Data
public class UserForm {
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 6, message = "パスワードは6文字以上で入力してください")
    private String password;

    @NotBlank(message = "パスワード再入力は必須です")
    private String passwordConfirmation;

    @NotBlank(message = "ユーザー名は必須です")
    private String name;

    @NotBlank(message = "プロフィールは必須です")
    private String profile;

    @NotBlank(message = "所属は必須です")
    private String occupation;

    @NotBlank(message = "役職は必須です")
    private String position;

    public void validatePasswordConfirmation(BindingResult result) {
        if (password != null && !password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", "null", "パスワードが一致しません");
        }
    }
}
