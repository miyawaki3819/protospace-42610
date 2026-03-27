package in.tech_camp.protospace.form;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@GroupSequence({ValidationGroups.First.class, ValidationGroups.Second.class, ValidationGroups.Third.class, UserForm.class})
public class UserForm {
    @NotBlank(message = "Name is required", groups = ValidationGroups.First.class)
    private String name;

    @NotBlank(message = "Email is required", groups = ValidationGroups.First.class)
    @Email(message = "Please enter a valid email address", groups = ValidationGroups.First.class)
    private String email;

    @NotBlank(message = "Password is required", groups = ValidationGroups.First.class)
    @Size(min = 6, max = 128, message = "Password must be 6 to 128 characters", groups = ValidationGroups.Second.class)
    private String password;

    @NotBlank(message = "Password confirmation is required", groups = ValidationGroups.First.class)
    private String passwordConfirmation;

    @AssertTrue(message = "Passwords do not match", groups = ValidationGroups.Third.class)
    public boolean isPasswordConfirmationValid() {
        return password == null || password.equals(passwordConfirmation);
    }

    @NotBlank(message = "Profile is required", groups = ValidationGroups.First.class)
    private String profile;

    @NotBlank(message = "Occupation is required", groups = ValidationGroups.First.class)
    private String occupation;

    @NotBlank(message = "Position is required", groups = ValidationGroups.First.class)
    private String position;

}
