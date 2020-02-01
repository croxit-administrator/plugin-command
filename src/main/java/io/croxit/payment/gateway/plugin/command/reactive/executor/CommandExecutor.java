package io.croxit.payment.gateway.plugin.command.reactive.executor;

import io.croxit.payment.gateway.plugin.command.reactive.Command;
import reactor.core.publisher.Mono;

public interface CommandExecutor {

  <T, S> Mono<S> execute(Class<? extends Command<T, S>> commandClass, T request);
}
