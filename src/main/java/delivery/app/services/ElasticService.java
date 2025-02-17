package delivery.app.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import delivery.app.entities.*;
import delivery.app.exceptions.ResourceNotFoundException;
import lombok.Getter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.io.IOException;
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

    private <T> long initElastic(Page<T> page, String indexName, Function<Integer, Page<T>> fetchPageFunction) throws IOException {
        long totalRecords = page.getTotalElements();
        indexPage(page, indexName);
        for (int i = 1; i < page.getTotalPages(); i++) {
            page = fetchPageFunction.apply(i);
            indexPage(page, indexName);
        }

        return totalRecords;
    }

    private <T> void indexPage(Page<T> page, String indexName) throws IOException {
        List<T> items = page.getContent();
        for (T item : items) {
            String id = getItemId(item);
            System.out.println("Indexing item: " + item);
            try {
                client.index(k -> k
                        .index(indexName)
                        .id(id)
                        .document(item));
            } catch (Exception e) {
                System.err.println("Error indexing " + indexName + " item " + id + ": " + e.getMessage());
            }
        }
    }

    private <T> String getItemId(T item) {
        if (item instanceof Meal) {
            return String.valueOf(((Meal) item).getMealId());
        } else if (item instanceof Dessert) {
            return String.valueOf(((Dessert) item).getDessertId());
        } else if (item instanceof Beverage) {
            return String.valueOf(((Beverage) item).getBeverageId());
        } else if (item instanceof Lunch) {
            return String.valueOf(((Lunch) item).getLunchId());
        }else if (item instanceof Order) {
            return String.valueOf(((Order) item).getOrderId());
        }
        throw new IllegalArgumentException("Unknown item type");
    }

    public long initElasticByMeals() throws IOException {
        Page<Meal> mealPage = this.menuService.getMeals(0, 10);
        return initElastic(mealPage, "meal", page -> this.menuService.getMeals(page, 10));
    }

    public long initElasticByDesserts() throws IOException {
        Page<Dessert> dessertsPage = this.menuService.getDesserts(0, 10);
        return initElastic(dessertsPage, "dessert", page -> this.menuService.getDesserts(page, 10));
    }

    public long initElasticByBeverages() throws IOException {
        Page<Beverage> beveragePage = this.menuService.getBeverages(0, 10);
        return initElastic(beveragePage, "beverage", page -> this.menuService.getBeverages(page, 10));
    }

    public long initElasticByLunches() throws IOException {
        Page<Lunch> lunchPage = this.menuService.getLunches(0, 10);
        return initElastic(lunchPage, "lunch", page -> this.menuService.getLunches(page, 10));
    }

    public long initElasticByOrders() throws IOException {
        Page<Order> orderPage = this.orderService.getOrders(0, 10);
        return initElastic(orderPage, "order", page -> orderService.getOrders(page, 10));
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
