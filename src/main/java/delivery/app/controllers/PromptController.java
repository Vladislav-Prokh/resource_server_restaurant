package delivery.app.controllers;


import delivery.app.services.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai")
public class PromptController {

    private final AiService aiService;

    public PromptController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/fact")
    public String getResponse() {
        return this.aiService.getResponse();
    }
}
