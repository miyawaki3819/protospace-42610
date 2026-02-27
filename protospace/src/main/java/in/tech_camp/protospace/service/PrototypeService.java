package in.tech_camp.protospace.service;

import org.springframework.stereotype.Service;

import in.tech_camp.protospace.entity.PrototypeEntity;
import in.tech_camp.protospace.repository.PrototypeRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PrototypeService {

    private final PrototypeRepository prototypeRepository;

    public Integer createPrototype(PrototypeEntity entity) {
        prototypeRepository.insert(entity);
        return entity.getId();
    }
}
