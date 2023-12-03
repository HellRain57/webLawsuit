package domain.exceptions;

import lombok.ToString;

@ToString
public abstract class DomainException extends RuntimeException {

  @Override
  public String getMessage() {
    return "There are domain errors: \n" + this;
  }
}
