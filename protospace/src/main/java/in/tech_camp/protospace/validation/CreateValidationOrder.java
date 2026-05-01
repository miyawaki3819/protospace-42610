package in.tech_camp.protospace.validation;

import jakarta.validation.GroupSequence;

@GroupSequence({ CreateValidationGroup1.class, CreateValidationGroup2.class })
public interface CreateValidationOrder {
}
