package dev.cremich.testcontainers.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public class KeycloakContainerTest {

  @ClassRule
  public static final GenericContainer keycloak =
      new GenericContainer("jboss/keycloak:4.6.0.Final")
          .withExposedPorts(8080)
          .withEnv("KEYCLOAK_USER", "admin")
          .withEnv("KEYCLOAK_PASSWORD", "admin")
          .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
          .withClasspathResourceMapping("realm-export.json", "/tmp/realm.json", BindMode.READ_ONLY)
          .withCopyFileToContainer(MountableFile.forClasspathResource("create-keycloak-user.sh", 700),
              "/opt/jboss/create-keycloak-user.sh")
          .waitingFor(Wait.forHttp("/auth"));
  protected static String keycloakHost;

  @BeforeClass
  public static void setupKeycloakContainer() throws IOException, InterruptedException {
    keycloakHost = "http://" + keycloak.getContainerIpAddress() + ":" + keycloak.getMappedPort(8080);
    Container.ExecResult commandResult = keycloak.execInContainer("sh", "/opt/jboss/create-keycloak-user.sh");
    assert commandResult.getExitCode() == 0;

  }

  protected static String getAccessToken(String username, String password, String clientId, String realm) {
    var restTemplate = new RestTemplate();
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    var map = new LinkedMultiValueMap<>();
    map.add("grant_type", "password");
    map.add("client_id", clientId);
    map.add("username", username);
    map.add("password", password);
    var token = restTemplate.postForObject(keycloakHost + "/auth/realms/" + realm + "/protocol/openid-connect/token",
        new HttpEntity<>(map, headers), KeyCloakToken.class);

    assert token != null;
    return token.getAccessToken();
  }

  private static class KeyCloakToken {

    private String accessToken;

    @JsonCreator
    KeyCloakToken(@JsonProperty("access_token") final String accessToken) {
      this.accessToken = accessToken;
    }

    public String getAccessToken() {
      return accessToken;
    }
  }
}
