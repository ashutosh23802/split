package org.tiaa.in.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FallbackController {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }

    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String redirectAll() {
        return "forward:/";
    }
}
