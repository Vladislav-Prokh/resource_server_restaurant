package delivery.app.controllers;

import delivery.app.dto.AggregatedLunchesDTO;
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


    @GetMapping("/lunches/price-edge")
    public AggregatedLunchesDTO priceEdgeCount(@RequestParam float priceEdge) throws IOException {
       return  this.elasticService.getPriceEdgeCounts(priceEdge);
    }


    @GetMapping("/lunches")
    public List<Lunch> findLunches(@RequestParam String query, @RequestParam(required = false) String lunchesPriceEdgeCondition, @RequestParam float priceEdge ) throws IOException {
        return this.elasticService.findLunches(query,lunchesPriceEdgeCondition,priceEdge);
    }


    @DeleteMapping("/document")
    public void deleteDocumentInIndex(@RequestParam String indexName, @RequestParam String documentId) throws IOException {
        this.elasticService.deleteDocumentInIndex(indexName, documentId);
    }

    @PostMapping("/menu")
    public long initElastic(@RequestParam String type){
        return this.elasticService.initElasticByType(type);
    }
}
