package com.example.resourceserverdemo.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @GetMapping
    public String moderator() {
        return "<h2> Hello Moderator! </h2>";
    }
}