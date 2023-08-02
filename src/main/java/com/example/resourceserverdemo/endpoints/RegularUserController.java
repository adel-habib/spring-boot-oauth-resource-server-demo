package com.example.resourceserverdemo.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class RegularUserController {

    @GetMapping
    public String index() {
        return "<h2> Hello Regular User! </h2>";
    }
}
