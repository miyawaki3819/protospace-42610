package in.tech_camp.protospace.controller;

import java.util.Collections;
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

        if (result.hasErrors()) {
            model.addAttribute("prototype", prototype);
            model.addAttribute("commentSubmitFailed", true);
            List<CommentEntity> comments = prototype.getComments() != null
                    ? prototype.getComments()
                    : Collections.emptyList();
            model.addAttribute("comments", comments);
            return "prototypes/detail";
        }

        CommentEntity comment = new CommentEntity();
        comment.setText(commentForm.getText().trim());
        comment.setPrototype(prototype);
        comment.setUser(userDetail.getUser());
        try {
            commentRepository.insert(comment);
        } catch (Exception e) {
            System.out.println("コメント保存エラー：" + e);
            model.addAttribute("prototype", prototype);
            model.addAttribute("commentSubmitFailed", true);
            model.addAttribute("commentErrorMessage", "コメントの保存に失敗しました。もう一度お試しください。");
            List<CommentEntity> comments = prototype.getComments() != null
                    ? prototype.getComments()
                    : Collections.emptyList();
            model.addAttribute("comments", comments);
            return "prototypes/detail";
        }

        return "redirect:/prototypes/" + id;
    }
}
