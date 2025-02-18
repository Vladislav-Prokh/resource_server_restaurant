package delivery.app.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import delivery.app.entities.Beverage;
import delivery.app.entities.Dessert;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
import delivery.app.exceptions.ResourceNotFoundException;
import lombok.Getter;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Getter
public class ElasticService {

    private final ElasticsearchClient client;
    private final MenuService menuService;
    private final OrderService orderService;

    public ElasticService(
            @Value("${elasticsearch.host}") String elasticsearchHost,
            MenuService menuService, OrderService orderService
    )
    {
        this.menuService = menuService;
        this.orderService = orderService;
        RestClient restClient = RestClient
                .builder(HttpHost.create(elasticsearchHost))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }


    public List<Lunch> findLunches(String query)  throws IOException{

        SearchResponse<Lunch> searchResponse = client.search(s -> s
                .index("lunch")
                .query(q -> q.bool(b -> b
                        .must(m -> m.multiMatch(mm -> mm
                                .query(query)
                                .fields("mainCourse.mealName", "mainCourse.description",
                                        "dessert.dessertName", "dessert.description")
                                .fuzziness("AUTO")
                        ))
                )), Lunch.class);


        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public long initElasticByType(String type) throws IOException {
        switch (type.toLowerCase()) {
            case "meal":
                return initElastic(page -> menuService.getMeals(page, 10), Meal::getMealId, "menu");
            case "dessert":
                return initElastic(page -> menuService.getDesserts(page, 10), Dessert::getDessertId, "menu");
            case "beverage":
                return initElastic(page -> menuService.getBeverages(page, 10), Beverage::getBeverageId, "menu");
            case "lunch":
                return initElastic(page -> menuService.getLunches(page, 10), Lunch::getLunchId, "lunch");
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public <T> long initElastic(Function<Integer, Page<T>> fetchFunction, Function<T, Object> idExtractor, String indexName) throws IOException {
        long recordsCount = 0;
        Page<T> page = fetchFunction.apply(0);
        recordsCount = indexPage(page, idExtractor, indexName);

        for (int i = 1; i < page.getTotalPages(); i++) {
            page = fetchFunction.apply(i);
            recordsCount +=indexPage(page, idExtractor, indexName);
        }
        return recordsCount;
    }

    private <T> long indexPage(Page<T> page, Function<T, Object> idExtractor, String indexName) throws IOException {
        long recordsIndexed = 0;
        List<T> items = page.getContent();
        for (T item : items) {
            String id = idExtractor.apply(item).toString();
            try {
                client.index(k -> k
                        .index(indexName)
                        .id(id)
                        .document(item));
                recordsIndexed++;
            } catch (Exception e) {
                System.err.println("Error indexing " + indexName + " item " + id + ": " + e.getMessage());
                return 0;
            }
        }
        return recordsIndexed;
    }

    public void deleteDocumentInIndex(String indexName, String documentId) throws IOException {

        DeleteResponse response = client.delete(i -> i
                .index(indexName)
                .id(documentId));
        if(response.result() == Result.NotFound){
            throw new ResourceNotFoundException("Document not found");
        }
    }



}
