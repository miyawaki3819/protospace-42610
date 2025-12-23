package in.tech_camp.protospace.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class PrototypeController {
  @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html を表示
    }
}
