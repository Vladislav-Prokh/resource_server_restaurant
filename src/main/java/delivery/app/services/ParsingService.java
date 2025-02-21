package delivery.app.services;

import delivery.app.dto.ParseData;
import delivery.app.entities.Beverage;
import delivery.app.entities.Meal;
import delivery.app.entities.Dessert;
import lombok.AllArgsConstructor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.jsoup.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParsingService {

    private final MenuService menuService;

    public ParsingService(MenuService menuService) {
        this.menuService = menuService;
    }
    public int parseItemsFromUrlAndSaveToDB(ParseData parseData) throws IOException {
       Document doc = Jsoup.connect(parseData.getUrlWebsite())
                .userAgent(parseData.getUserAgent())
                .referrer(parseData.getReferrer())
                .get();
        Elements productList = doc.select(parseData.getListClass());
        List<Item> items = parseItems(productList, parseData.getNameClass(),
                parseData.getDescriptionClass(), parseData.getPriceClass());
        String itemType = parseData.getItemType();
        if ("MEAL".equals(itemType)) {
          return  menuService.saveMeals(items.stream().map(i -> new Meal(i.name, i.description, i.price)).toList()).size();
        } else if ("DESSERT".equals(itemType)) {
           return  menuService.saveDesserts(items.stream().map(i -> new Dessert(i.name, i.description, i.price)).toList()).size();
        }
        else if ("BEVERAGE".equals(itemType)) {
           return menuService.saveBeverages(items.stream().map(i -> new Beverage(i.name, i.description, i.price)).toList()).size();
        }
        else{
            return 0;
        }
    }
    private List<Item> parseItems(Elements productList,
                                  String itemNameClass,
                                  String itemDescriptionClass,
                                  String itemPriceClass) {
        List<Item> items = new ArrayList<>();
        for (Element element : productList) {
            String itemName = element.select(itemNameClass).text();
            String itemDescription = element.select(itemDescriptionClass).text();
            if(itemDescription.isEmpty()) {
                itemDescription = "ask for manager";
            }
            String itemPrice = element.select(itemPriceClass).text().replace(" ", ".");
            float price = Float.parseFloat(itemPrice);
            items.add(new Item(itemName, itemDescription, price));
        }
        return items;
    }

    @AllArgsConstructor
    private static class Item {
        String name;
        String description;
        float price;
    }
}
