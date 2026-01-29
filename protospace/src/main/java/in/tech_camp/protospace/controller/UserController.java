package in.tech_camp.protospace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.UserService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserRepository userRepository;
  private final UserService userService;

  @GetMapping("/users/sign_up")
    public String signUpForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/signUp";
    }

  // ユーザー登録
  @PostMapping("/users")
  public String createUser(
      @ModelAttribute("userForm") @Validated UserForm userForm,
      BindingResult result,
      Model model) {

    logger.info("createUser called with email: {}", userForm.getEmail());
    userForm.validatePasswordConfirmation(result);

    if (userRepository.existsByEmail(userForm.getEmail())) {
      logger.warn("Email already exists: {}", userForm.getEmail());
      result.rejectValue("email", "null", "Email already exists");
    }

    if (result.hasErrors()) {
      logger.warn("Validation errors found: {}", result.getAllErrors());
      return "users/signUp";
    }

    logger.info("Validation passed, creating user");
    userService.createUser(userForm);
    logger.info("User created, redirecting to login");
    return "redirect:/users/login";
  }

  // ログイン画面表示
  @GetMapping("/users/login")
  public String showLogin(Model model) {
    return "users/login";
  }

  // トップページ
  @GetMapping("/")
  public String index(@AuthenticationPrincipal CustomUserDetail user, Model model) {
    if (user != null) {
      model.addAttribute("user", user);
    }
    return "index";
  }
}