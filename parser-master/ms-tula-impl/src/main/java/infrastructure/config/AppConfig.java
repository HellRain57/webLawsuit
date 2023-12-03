package infrastructure.config;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Clock;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationPropertiesScan
@EnableJpaRepositories(basePackages = "adapters")
@EntityScan(basePackages = "adapters.repositories.records")
@EnableFeignClients(basePackages = "adapters")
public class AppConfig {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Value("${openai.api-key}")
  private String apiKey;

  @Bean
  OkHttpClient okHttpClient() {
    return new OkHttpClient().newBuilder()
        .readTimeout(90, SECONDS)
        .build();
  }
}
