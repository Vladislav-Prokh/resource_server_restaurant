package delivery.app.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import delivery.app.dto.AggregatedLunchesDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.Dessert;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
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
import java.util.stream.Collectors;


@Service
@Getter
public class ElasticService {

    private final ElasticsearchClient client;
    private final MenuService menuService;
    private final OrderService orderService;


    public ElasticService(
            @Value("${elasticsearch.host}") String elasticsearchHost,
            MenuService menuService, OrderService orderService)
    {
        this.menuService = menuService;
        this.orderService = orderService;
        RestClient restClient = RestClient
                .builder(HttpHost.create(elasticsearchHost))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

    public void saveLunch(Lunch lunch) throws IOException {
       client.index(i -> i
                .index("lunch")
                .id(lunch.getLunchId().toString())
                .document(lunch));
    }

    public void deleteLunch(String lunchId) throws IOException {
        client.delete(i -> i
                .index("lunch")
                .id(lunchId));
    }

    public List<Lunch> findLunches(String query,String  lunchesPriceEdgeCondition,float priceEdge)  throws IOException{
        if(!query.isEmpty()){
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

            return  searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        }
        else if(!lunchesPriceEdgeCondition.isEmpty()){
          return this.getLunchesByTotalPrice(lunchesPriceEdgeCondition,priceEdge);
        }
        return List.of(new Lunch());
    }

    public List<Lunch> getLunchesByTotalPrice(String condition, float priceEdge) throws IOException {

        String scriptSource = "doc['mainCourse.mealPrice'].value + doc['dessert.dessertPrice'].value "
                + (condition.equals("more") ? ">" : "<")
                + " " + priceEdge;


        SearchResponse<Lunch> response = client.search(s -> s
                        .index("lunch")
                        .query(q -> q
                                .bool(b -> b
                                        .filter(f -> f
                                                .script(sc->sc
                                                        .script(sc1->sc1.
                                                                source(scriptSource)
                                                                .lang("painless")
                                                        )

                                                )
                                        )
                                )
                        ),
                Lunch.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public AggregatedLunchesDTO getPriceEdgeCounts(float priceEdge) throws IOException {
        AggregationRange range1 = new AggregationRange.Builder()
                .to((double)priceEdge)
                .build();
        AggregationRange range2 = new AggregationRange.Builder()
                .from((double)priceEdge)
                .build();

        SearchResponse<Void> response = client.search(s -> s
                        .index("lunch")
                        .size(0)
                        .aggregations("price_ranges", a -> a
                                .range(r -> r
                                        .script(script -> script
                                                .source("doc['mainCourse.mealPrice'].value + doc['dessert.dessertPrice'].value")
                                                .lang("painless")
                                        )
                                        .ranges(range1, range2)
                                )
                        ),
                Void.class
        );

        String aggregationResult = response.aggregations().get("price_ranges")._get().toString();
        return parseAggregatedLunchesDTO(aggregationResult);
    }

    private AggregatedLunchesDTO parseAggregatedLunchesDTO(String priceRanges) {
        int indexOfStartValue = 41;
        priceRanges = priceRanges.substring(indexOfStartValue, priceRanges.length() - 3);
        String[] aggregationValues = priceRanges.split("},\\{");

        int indexOfFirstComma = aggregationValues[0].indexOf(",");
        String  amountLessEdgeAsStr = aggregationValues[0].substring(0,indexOfFirstComma);
        int indexOfFirstColon = aggregationValues[1].indexOf(":")+1;
        indexOfFirstComma = aggregationValues[1].indexOf(",");
        String  amountGreaterEdgeAsStr = aggregationValues[1].substring(indexOfFirstColon,indexOfFirstComma);

        return new AggregatedLunchesDTO(Integer.parseInt(amountLessEdgeAsStr),Integer.parseInt(amountGreaterEdgeAsStr));
    }

    public long initElasticByType(String type)  {
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

    public <T> long initElastic(Function<Integer, Page<T>> fetchFunction, Function<T, Object> idExtractor, String indexName) {
        long recordsCount;
        Page<T> page = fetchFunction.apply(0);
        recordsCount = indexPage(page, idExtractor, indexName);

        for (int i = 1; i < page.getTotalPages(); i++) {
            page = fetchFunction.apply(i);
            recordsCount +=indexPage(page, idExtractor, indexName);
        }
        return recordsCount;
    }

    private <T> long indexPage(Page<T> page, Function<T, Object> idExtractor, String indexName) {
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
