package com.armandoplacido.admin.catalogo.infrastructure;

import com.armandoplacido.admin.catalogo.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Main {
   public static void main(String[] args){
       System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME,"development");
       SpringApplication.run(WebServerConfig.class,args);
   }
}