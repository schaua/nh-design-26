package com.neueda.leap.sprint6;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

// This is a simple Controller for handling library-related requests
// such as displaying information about the library or 
// redirecting to API documentation.

@Controller
public class LibraryController {

    @GetMapping("/about")
    public String about() {
        return "forward:/about.html" ;
    }

    @GetMapping("/api/docs")
    public String contracts() {
        return "redirect:/docs.html";
    }
}
