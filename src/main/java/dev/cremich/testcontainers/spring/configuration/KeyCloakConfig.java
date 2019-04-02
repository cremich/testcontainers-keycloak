package dev.cremich.testcontainers.spring.configuration;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * The KeyCloakConfig class extends KeycloakWebSecurityConfigurerAdapter, which is a class provided by Keycloak that provides
 * integration with Spring Security.
 */
@Configuration
@EnableWebSecurity
class KeyCloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * And then we configure Spring Security to authorize all requests.
     *
     * @param http the http security
     * @throws Exception an exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        super.configure(http);
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
            .and()
            .authorizeRequests()
            .antMatchers("/actuator/**").hasRole("monitoring")
            .anyRequest().permitAll();
    }

    /**
     * We configure the authentication manager with the addition of a SimpleAuthorityMapper, which is responsible for converting the
     * role name coming from Keycloak to match the conventions of Spring Security. Basically Spring Security expects rolenames to start
     * with the ROLE_ prefix, and we have 2 choices: either we name our roles like ROLE_ADMIN in Keycloak, or we can name them like admin,
     * and then use this mapper to convert it to uppercase and prepend the necessary ROLE_ prefix.
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Override
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        var provider = super.keycloakAuthenticationProvider();
        provider.setGrantedAuthoritiesMapper(grantedAuthoritiesMapper());
        return provider;
    }

    /**
     * We also need to set a session strategy for Keycloak, but as we are creating a stateless REST service we do not really want to have sessions,
     * therefore we utilize the NullAuthenticatedSessionStrategy.
     *
     * @return a session authentication strategy
     */
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return new SimpleAuthorityMapper();
    }

    /**
     * Normally, the Keycloak Spring Security integration resolves the keycloak configuration from a keycloak.json file, however we would like to
     * have proper Spring Boot configuration, so we override the configuration resolver with the one for Spring Boot
     *
     * @return a config resolver
     */
    @Bean
    KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * And finally, per documentation we prevent double-registering the filters for Keycloak.
     *
     * @param filter a KeycloakAuthenticationProcessingFilter
     * @return a filter registration bean
     */
    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(final KeycloakAuthenticationProcessingFilter filter) {
        var registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    /**
     * And finally, per documentation we prevent double-registering the filters for Keycloak.
     *
     * @param filter a KeycloakPreAuthActionsFilter
     * @return a filter registration bean
     */
    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(final KeycloakPreAuthActionsFilter filter) {
        var registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
