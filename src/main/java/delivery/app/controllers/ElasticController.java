package delivery.app.controllers;

import delivery.app.services.ElasticService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/elastic")
public class ElasticController {

    private final ElasticService elasticService;

    public ElasticController(ElasticService elasticService) {
        this.elasticService = elasticService;
    }

    @PostMapping("/orders")
    public long initElasticByOrders() throws IOException {
        return elasticService.initElasticByOrders();
    }

    @PostMapping("/meals")
    public long initElasticByMeals() throws IOException {
        return elasticService.initElasticByMeals();
    }
    @PostMapping("/desserts")
    public long initElasticByDesserts() throws IOException {
        return elasticService.initElasticByDesserts();
    }
    @PostMapping("/beverages")
    public long initElasticByBeverages() throws IOException {
        return elasticService.initElasticByBeverages();
    }
    @PostMapping("/lunches")
    public long initElasticByLunches() throws IOException {
        return elasticService.initElasticByLunches();
    }
    @DeleteMapping("/document")
    public void deleteDocumentInIndex(@RequestParam String indexName, @RequestParam String documentId) throws IOException {
        this.elasticService.deleteDocumentInIndex(indexName, documentId);
    }
}
