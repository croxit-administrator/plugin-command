package io.croxit.payment.gateway.plugin.command.configuration;



import io.croxit.payment.gateway.plugin.command.reactive.executor.impl.CommandExecutorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Validator;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveCommandPluginConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public Scheduler scheduler() {
    return Schedulers.elastic();
  }

  @Bean
  @ConditionalOnMissingBean
  public CommandExecutorImpl serviceExecutor(@Autowired Validator validator) {
    return new CommandExecutorImpl(validator);
  }
}
