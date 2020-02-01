package io.croxit.payment.gateway.plugin.command.exception;

import javax.validation.ConstraintViolation;
import java.util.Set;


public class CommandValidationException extends CommandRuntimeException{
    private Set<ConstraintViolation<?>> constraintViolations;

    public CommandValidationException(Set constraintViolations) {
        this(null, constraintViolations);
    }

    public CommandValidationException(String message, Set constraintViolations) {
        super(message);
        this.constraintViolations = constraintViolations;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }
}
