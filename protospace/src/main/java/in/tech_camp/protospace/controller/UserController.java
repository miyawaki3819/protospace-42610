package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.entity.User;
import in.tech_camp.protospace.form.LoginForm;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/signUp";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "users/signUp";
        }

        // パスワードとパスワード再入力の一致確認
        if (!userForm.getPassword().equals(userForm.getPasswordConfirmation())) {
            bindingResult.rejectValue("passwordConfirmation", "error.passwordConfirmation", "パスワードとパスワード再入力が一致しません");
            return "users/signUp";
        }

        // UserFormからUserエンティティに変換
        User user = new User();
        user.setEmail(userForm.getEmail());
        user.setEncryptedPassword(userForm.getPassword()); // 本来はハッシュ化が必要
        user.setName(userForm.getName());
        user.setProfile(userForm.getProfile());
        user.setOccupation(userForm.getOccupation());
        user.setPosition(userForm.getPosition());

        // データベースに保存
        userMapper.insert(user);

        // セッションにユーザーIDを保存
        session.setAttribute("userId", user.getId());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        // ログイン中にログインページへアクセスした場合はトップページへリダイレクト
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "users/login";
        }

        // メールアドレスでユーザーを検索
        User user = userMapper.findByEmail(loginForm.getEmail());
        
        // ユーザーが存在しない、またはパスワードが一致しない場合
        if (user == null || !user.getEncryptedPassword().equals(loginForm.getPassword())) {
            return "users/login";
        }

        // セッションにユーザーIDを保存
        session.setAttribute("userId", user.getId());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/detail")
    public String detail(HttpSession session) {
        // 未ログインでユーザー情報ページにアクセスした場合はログインページへリダイレクト
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }
        // ユーザー詳細ページの実装は後で追加
        return "users/detail";
    }
}

