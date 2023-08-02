package com.example.resourceserverdemo.endpoints;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class RegularUserController {

    @GetMapping
    public String index() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken authentication) {
            String sub = (String) authentication.getTokenAttributes().get("sub");
            System.out.println("Sub Claim: " + sub);
        }
        
        return "<h2> Hello Regular User! </h2>";
    }
}
