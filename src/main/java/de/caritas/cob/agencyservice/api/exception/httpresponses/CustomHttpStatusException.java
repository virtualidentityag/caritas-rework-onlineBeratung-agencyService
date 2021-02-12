package de.caritas.cob.agencyservice.api.exception.httpresponses;

import static java.util.Objects.nonNull;

import de.caritas.cob.agencyservice.api.service.LogService;
import java.util.function.Consumer;

/**
 * Custom HTTP status exception that provides
 */
public abstract class CustomHttpStatusException extends RuntimeException {

  private static final long serialVersionUID = -3545035432045919306L;
  private final Consumer<Exception> loggingMethod;

  CustomHttpStatusException() {
    super();
    this.loggingMethod = LogService::logWarning;
  }

  CustomHttpStatusException(String message, Consumer<Exception> loggingMethod) {
    super(message);
    this.loggingMethod = loggingMethod;
  }

  /**
   * Executes the non null logging method.
   */
  public void executeLogging() {
    if (nonNull(this.loggingMethod)) {
      this.loggingMethod.accept(this);
    }
  }
}