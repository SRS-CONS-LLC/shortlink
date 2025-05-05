package com.srscons.shortlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    @RequestMapping("/index")
    public String indexPage() {
        return "index";
    }


    @RequestMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @RequestMapping("/test-smartlink")
    public String testSmartlinkPage() {
        return "test-smartlink"; // templates/test-smartlink.html faylına yönləndirir
    }


}
