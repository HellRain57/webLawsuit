package domain.ports;

import java.util.function.Supplier;
import lombok.NonNull;

public interface TransactionHandler {
  <T> T run(@NonNull Supplier<T> supplier);

  void run(@NonNull Runnable runnable);
}
