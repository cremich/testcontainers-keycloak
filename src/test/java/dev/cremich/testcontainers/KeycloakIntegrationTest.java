package dev.cremich.testcontainers;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.cremich.testcontainers.utils.KeycloakConfiguration;
import dev.cremich.testcontainers.utils.KeycloakContainerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@AutoConfigureMockMvc
public class KeycloakIntegrationTest extends KeycloakContainerTest {

  @Autowired
  private KeycloakConfiguration keycloakConfiguration;

  @Autowired
  protected MockMvc mockMvc;

  @Test
  public void anAnonymousClientIsNotAuthorized() throws Exception {
    overwriteKeyCloakAuthServerUrl();
    this.mockMvc.perform(get("/actuator/info"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  public void aClientWithMonitoringRoleIsOk() throws Exception {
    overwriteKeyCloakAuthServerUrl();
    this.mockMvc.perform(get("/actuator/info")
        .header("Authorization", "Bearer " + getAccessToken("monitoring-user", "default", "demo-frontend", "demo")))
        .andExpect(status().isOk());
  }

  private void overwriteKeyCloakAuthServerUrl() {
    keycloakConfiguration.getProperties().setAuthServerUrl(keycloakHost + "/auth");
  }

}
