package delivery.app.integration.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.transaction.Transactional;
import delivery.app.dto.LunchRequestDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Meal;
import delivery.app.services.MenuService;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
public class MenuControllerIntegrationGetTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MenuService menuService;

	@BeforeEach
	public void setUp() {

		Beverage beverage = new Beverage("Coke", 10f);
		menuService.saveBeverage(beverage);

		Meal meal = new Meal("Spaghetti", 12f);
		meal = menuService.saveMeal(meal);

		Dessert dessert = new Dessert("Chocolate Cake", 5f);
		dessert = menuService.saveDessert(dessert);

		BeverageAdditional additional = new BeverageAdditional();
		additional.setBeverageAdditionalName("ice");
		additional.setBeverageAdditionalPrice(10.0f);
		 menuService.saveBeverageAdditional(additional);

		LunchRequestDTO lunchRequest = new LunchRequestDTO(meal.getMealId(), dessert.getDessertId());
		menuService.saveLunch(lunchRequest);

	}

	@Test
	public void testGetBeverages() throws Exception {

		mockMvc.perform(get("/menu/beverages?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	public void testGetAdditionals() throws Exception {

		mockMvc.perform(get("/menu/additionals?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	public void testGetLunches() throws Exception {
		mockMvc.perform(get("/menu/lunches?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	public void testGetMeals() throws Exception {
		mockMvc.perform(get("/menu/meals?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	public void testGetDesserts() throws Exception {

		mockMvc.perform(get("/menu/desserts?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
	}

}
