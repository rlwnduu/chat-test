package com.example.chat.view.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class PageController {

    @GetMapping(value = {"/", "/login"})
    public String rootOrLogin(Principal principal) {
        if (principal == null) {
            return "forward:/login.html";
        }
        return "redirect:/chat";
    }
}
