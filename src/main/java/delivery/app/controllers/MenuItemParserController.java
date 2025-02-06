package delivery.app.controllers;


import delivery.app.dto.ParseData;
import delivery.app.services.ParsingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/menu-items")
public class MenuItemParserController {

    private final ParsingService parsingService;

    public MenuItemParserController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @RequestMapping
    public ResponseEntity<Integer> parseMeals(@RequestBody ParseData parseDataForm)
            throws IOException {
            int amountOfSavedItems = this.parsingService.parseItemsFromUrlAndSaveToDB(parseDataForm);
            return ResponseEntity.ok(amountOfSavedItems);
    }

}
