package delivery.app.controllers.menu;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import delivery.app.controllers.MenuController;
import delivery.app.services.MenuService;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerDeleteMethodTest{

    @Mock
    private MenuService menuService;




    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MenuController menuController = new MenuController(menuService);
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
    }

    @Test
    void testDeleteBeverageAdditional() throws Exception {
        Long additionalId = 1L;

        mockMvc.perform(delete("/menu/beverage-additionals/{additional-id}", additionalId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteBeverageAdditional(additionalId);
    }

    @Test
    void testDeleteBeverage() throws Exception {
        Long beverageId = 1L;

        mockMvc.perform(delete("/menu/beverages/{beverage-id}", beverageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteBeverage(beverageId);
    }

    @Test
    void testDeleteMeal() throws Exception {
        Long mealId = 1L;

        mockMvc.perform(delete("/menu/meals/{meal-id}", mealId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteMeal(mealId);
    }

    @Test
    void testDeleteDessert() throws Exception {
        Long dessertId = 1L;

        mockMvc.perform(delete("/menu/desserts/{dessert-id}", dessertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteDessert(dessertId);
    }

    @Test
    void testDeleteLunch() throws Exception {
        Long lunchId = 1L;

        mockMvc.perform(delete("/menu/lunches/{lunch-id}", lunchId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteLunch(lunchId);
    }
}
