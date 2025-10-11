package fr.rawz06.starter.web.controller.pub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/info")
    public Map<String, String> getPublicInfo() {
        return Map.of(
                "appName", "Java Starter Kit",
                "version", "1.0.0",
                "description", "Powered by Angular"
        );
    }
}
