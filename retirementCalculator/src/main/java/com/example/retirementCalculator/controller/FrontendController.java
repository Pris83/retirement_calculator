package com.example.retirementCalculator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = { "/", "/{path:^(?!api$).*$}", "/{path:^(?!api$).*$}/**" })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
