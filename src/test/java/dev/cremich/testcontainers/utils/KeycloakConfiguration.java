package dev.cremich.testcontainers.utils;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfiguration {

  private final KeycloakSpringBootProperties properties;

  public KeycloakConfiguration(KeycloakSpringBootProperties properties) {
    this.properties = properties;
  }

  public KeycloakSpringBootProperties getProperties() {
    return properties;
  }
}
