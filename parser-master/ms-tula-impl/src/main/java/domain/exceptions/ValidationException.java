package domain.exceptions;

import static java.lang.String.join;

import am.ik.yavi.core.ConstraintViolations;
import java.util.ArrayList;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidationException extends DomainException {

  private final transient Object object;
  private final ConstraintViolations violations;

  public ValidationException(Object object, ConstraintViolations violations) {
    this.object = object;
    this.violations = violations;
  }

  @Override
  public String getMessage() {
    var builder = new StringBuilder()
        .append("Validation errors of object ")
        .append(object.toString())
        .append("\n");

    var list = new ArrayList<String>();
    for (var violation : violations) {
      list.add("    " + violation.message());
    }

    return builder.append(join("\n", list)).toString();
  }
}
