package infrastructure.config;

import static adapters.Constants.PRODUCTS;
import static java.time.OffsetDateTime.now;

import java.time.Clock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableCaching
@EnableScheduling
@Configuration
public class CacheConfig {

  @Autowired
  private Clock clock;

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(PRODUCTS);
  }

  @CacheEvict(allEntries = true, value = {PRODUCTS})
  @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 500)
  public void flushProductsCache() {
    log.trace("Flush {} cache: {}", PRODUCTS, now(clock));
  }
}
