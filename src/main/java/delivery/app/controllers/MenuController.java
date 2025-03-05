package delivery.app.controllers;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import delivery.app.dto.LunchRequestDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
import delivery.app.services.MenuService;

@RestController
@RequestMapping("/menu")
public class MenuController  {

	private final MenuService menuService;

	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	@GetMapping("/beverages")
    public Page<Beverage> getBeverages(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return menuService.getBeverages(page, size);
    }
	
	@GetMapping("/additionals")
    public Page<BeverageAdditional> getAdditionals(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size)  {
        return menuService.getAdditionals(page, size);
    }

	@GetMapping("/lunches")
    public Page<Lunch> getLunches(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size)  {
        return menuService.getLunches(page, size);
    }
	@GetMapping("/lunches/{id}")
	public Lunch getLunchById(@PathVariable long id)  {
		return menuService.getLunchById(id);
	}

	@GetMapping("/meals")
    public Page<Meal> getMeals(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size)  {
        return menuService.getMeals(page, size);
    }
	
	@GetMapping("/desserts")
    public Page<Dessert> getDesserts(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return menuService.getDesserts(page, size);
    }

	@PostMapping("/beverage-additionals")
	public BeverageAdditional addBeverageAdditional(@RequestBody BeverageAdditional additional) {
		return menuService.saveBeverageAdditional(additional);
	}

	@PostMapping("/beverages")
	public Beverage addBeverage(@Valid @RequestBody Beverage beverage) {
		return menuService.saveBeverage(beverage);
	}
	@PostMapping("/meals")
	public Meal addMeal(@Valid @RequestBody Meal meal) {
		return menuService.saveMeal(meal);
	}
	@PostMapping("/desserts")
	public Dessert addDessert(@Valid @RequestBody Dessert dessert) {
		return menuService.saveDessert(dessert);
	}
	@PostMapping("/lunches")
	public Lunch addLunch(@RequestBody LunchRequestDTO lunch) {
		return this.menuService.saveLunch(lunch);
	}

	@DeleteMapping("/beverage-additionals/{additional-id}")
	public void deleteBeverageAdditional(@PathVariable("additional-id") Long additional_id) {
		menuService.deleteBeverageAdditional(additional_id);
	}
	@DeleteMapping("/beverages/{beverage-id}")
	public void deleteBeverage(@PathVariable("beverage-id") Long beverageId)  {
	    menuService.deleteBeverage(beverageId);
	}
	@DeleteMapping("/meals/{meal-id}")
	public void deleteMeal(@PathVariable("meal-id") Long meal_id)  {
		menuService.deleteMeal(meal_id);
	}
	@DeleteMapping("/desserts/{dessert-id}")
	public void deleteDessert(@PathVariable("dessert-id") Long dessert_id) {
		menuService.deleteDessert(dessert_id);
	}
	@DeleteMapping("/lunches/{lunch-id}")
	public void deleteLunch(@PathVariable("lunch-id") Long lunch_id)  {
		menuService.deleteLunch(lunch_id);
	}
}
