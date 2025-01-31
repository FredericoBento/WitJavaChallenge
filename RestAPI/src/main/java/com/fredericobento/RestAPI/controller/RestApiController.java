package com.fredericobento.RestAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestApiController {

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }

    @GetMapping("/sum")
    public String sum(@RequestParam int a, @RequestParam int b) {
       int result = a + b;
       return "result: " + result;
    }

    @GetMapping("/subtraction")
    public String subtraction(@RequestParam int a, @RequestParam int b) {
        int result = a - b;
        return "result: " + result;
    }

    @GetMapping("/multiplication")
    public String multiplication(@RequestParam int a, @RequestParam int b) {
        int result = a * b;
        return "result: " + result;
    }

    @GetMapping("/division")
    public String division(@RequestParam int a, @RequestParam int b) {
        int result = a / b;
        return "result: " + result;
    }
}
