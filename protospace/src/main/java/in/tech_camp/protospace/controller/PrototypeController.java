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
import in.tech_camp.protospace.validation.CreateValidationOrder;
import in.tech_camp.protospace.validation.UpdateValidationOrder;
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

    @GetMapping("/prototypes/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {

        PrototypeEntity prototype = prototypeRepository.findById(id);
        if (prototype == null) {
            return "redirect:/prototypes/";
        }

        model.addAttribute("prototype", prototype);
        return "prototypes/detail";
    }

    @GetMapping("/prototypes/new")
    public String newForm(Model model) {
        model.addAttribute("prototypeForm", new PrototypeForm());
        return "prototypes/new";
    }

    @PostMapping("/prototypes")
    public String create(
            @ModelAttribute("prototypeForm") @Validated(CreateValidationOrder.class) PrototypeForm prototypeForm,
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
            return "redirect:/prototypes/";
        }

        return "redirect:/prototypes/";
    }

    @PostMapping("/prototypes/")
    public String createWithSlash(
            @ModelAttribute("prototypeForm") @Validated(CreateValidationOrder.class) PrototypeForm prototypeForm,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Model model) throws IOException {
        return create(prototypeForm, result, userDetail, model);
    }

    @GetMapping("/prototypes/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, @AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        PrototypeEntity prototype = prototypeRepository.findById(id);
        if (prototype == null) {
            return "redirect:/prototypes/";
        }
        if (prototype.getUser() == null || userDetail == null || userDetail.getUser() == null
                || !prototype.getUser().getId().equals(userDetail.getUser().getId())) {
            return "redirect:/prototypes/";
        }

        PrototypeForm prototypeForm = new PrototypeForm();
        prototypeForm.setTitle(prototype.getTitle());
        prototypeForm.setCatchCopy(prototype.getCatchCopy());
        prototypeForm.setConcept(prototype.getConcept());
        model.addAttribute("prototypeForm", prototypeForm);
        model.addAttribute("prototype", prototype);
        return "prototypes/edit";
    }

    @PostMapping("/prototypes/{id}")
    public String update(
            @PathVariable("id") Integer id,
            @ModelAttribute("prototypeForm") @Validated(UpdateValidationOrder.class) PrototypeForm prototypeForm,
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
            model.addAttribute("prototypeId", id);
            PrototypeEntity prototypeForView = new PrototypeEntity();
            prototypeForView.setId(id);
            model.addAttribute("prototype", prototypeForView);
            return "prototypes/edit";
        }

        PrototypeEntity prototype = prototypeRepository.findById(id);
        if (prototype == null) {
            return "redirect:/prototypes/";
        }
        if (prototype.getUser() == null || userDetail == null || userDetail.getUser() == null
                || !prototype.getUser().getId().equals(userDetail.getUser().getId())) {
            return "redirect:/prototypes/";
        }

        try {
            prototypeService.updateFromForm(prototype, prototypeForm);
        } catch (Exception e) {
            System.out.println("エラー：" + e);
            return "redirect:/prototypes/";
        }

        return "redirect:/prototypes/" + id;
    }

    @GetMapping(value = "/prototypes/{id}/image", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id){
        try {
            PrototypeEntity prototype = prototypeRepository.findById(id);

            if (prototype == null || prototype.getImageData() == null) {
                return ResponseEntity.notFound().build();
            }

            MediaType contentType;
            try {
                if (prototype.getImageType() == null || prototype.getImageType().isBlank()) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                } else {
                    contentType = MediaType.parseMediaType(prototype.getImageType());
                }
            } catch (IllegalArgumentException e) {
                // DBに不正な保存値がある可能性を考慮して、フォールバックのContent-Typeを返す
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .contentLength(prototype.getImageData().length)
                    .body(prototype.getImageData());

        } catch (Exception e) {
            System.out.println("画像取得エラー：" + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}