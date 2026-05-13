package in.tech_camp.protospace.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.protospace.custom_user.CustomUserDetail;
import in.tech_camp.protospace.entity.CommentEntity;
import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.CommentForm;
import in.tech_camp.protospace.repository.CommentRepository;
import in.tech_camp.protospace.repository.PrototypeRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final PrototypeRepository prototypeRepository;

    @PostMapping("/prototypes/{id}/comments")
    public String createComment(
            @PathVariable("id") Integer id,
            @ModelAttribute("commentForm") @Valid CommentForm commentForm,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Model model) {

        PrototypeEntity prototype = prototypeRepository.findById(id);
        if (prototype == null) {
            return "redirect:/prototypes";
        }

        List<CommentEntity> comments = commentRepository.findByPrototypeId(id);
        if (result.hasErrors()) {
            model.addAttribute("prototype", prototype);
            model.addAttribute("comments", comments);
            return "prototypes/detail";
        }

        CommentEntity comment = new CommentEntity();
        comment.setText(commentForm.getText().trim());
        comment.setPrototypeId(id);
        UserEntity user = new UserEntity();
        user.setId(userDetail.getUser().getId());
        comment.setUser(user);
        commentRepository.insert(comment);

        return "redirect:/prototypes/" + id;
    }
}
