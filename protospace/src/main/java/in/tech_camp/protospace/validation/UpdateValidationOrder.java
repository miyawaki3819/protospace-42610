package in.tech_camp.protospace.validation;

import jakarta.validation.GroupSequence;

@GroupSequence({ UpdateValidationGroup1.class, UpdateValidationGroup2.class })
public interface UpdateValidationOrder {
}
