package infrastructure.config.auth;

import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public class SecurityContextUtils {

  private static final String APPID = "appid";
  private static final String AUD = "aud";

  public static String getClientId(Jwt jwt) {
    return Optional.<String>
            ofNullable(jwt.getClaim(APPID))
        .orElse((String) ((List<?>) jwt.getClaim(AUD)).get(0));
  }
}
