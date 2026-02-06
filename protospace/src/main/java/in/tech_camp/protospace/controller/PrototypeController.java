package in.tech_camp.protospace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrototypeController {

  @GetMapping("/")
  public String index() {
    return "index";
  }
}
