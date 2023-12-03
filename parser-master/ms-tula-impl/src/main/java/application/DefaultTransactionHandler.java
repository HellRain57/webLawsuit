package application;

import domain.ports.TransactionHandler;
import java.util.function.Supplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultTransactionHandler implements TransactionHandler {
  @Transactional
  public <T> T run(@NonNull Supplier<T> supplier) {
    return supplier.get();
  }

  @Transactional
  public void run(@NonNull Runnable runnable) {
    runnable.run();
  }
}
