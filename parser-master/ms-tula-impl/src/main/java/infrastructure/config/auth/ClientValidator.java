package infrastructure.config.auth;

import static java.util.Objects.nonNull;

import infrastructure.config.SecurityProperties;
import java.util.List;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class ClientValidator implements OAuth2TokenValidator<Jwt> {

  private final SecurityProperties securityProperties;

  public ClientValidator(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    String clientId = SecurityContextUtils.getClientId(jwt);
    String userClient = securityProperties.getUserClient();
    List<String> serviceClients = securityProperties.getServiceClients();
    if (nonNull(clientId) && (clientId.equals(userClient) || serviceClients.contains(clientId))) {
      return OAuth2TokenValidatorResult.success();
    }
    return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token",
        "Client id '" + clientId + "' is not valid", null));
  }

}
