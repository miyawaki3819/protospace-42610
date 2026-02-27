package in.tech_camp.protospace.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;

import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.repository.PrototypeRepository;
import in.tech_camp.protospace.validation.ValidationPriority1;
import in.tech_camp.protospace.service.PrototypeService;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping(value = {"/", "/prototypes"})
@AllArgsConstructor
public class PrototypeController {

    private final PrototypeService prototypeService;
    private final PrototypeRepository prototypeRepository;

    @GetMapping({"", "/"})
    public String index(Model model) {
        List<PrototypeEntity> prototypes = prototypeRepository.findAll();
        model.addAttribute("prototypes", prototypes);
        return "prototypes/index";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("prototypeForm", new PrototypeForm());
        return "prototypes/new";
    }

    @PostMapping
    public String create(
            @ModelAttribute("prototypeForm") @Validated(ValidationPriority1.class) PrototypeForm prototypeForm,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Model model) {

        if (result.hasErrors()) {
            prototypeForm.setImageFile(null);
            model.addAttribute("prototypeForm", prototypeForm);
            return "prototypes/new";
        }

        try {
            Integer userId = userDetail.getUser().getId();
            PrototypeEntity prototype = new PrototypeEntity();
            prototype.setTitle(prototypeForm.getTitle());
            prototype.setCatchCopy(prototypeForm.getCatchCopy());
            prototype.setConcept(prototypeForm.getConcept());
            prototype.setUserId(userId);
            if (prototypeForm.getImageFile() != null && !prototypeForm.getImageFile().isEmpty()) 
            {   prototype.setImageName(prototypeForm.getImageFile().getOriginalFilename());
                prototype.setImageType(prototypeForm.getImageFile().getContentType());
                prototype.setImageData(prototypeForm.getImageFile().getBytes()); }
            prototypeService.createPrototype(prototype);
            return "redirect:/prototypes/";
        } catch (IOException e) {
      System.out.println("画像処理エラー：" + e);
      model.addAttribute("errorMessages", List.of("画像の処理中にエラーが発生しました"));
      model.addAttribute("prototypeForm", prototypeForm);
      return "prototypes/new";
    } catch (Exception e) {
      System.out.println("データベースエラー：" + e);
      model.addAttribute("errorMessages", List.of("保存中にエラーが発生しました"));
      model.addAttribute("prototypeForm", prototypeForm);
      return "prototypes/new";}
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