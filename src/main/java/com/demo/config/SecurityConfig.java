package com.demo.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // configure pre-authentication flow
                .addFilterBefore(siteminderFilter(), RequestHeaderAuthenticationFilter.class)
                .authorizeRequests()
                // allow access to actuator endpoints
                .antMatchers("/actuator/**").permitAll()
                // allow access to favicon.ico endpoint
                .antMatchers("/favicon.ico").permitAll()
                // allow access to v1 endpoints, still subject to authorization check defined on the method level
                .antMatchers("/v1/**").permitAll()
            //    .and()
              //  .sessionManagement().sessionAuthenticationStrategy(SessionAuthenticationStrategy.)
                // deny all other endpoints
                .anyRequest().denyAll()
        ;

        // alternatively, configure auth here if not using method level security
//        http
//                .addFilterAfter(siteminderFilter(), RequestHeaderAuthenticationFilter.class)
//                .authorizeRequests()
//                .antMatchers("/v1/**")
//                .hasRole("ADMIN")
        ;
    }

    /**
     * Creates RequestHeaderAuthenticationFilter bean and specifies which HTTP headers are for communicating
     * principal and credential information
     *
     * @return
     */
    @Bean(name = "siteminderFilter")
    public RequestHeaderAuthenticationFilter siteminderFilter() throws Exception {
        CustomRequestHeaderFilter customRequestHeaderFilter = new CustomRequestHeaderFilter();
        customRequestHeaderFilter.setAuthenticationManager(super.authenticationManager());
        return customRequestHeaderFilter;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() {
        final List<AuthenticationProvider> providers = new ArrayList<>(1);
        providers.add(preAuthAuthProvider());
        return new ProviderManager(providers);
    }

    @Bean(name = "preAuthProvider")
    PreAuthenticatedAuthenticationProvider preAuthAuthProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() {
        return new AuthorizationUserDetailsService();
    }
}
