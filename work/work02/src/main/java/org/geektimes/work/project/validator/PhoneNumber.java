package org.geektimes.work.project.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

	String message() default "Please input valid phone number!";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
