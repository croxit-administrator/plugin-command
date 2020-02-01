package io.croxit.payment.gateway.plugin.command.reactive.executor.impl;


import io.croxit.payment.gateway.plugin.command.exception.CommandValidationException;
import io.croxit.payment.gateway.plugin.command.helper.ErrorHelper;
import io.croxit.payment.gateway.plugin.command.plugin.CommandInterceptor;
import io.croxit.payment.gateway.plugin.command.plugin.InterceptorUtil;
import io.croxit.payment.gateway.plugin.command.reactive.Command;
import io.croxit.payment.gateway.plugin.command.reactive.executor.CommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class CommandExecutorImpl implements CommandExecutor, ApplicationContextAware {

  private Validator validator;

  private ApplicationContext applicationContext;

  private Collection<CommandInterceptor> commandInterceptors;

  public CommandExecutorImpl(Validator validator) {
    this.validator = validator;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    commandInterceptors = InterceptorUtil.getCommandInterceptors(applicationContext);
  }

  @Override
  public <R, T> Mono<T> execute(Class<? extends Command<R, T>> commandClass, R request) {
    return validateRequest(request, commandClass)
            .flatMap(validRequest -> doExecute(commandClass, validRequest));
  }

  private <R> Mono<R> validateRequest(R request, Class<? extends Command<?, ?>> clazz) {
    return Mono.fromCallable(() -> {
      if (isValidateRequest(clazz)) {
        log.info("Validate request {}", request.getClass().getName());
        validateAndThrownIfInvalid(request);
      }
      return request;
    });
  }

  private boolean isValidateRequest(Class<? extends Command<?, ?>> clazz) {
    Command<?, ?> bean = applicationContext.getBean(clazz);
    return bean.validateRequest();
  }

  private <R> void validateAndThrownIfInvalid(R request)
          throws CommandValidationException {
    Set<ConstraintViolation<R>> constraintViolations = validator.validate(request);
    if (!constraintViolations.isEmpty()) {
      String validationMessage = ErrorHelper.from(constraintViolations).toString();
      log.warn("Invalid command request with validation message {}", validationMessage);
      throw new CommandValidationException(validationMessage, constraintViolations);
    }
  }

  private <R, T> Mono<T> doExecute(Class<? extends Command<R, T>> commandClass, R request) {
    Command<R, T> command = applicationContext.getBean(commandClass);
    return InterceptorUtil.beforeExecute(commandInterceptors, command, request)
            .switchIfEmpty(doExecuteCommand(request, command));
  }

  private <R, T> Mono<T> doExecuteCommand(R request, Command<R, T> command) {
    return command.execute(request)
            .doOnSuccess(response -> InterceptorUtil.afterSuccessExecute(commandInterceptors, command, request, response).subscribe())
            .doOnError(throwable -> InterceptorUtil.afterFailedExecute(commandInterceptors, command, request, throwable).subscribe())
            .onErrorResume(throwable -> command.fallback(throwable, request));
  }
}