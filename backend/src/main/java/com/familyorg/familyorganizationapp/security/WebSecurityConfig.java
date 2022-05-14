package com.familyorg.familyorganizationapp.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private CustomLogoutSuccessHandler customLogoutSuccessHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .configurationSource(CustomCorsFilter.configurationSource())
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/services/auth/**")
        .permitAll()
        .antMatchers("/api/services/auth/usernameCheck",
            "/api/services/auth/emailCheck",
            "/login",
            "/signup",
            "/api/v1/utility/timezones",
            "/api/services/auth/csrf")
        .permitAll()
        .antMatchers("/api/**")
        .authenticated()
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/api/services/auth/logout"))
        .logoutSuccessHandler(customLogoutSuccessHandler)
        .and()
        .formLogin().disable()
        .exceptionHandling()
        .and()
        .csrf()
        .disable();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  public AuthenticationManager customAuthenticationManager() throws Exception {
    return authenticationManager();
  }

  private class CustomCorsFilter extends CorsFilter {

    public CustomCorsFilter() {
      super(configurationSource());
    }

    public static UrlBasedCorsConfigurationSource configurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowCredentials(true);
      configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
      configuration.addAllowedHeader("*");
      configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PATCH", "DELETE"));
      configuration.setMaxAge(3600L);

      UrlBasedCorsConfigurationSource corsConfigurationSource =
          new UrlBasedCorsConfigurationSource();
      corsConfigurationSource.registerCorsConfiguration("/**", configuration);

      return corsConfigurationSource;
    }
  }
}
