package delivery.app.controllers;

import delivery.app.entities.Lunch;
import delivery.app.services.ElasticService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/elastic")
public class ElasticController {

    private final ElasticService elasticService;

    public ElasticController(ElasticService elasticService) {
        this.elasticService = elasticService;
    }



    @PostMapping("/menu")
    public long initElastic(@RequestParam String type) throws IOException {
        return this.elasticService.initElasticByType(type);
    }

    @GetMapping("/lunches")
    public List<Lunch> findLunches(@RequestParam String query) throws IOException {
        return this.elasticService.findLunches(query);
    }

    @DeleteMapping("/document")
    public void deleteDocumentInIndex(@RequestParam String indexName, @RequestParam String documentId) throws IOException {
        this.elasticService.deleteDocumentInIndex(indexName, documentId);
    }
}
