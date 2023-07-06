package com.example.resourceserverdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "<h2> Hello Regular User! </h2>";
    }

    @GetMapping("admin")
    public String admin() {
        return "<h2> Hello admin! </h2>";
    }

    @GetMapping("moderate")
    public String moderator() {
        return "<h2> Hello Moderator! </h2>";
    }
}
