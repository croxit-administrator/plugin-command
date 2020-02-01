package io.croxit.payment.gateway.plugin.command.reactive;

import io.croxit.payment.gateway.plugin.command.helper.CommandHelper;
import reactor.core.publisher.Mono;

public interface Command<R, T> extends CommandHelper {


  /**
   * Command logic implementation
   *
   * @param request command request
   * @return command response in Single
   */
  Mono<T> execute(R request);

  /**
   * If {@link #execute(Object)} produce error,
   * this method will executed as fallback method
   *
   * @param throwable error from {@link #execute(Object)}
   * @param request   command request
   * @return fallback command response
   */
  default Mono<T> fallback(Throwable throwable, R request) {
    return Mono.error(throwable);
  }

  /**
   * Is request need to be validated before execute command
   *
   * @return true if need, false if not need
   */
  default boolean validateRequest() {
    return true;
  }
}
