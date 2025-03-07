package delivery.app.controllers.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import delivery.app.dto.LunchRequestDTO;
import delivery.app.controllers.MenuController;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Meal;
import delivery.app.services.MenuService;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerPostMethodTest {

	    @Mock
	    private MenuService menuService;
	    @Autowired
	    private MockMvc mockMvc;
	    @Autowired
	    private ObjectMapper objectMapper;

	    @BeforeEach
	    void setUp() {
			MockitoAnnotations.openMocks(this);
			MenuController menuController = new MenuController(menuService);
	        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
	        objectMapper = new ObjectMapper();
	    }

	    @Test
	    void testAddBeverageAdditional() throws Exception {
	        BeverageAdditional additional = new BeverageAdditional();
	        additional.setBeverageAdditionalId(1L);
	        additional.setBeverageAdditionalName("lemon");
	        additional.setBeverageAdditionalPrice(2.0f);

	        mockMvc.perform(post("/menu/beverage-additionals")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(additional)))
	                .andExpect(status().isOk());

	        verify(menuService, times(1)).saveBeverageAdditional(any(BeverageAdditional.class));

	    }

	    @Test
	    void testAddBeverage() throws Exception {
	
	        Beverage beverage = new Beverage("Coffee","description",12.0f);

	        mockMvc.perform(post("/menu/beverages")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(beverage)))
	                .andExpect(status().isOk());


	        verify(menuService, times(1)).saveBeverage(any(Beverage.class));
	    }

	    @Test
	    void testAddMeal() throws Exception {

	        Meal meal = new Meal("Pizza",10.0f);

	        mockMvc.perform(post("/menu/meals")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(meal)))
	                .andExpect(status().isOk());

	        verify(menuService, times(1)).saveMeal(any(Meal.class));
	    }

	    @Test
	    void testAddDessert() throws Exception {
	        Dessert dessert = new Dessert("Cake", 5.0f);

	        mockMvc.perform(post("/menu/desserts")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(dessert)))
	                .andExpect(status().isOk());

	        verify(menuService, times(1)).saveDessert(any(Dessert.class));
	    }
	    
	    @Test
	    void testAddLunch() throws Exception{
	    	LunchRequestDTO lunchDTO = new LunchRequestDTO();
	    	lunchDTO.setDessertId(1L);
	    	lunchDTO.setMainCourseId(1L);
	    	
	    	mockMvc.perform(post("/menu/lunches")
	    			.contentType(MediaType.APPLICATION_JSON)
	    			.content(objectMapper.writeValueAsString(lunchDTO)))
	    			.andExpect(status().isOk());
	    				
	    	verify(menuService, times(1)).saveLunch(any(LunchRequestDTO.class));
	    }
}
