package dev.razvansauciuc.plg.controllers;

import dev.razvansauciuc.plg.responses.SnippetResponse;
import dev.razvansauciuc.plg.services.SnippetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController implements ErrorController {

    private final SnippetService snippetService;

    public BaseController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @GetMapping("/")
    public SnippetResponse getSnippet() throws Exception {
        return snippetService.generateSnippet();
    }

    @RequestMapping("/error")
    @ResponseBody
    public String error(HttpServletRequest request) {
        return "An error occurred!";
    }

}
