package com.demo.config;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

public class CustomRequestHeaderFilter extends RequestHeaderAuthenticationFilter {

    @Override
    public Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String header = request.getHeader("x-auth-employee");
        System.out.println(header);
        if (header != null) {
//new Apple()
            return new User(header, "pwd", new ArrayList<>());

        }
        return null;
    }
}
