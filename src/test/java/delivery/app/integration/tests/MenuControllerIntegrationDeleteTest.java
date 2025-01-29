package delivery.app.integration.tests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import delivery.app.dto.LunchRequestDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Meal;
import delivery.app.services.MenuService;
import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
public class MenuControllerIntegrationDeleteTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MenuService menuService;

	private Long beverageAdditionalId;
	private Long beverageId;
	private Long mealId;
	private Long dessertId;
	private Long lunchId;

	@BeforeEach
	public void setUp() {

		BeverageAdditional additional = new BeverageAdditional();
		additional.setBeverageAdditionalName("Ice");
		additional.setBeverageAdditionalPrice(10.0f);
		additional = menuService.saveBeverageAdditional(additional);
		beverageAdditionalId = additional.getBeverageAdditionalId();

		Beverage beverage = new Beverage("Pepsi", 12f);
		beverage = menuService.saveBeverage(beverage);
		beverageId = beverage.getBeverageId();

		Meal meal = new Meal("Pasta", 15f);
		meal = menuService.saveMeal(meal);
		mealId = meal.getMealId();

		Dessert dessert = new Dessert("Cheesecake", 7.5f);
		dessert = menuService.saveDessert(dessert);
		dessertId = dessert.getDessertId();

		LunchRequestDTO lunchRequest = new LunchRequestDTO(meal.getMealId(), dessert.getDessertId());
		lunchId = menuService.saveLunch(lunchRequest).getLunchId();
	}

	@Test
	public void testDeleteBeverageAdditional() throws Exception {
		mockMvc.perform(delete("/menu/beverage-additionals/{additional-id}", beverageAdditionalId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void testDeleteBeverage() throws Exception {
		mockMvc.perform(delete("/menu/beverages/{beverage-id}", beverageId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteMeal() throws Exception {
		mockMvc.perform(delete("/menu/meals/{meal-id}", mealId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteDessert() throws Exception {
		mockMvc.perform(delete("/menu/desserts/{dessert-id}", dessertId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteLunch() throws Exception {
		mockMvc.perform(delete("/menu/lunches/{lunch-id}", lunchId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
