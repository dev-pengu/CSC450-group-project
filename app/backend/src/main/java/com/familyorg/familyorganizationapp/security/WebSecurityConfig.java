package com.familyorg.familyorganizationapp.security;

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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private CustomLogoutSuccessHandler customLogoutSuccessHandler;
  @Autowired
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/services/auth/**")
        .permitAll()
        .antMatchers("/api/services/auth/usernameCheck")
        .permitAll()
        .antMatchers("/api/services/auth/emailCheck")
        .permitAll()
        .antMatchers("/login")
        .permitAll()
        .antMatchers("/signup")
        .permitAll()
        .antMatchers("/api/v1/utility/timezones")
        .permitAll()
        .antMatchers("/api/**")
        .authenticated()
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/api/services/auth/logout"))
        .logoutSuccessHandler(customLogoutSuccessHandler)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(customAuthenticationEntryPoint)
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

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
