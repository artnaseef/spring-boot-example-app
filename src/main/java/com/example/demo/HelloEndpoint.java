package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloEndpoint {
    @GetMapping("/hi")
    public String sayHi() {
        return "Hello World";
    }
}
