package com.familyorg.familyorganizationapp.configuration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class PropertiesLogger {
  private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

  @EventListener
  public void handleContextRefreshed(ContextRefreshedEvent event) {
    printProperties((ConfigurableEnvironment) event.getApplicationContext().getEnvironment());
  }

  private void printProperties(ConfigurableEnvironment env) {
    log.info("**** APPLICATION PROPERTIES SOURCES ****");
    List<MapPropertySource> propertySources = new LinkedList<>();
    for (PropertySource source : env.getPropertySources()) {
      if (source instanceof MapPropertySource
          && source.getName().contains("application.properties")) {
        log.info(source.toString());
        propertySources.add((MapPropertySource) source);
      }
    }
    log.info("**** APPLICATION PROPERTY VALUES ****");
    print(propertySources, env);
  }

  private void print(List<MapPropertySource> sources, ConfigurableEnvironment env) {
    sources.stream()
        .map(propertySource -> propertySource.getSource().keySet())
        .flatMap(Collection::stream)
        .distinct()
        .sorted()
        .forEach(
            propertyName -> {
              try {
                log.info(
                    "{}={}",
                    propertyName,
                    propertyName.contains("password") || propertyName.equalsIgnoreCase("spring.datasource.url") ? "*" : env.getProperty(propertyName));
              } catch (Exception e) {
                log.warn("{} -> {}", propertyName, e.getMessage());
              }
            });
  }
}
