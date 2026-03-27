package in.tech_camp.protospace.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.repository.PrototypeRepository;
import in.tech_camp.protospace.validation.ValidationPriority1;
import in.tech_camp.protospace.service.PrototypeService;

@Controller
public class PrototypeController {

    private final PrototypeService prototypeService;
    private final PrototypeRepository prototypeRepository;

    public PrototypeController(PrototypeService prototypeService, PrototypeRepository prototypeRepository) {
        this.prototypeService = prototypeService;
        this.prototypeRepository = prototypeRepository;
    }

    @GetMapping("/prototypes")
    public String index(Model model) {
        List<PrototypeEntity> prototypes = prototypeRepository.findAll();
        model.addAttribute("prototypes", prototypes);
        return "prototypes/index";
    }

    @GetMapping("/prototypes/")
    public String indexWithSlash(Model model) {
        return index(model);
    }

    @GetMapping("/prototypes/new")
    public String newForm(Model model) {
        model.addAttribute("prototypeForm", new PrototypeForm());
        return "prototypes/new";
    }

    @PostMapping("/prototypes")
    public String create(
            @ModelAttribute("prototypeForm") @Validated(ValidationPriority1.class) PrototypeForm prototypeForm,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Model model) throws IOException {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            prototypeForm.setImageFile(null);
            model.addAttribute("prototypeForm", prototypeForm);
            return "prototypes/new";
        }

        Integer userId = userDetail.getUser().getId();
        try {
            prototypeService.createFromForm(prototypeForm, userId);
        } catch (Exception e) {
            System.out.println("エラー：" + e);
            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/prototypes/")
    public String createWithSlash(
            @ModelAttribute("prototypeForm") @Validated(ValidationPriority1.class) PrototypeForm prototypeForm,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Model model) throws IOException {
        return create(prototypeForm, result, userDetail, model);
    }

    @GetMapping("/prototypes/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        try {
        PrototypeEntity prototype = prototypeRepository.findById(id);
        
        if (prototype == null || prototype.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(prototype.getImageType()));
        
        return new ResponseEntity<>(prototype.getImageData(), headers, HttpStatus.OK);
        
        } catch (Exception e) {
        System.out.println("画像取得エラー：" + e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}