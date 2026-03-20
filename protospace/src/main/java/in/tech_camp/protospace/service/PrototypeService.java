package in.tech_camp.protospace.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.form.PrototypeForm;
import in.tech_camp.protospace.repository.PrototypeRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PrototypeService {

    private final PrototypeRepository prototypeRepository;

    public Integer createFromForm(PrototypeForm form, Integer userId) throws IOException {
        PrototypeEntity entity = convertFormToEntity(form, userId);
        prototypeRepository.insert(entity);
        return entity.getId();
    }

    private PrototypeEntity convertFormToEntity(PrototypeForm form, Integer userId) throws IOException {
        PrototypeEntity entity = new PrototypeEntity();
        entity.setTitle(form.getTitle());
        entity.setCatchCopy(form.getCatchCopy());
        entity.setConcept(form.getConcept());
        entity.setUserId(userId);
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            entity.setImageName(form.getImageFile().getOriginalFilename());
            entity.setImageType(form.getImageFile().getContentType());
            entity.setImageData(form.getImageFile().getBytes());
        }
        return entity;
    }
}
