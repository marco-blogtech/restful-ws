package com.edicom.webservice.rest.restfulws.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        //Every petition should be authenticated
        httpSecurity.authorizeHttpRequests(
                auth -> auth.anyRequest().authenticated()
        );

        //If not authenticated a web should be shown
        httpSecurity.httpBasic(withDefaults());

        //Disables CSRF to get POST,PUT methods working (NOT RECOMMENDED IN REAL CASE SCENARIOS)
        httpSecurity.csrf().disable();

        return httpSecurity.build();
    }
}
