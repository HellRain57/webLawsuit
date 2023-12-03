package infrastructure.config;

import com.google.common.collect.ImmutableList;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {


  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(ImmutableList.of("*"));
    configuration.setAllowedMethods(ImmutableList.of("HEAD",
        "GET", "POST", "PUT", "DELETE", "PATCH"));
    // setAllowCredentials(true) is important, otherwise:
    // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
    configuration.setAllowCredentials(true);
    // setAllowedHeaders is important! Without it, OPTIONS preflight request
    // will fail with 403 Invalid CORS request
    configuration.setAllowedHeaders(
        ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @ConditionalOnProperty(name = "security.enabled", havingValue = "true")
  @Bean
  public SecurityFilterChain securedFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf()
        .disable()
        .cors()
        .and()
        .authorizeRequests()
        .antMatchers(
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/actuator/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();

    return http.build();
  }

  @ConditionalOnProperty(name = "security.enabled", havingValue = "false", matchIfMissing = true)
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .httpBasic().disable()
      .cors().and().csrf().disable()
    .build();
  }

  @ConditionalOnProperty(name = "security.ssl.validate-peer-certificate", havingValue = "true",
      matchIfMissing = true)
  @Bean
  public RestTemplate restTemplateSsl() {
    return new RestTemplate();
  }

  @SneakyThrows
  @ConditionalOnProperty(name = "security.ssl.validate-peer-certificate", havingValue = "false")
  @Bean
  public RestTemplate restTemplateNoSsl() {
    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial((X509Certificate[] chain, String authType) -> true)
        .build();
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
        .build();
    var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }
}
