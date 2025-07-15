package com.example.Nitin.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PanValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPAN {
    String message() default "Invalid PAN format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
