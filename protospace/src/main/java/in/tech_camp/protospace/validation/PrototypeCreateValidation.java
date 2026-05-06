package in.tech_camp.protospace.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

public final class PrototypeCreateValidation {

    private PrototypeCreateValidation() {
    }

    public interface Group1 extends Default {
    }

    public interface Group2 extends Default {
    }

    @GroupSequence({ Group1.class, Group2.class })
    public interface Order {
    }
}
