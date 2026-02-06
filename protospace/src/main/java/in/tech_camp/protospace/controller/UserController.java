package in.tech_camp.protospace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.UserService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;

  @GetMapping("/users/sign_up")
  public String signUpForm(Model model) {
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }

  @PostMapping("/users")
  public String createUser(
      @ModelAttribute("userForm") @Validated UserForm userForm,
      BindingResult result,
      Model model,
      HttpServletRequest request) {

    if (userRepository.existsByEmail(userForm.getEmail())) {
      result.rejectValue("email", "null", "Email already exists");
    }

    if (result.hasErrors()) {
      return "users/signUp";
    }

    UserEntity user = new UserEntity();
    user.setName(userForm.getName());
    user.setEmail(userForm.getEmail());
    user.setPassword(userForm.getPassword());
    user.setProfile(userForm.getProfile());
    user.setOccupation(userForm.getOccupation());
    user.setPosition(userForm.getPosition());

    userService.createUserWithEncryptedPassword(user);

    try {
      request.login(userForm.getEmail(), userForm.getPassword());
    } catch (Exception e) {
      throw new RuntimeException("登録後の自動ログインに失敗しました", e);
    }

    return "redirect:/";
  }

  @GetMapping("/users/login")
  public String showLogin() {
    return "users/login";
  }
}
