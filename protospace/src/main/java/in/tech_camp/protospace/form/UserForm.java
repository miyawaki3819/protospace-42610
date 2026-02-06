package in.tech_camp.protospace.form;

import org.springframework.validation.BindingResult;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserForm {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirmation;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmationValid() {
        return password == null || password.equals(passwordConfirmation);
    }

    public void validatePasswordConfirmation(BindingResult result) {
        if (!password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", "error.user", "Passwords do not match");
        }
    }

    @NotBlank(message = "Profile is required")
    private String profile;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotBlank(message = "Position is required")
    private String position;

}
