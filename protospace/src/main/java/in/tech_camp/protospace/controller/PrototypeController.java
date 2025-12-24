package in.tech_camp.protospace.controller;

import in.tech_camp.protospace.entity.User;
import in.tech_camp.protospace.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrototypeController {
    private final UserMapper userMapper;

    public PrototypeController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userMapper.findById(userId);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "index"; // templates/index.html を表示
    }

    @GetMapping("/prototypes/new")
    public String newPrototype(HttpSession session) {
        // 未ログイン状態でログイン必須機能にアクセスした場合はログインページへリダイレクト
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }
        return "prototypes/new";
    }
}
