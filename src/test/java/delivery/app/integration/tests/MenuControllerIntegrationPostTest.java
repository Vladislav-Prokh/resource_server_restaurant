package delivery.app.integration.tests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MenuControllerIntegrationPostTest {
	
	@Autowired
	private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MenuService menuService;
    
	@Test
	public void testAddBeverageAdditional() throws Exception {
	    BeverageAdditional additional = new BeverageAdditional();
	    additional.setBeverageAdditionalName("Ice");
	    additional.setBeverageAdditionalPrice(10.0f);

	    mockMvc.perform(post("/menu/beverage-additionals")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(additional)))
	            .andExpect(status().isOk()) 
	            .andExpect(jsonPath("$.beverageAdditionalName").value("Ice"))
	            .andExpect(jsonPath("$.beverageAdditionalPrice").value(10.0f));
	}

	@Test
	public void testAddBeverage() throws Exception {
	    Beverage beverage = new Beverage("Pepsi", 12f);

	    mockMvc.perform(post("/menu/beverages")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(beverage)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.beverageName").value("Pepsi"))
	            .andExpect(jsonPath("$.beveragePrice").value(12f));
	}

	@Test
	public void testAddMeal() throws Exception {
	    Meal meal = new Meal("Pasta", 15f);

	    mockMvc.perform(post("/menu/meals")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(meal)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.mealName").value("Pasta"))
	            .andExpect(jsonPath("$.mealPrice").value(15f));
	}
	
	@Test
	public void testAddDessert() throws Exception {
	    Dessert dessert = new Dessert("Cheesecake", 7.5f);

	    mockMvc.perform(post("/menu/desserts")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(dessert)))
	            .andExpect(status().isOk()) 
	            .andExpect(jsonPath("$.dessertName").value("Cheesecake"))
	            .andExpect(jsonPath("$.dessertPrice").value(7.5f));
	}
	@Test
	public void testAddLunch() throws Exception {
	    Meal meal = new Meal("Pasta", 15f);
	    meal = menuService.saveMeal(meal);
	    Dessert dessert = new Dessert("Cheesecake", 7.5f);
	    dessert = menuService.saveDessert(dessert);
	    
	    
	    LunchRequestDTO lunchRequest = new LunchRequestDTO(meal.getMealId(), dessert.getDessertId());

	    mockMvc.perform(post("/menu/lunches")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(lunchRequest)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.mainCourseId").value(meal.getMealId()))
	            .andExpect(jsonPath("$.dessertId").value(dessert.getDessertId()));
	}

}
