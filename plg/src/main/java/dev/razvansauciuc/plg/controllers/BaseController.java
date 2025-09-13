package dev.razvansauciuc.plg.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController implements ErrorController {

    @RequestMapping("/")
    public String hello() {
        return "Hello world!";
    }

    @RequestMapping("/error")
    @ResponseBody
    public String error(HttpServletRequest request) {
        return "An error occurred!";
    }

}
