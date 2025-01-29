package delivery.app.controllers.menu;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import delivery.app.controllers.MenuController;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
import delivery.app.services.MenuService;

public class MenuControllerGetMethodTest {
	@InjectMocks
	private MenuController menuController;

	@Mock
	private MenuService menuService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetBeverages() {

		Beverage beverage1 = new Beverage();
		beverage1.setBeverageName("Coffee");
		Beverage beverage2 = new Beverage();
		beverage2.setBeverageName("Tea");
		Page<Beverage> beveragePage = new PageImpl<>(Arrays.asList(beverage1, beverage2));

		when(menuService.getBeverages(anyInt(), anyInt())).thenReturn(beveragePage);

		Page<Beverage> result = menuController.getBeverages(0, 10);

		assert (result.getTotalElements() == 2);
		assert (result.getContent().get(0).getBeverageName().equals("Coffee"));
		
		verify(menuService, times(1)).getBeverages(0, 10);
	}

	@Test
	void testGetAdditionals()  {

		BeverageAdditional additional1 = new BeverageAdditional();
		additional1.setBeverageAdditionalName("ice");
		BeverageAdditional additional2 = new BeverageAdditional();
		additional2.setBeverageAdditionalName("lemon");
		Page<BeverageAdditional> additionalsPage = new PageImpl<>(Arrays.asList(additional1, additional2));

		when(menuService.getAdditionals(anyInt(), anyInt())).thenReturn(additionalsPage);

		Page<BeverageAdditional> result = menuController.getAdditionals(0, 10);
		assert (result.getTotalElements() == 2);
		assert (result.getContent().get(0).getBeverageAdditionalName().equals("ice"));
		verify(menuService, times(1)).getAdditionals(0, 10);
	}

	@Test
	void testGetLunches() {

		Lunch lunch = new Lunch();
		
		Meal mainCourse = new Meal();
		mainCourse.setMealName("Pasta");
		Dessert dessert = new Dessert();
		dessert.setDessertName("Cake");
		lunch.setMainCourse(mainCourse);
		lunch.setDessert(dessert);
		

		Page<Lunch> lunchesPage = new PageImpl<>(List.of(lunch));

		when(menuService.getLunches(anyInt(), anyInt())).thenReturn(lunchesPage);

		Page<Lunch> result = menuController.getLunches(0, 10);

		assert (result.getTotalElements() == 1);
		assert (result.getContent().get(0).getMainCourse().getMealName().equals("Pasta"));
		assert (result.getContent().get(0).getDessert().getDessertName().equals("Cake"));
		verify(menuService, times(1)).getLunches(0, 10);
	}

	@Test
	void testGetMeals()  {
		
		Meal meal1 = new Meal();
		meal1.setMealName("Burger");
		Meal meal2 = new Meal();
		meal2.setMealName("Pasta");
		Page<Meal> mealsPage = new PageImpl<>(Arrays.asList(meal1, meal2));

		when(menuService.getMeals(anyInt(), anyInt())).thenReturn(mealsPage);

		Page<Meal> result = menuController.getMeals(0, 10);

		assert (result.getTotalElements() == 2);
		assert (result.getContent().get(0).getMealName().equals("Burger"));
		verify(menuService, times(1)).getMeals(0, 10);
	}

	@Test
	void testGetDesserts() {
		Dessert dessert1 = new Dessert();
		dessert1.setDessertName("Cake");
		Dessert dessert2 = new Dessert();
		dessert2.setDessertName("Candy");
		Page<Dessert> dessertsPage = new PageImpl<>(Arrays.asList(dessert1, dessert2));

		when(menuService.getDesserts(anyInt(), anyInt())).thenReturn(dessertsPage);

		Page<Dessert> result = menuController.getDesserts(0, 10);

		assert (result.getTotalElements() == 2);
		assert (result.getContent().get(1).getDessertName().equals("Candy"));
		verify(menuService, times(1)).getDesserts(0, 10);
	}
}
