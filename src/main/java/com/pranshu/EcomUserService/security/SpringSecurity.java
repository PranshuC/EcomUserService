package com.pranshu.EcomUserService.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurity {

    @Order(1)
    @Bean
    public SecurityFilterChain filteringCriteria(HttpSecurity http) throws Exception {
        http.cors().disable();
        http.csrf().disable();
        //http.authorizeRequests(authorize -> authorize.requestMatchers("/auth/*").permitAll());
        //http.authorizeRequests(authorize -> authorize.requestMatchers("/order/*").authenticated());
        http.authorizeRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

/*
SpringSecurity doesn't allow any non-authorized API to be accessed without a token.
So, need SecurityFilterChain to allow few APIs to be accessed without a token.
 */