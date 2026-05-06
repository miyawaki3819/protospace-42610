package in.tech_camp.protospace.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.entity.UserEntity;
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

    public void updateFromForm(PrototypeEntity existingEntity, PrototypeForm form) throws IOException {
        existingEntity.setTitle(form.getTitle());
        existingEntity.setCatchCopy(form.getCatchCopy());
        existingEntity.setConcept(form.getConcept());

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            existingEntity.setImageName(form.getImageFile().getOriginalFilename());
            existingEntity.setImageType(form.getImageFile().getContentType());
            existingEntity.setImageData(form.getImageFile().getBytes());
        }

        prototypeRepository.update(existingEntity);
    }

    private PrototypeEntity convertFormToEntity(PrototypeForm form, Integer userId) throws IOException {
        PrototypeEntity entity = new PrototypeEntity();
        entity.setTitle(form.getTitle());
        entity.setCatchCopy(form.getCatchCopy());
        entity.setConcept(form.getConcept());
        UserEntity user = new UserEntity();
        user.setId(userId);
        entity.setUser(user);
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            entity.setImageName(form.getImageFile().getOriginalFilename());
            entity.setImageType(form.getImageFile().getContentType());
            entity.setImageData(form.getImageFile().getBytes());
        }
        return entity;
    }
}
