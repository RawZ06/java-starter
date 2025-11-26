package fr.rawz06.starter.front;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController implements ErrorController {

    /**
     * Catch all 404 errors and forward to index.html for Angular SPA routing.
     * This allows Spring to serve static files normally (js, css, json, etc.)
     * and fallback to index.html for client-side routes.
     */
    @RequestMapping("/error")
    public String handleError() {
        return "forward:/index.html";
    }
}