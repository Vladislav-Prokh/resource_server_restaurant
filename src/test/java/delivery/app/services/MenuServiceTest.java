package delivery.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import delivery.app.dto.LunchRequestDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.BeverageAdditionalRepository;
import delivery.app.repositories.BeverageRepository;
import delivery.app.repositories.DessertRepository;
import delivery.app.repositories.LunchRepository;
import delivery.app.repositories.MealRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
public class MenuServiceTest {

    @Mock
    private BeverageRepository beverageRepository;
    @Mock
    private DessertRepository dessertRepository;
    @Mock
    private MealRepository mealRepository;
    @Mock
    private BeverageAdditionalRepository beverageAdditionalRepository;
    @Mock
    private LunchRepository lunchRepository;

    @InjectMocks
    private MenuService menuService;

    private Beverage beverage;
    private Dessert dessert;
    private Meal meal;
    private BeverageAdditional beverageAdditional;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        beverage = new Beverage();
        beverage.setBeverageId(1L);

        dessert = new Dessert();
        dessert.setDessertId(1L);

        meal = new Meal();
        meal.setMealId(1L);

        Lunch lunch = new Lunch();
        lunch.setLunchId(1L);

        beverageAdditional = new BeverageAdditional();
        beverageAdditional.setBeverageAdditionalId(1L);
    }

    @Test
    void testFindBeverageById_Found() {
        when(beverageRepository.findById(1L)).thenReturn(Optional.of(beverage));

        Beverage result = menuService.findBeverageById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getBeverageId());
        verify(beverageRepository, times(1)).findById(1L);
    }

    @Test
    void testFindBeverageById_NotFound() {
        when(beverageRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> menuService.findBeverageById(1L));

        assertEquals("Resource not found", exception.getMessage());
        verify(beverageRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveBeverage() {
        when(beverageRepository.save(beverage)).thenReturn(beverage);

        Beverage result = menuService.saveBeverage(beverage);

        assertNotNull(result);
        assertEquals(1L, result.getBeverageId());
        verify(beverageRepository, times(1)).save(beverage);
    }

    @Test
    void testFindDessertById_Found() {
        when(dessertRepository.findById(1L)).thenReturn(Optional.of(dessert));

        Dessert result = menuService.findDessertById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getDessertId());
        verify(dessertRepository, times(1)).findById(1L);
    }

    @Test
    void testFindDessertById_NotFound() {
        when(dessertRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> menuService.findDessertById(1L));

        assertEquals("Resource not found", exception.getMessage());
        verify(dessertRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveDessert() {
        when(dessertRepository.save(dessert)).thenReturn(dessert);

        Dessert result = menuService.saveDessert(dessert);

        assertNotNull(result);
        assertEquals(1L, result.getDessertId());
        verify(dessertRepository, times(1)).save(dessert);
    }

    @Test
    void testFindMealById_Found() {
        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));

        Meal result = menuService.findMealById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getMealId());
        verify(mealRepository, times(1)).findById(1L);
    }

    @Test
    void testFindMealById_NotFound() {
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> menuService.findMealById(1L));

        assertEquals("Resource not found", exception.getMessage());
        verify(mealRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveMeal() {
        when(mealRepository.save(meal)).thenReturn(meal);

        Meal result = menuService.saveMeal(meal);

        assertNotNull(result);
        assertEquals(1L, result.getMealId());
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    void testSaveBeverageAdditional() {
        when(beverageAdditionalRepository.save(beverageAdditional)).thenReturn(beverageAdditional);

        BeverageAdditional result = menuService.saveBeverageAdditional(beverageAdditional);

        assertNotNull(result);
        assertEquals(1L, result.getBeverageAdditionalId());
        verify(beverageAdditionalRepository, times(1)).save(beverageAdditional);
    }

    @Test
    void testSaveLunch() {
        LunchRequestDTO lunchRequestDto = new LunchRequestDTO();
        lunchRequestDto.setMainCourseId(1L);
        lunchRequestDto.setDessertId(1L);

        Meal meal = new Meal();
        meal.setMealId(1L);  
        Dessert dessert = new Dessert();
        dessert.setDessertId(1L); 

        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));
        when(dessertRepository.findById(1L)).thenReturn(Optional.of(dessert));

        Lunch lunch = new Lunch();
        lunch.setLunchId(1L);  
        lunch.setMainCourse(meal);
        lunch.setDessert(dessert);
        

        when(lunchRepository.save(any(Lunch.class))).thenReturn(lunch);

        Lunch result = menuService.saveLunch(lunchRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getLunchId());
        assertEquals(1L, result.getDessert().getDessertId());
        assertEquals(1L, result.getMainCourse().getMealId());

        verify(mealRepository, times(1)).findById(1L);
        verify(dessertRepository, times(1)).findById(1L);
        verify(lunchRepository, times(1)).save(any(Lunch.class));
    }
    
    
    @Test
    void testDeleteBeverage() {
        Long id = 1L;
        menuService.deleteBeverage(id);
        verify(beverageRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteBeverageAdditional() {
        Long id = 1L;
        menuService.deleteBeverageAdditional(id);
        verify(beverageAdditionalRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteMeal() {
        Long id = 1L;
        menuService.deleteMeal(id);
        verify(mealRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteDessert() {
        Long id = 1L;
        menuService.deleteDessert(id);
        verify(dessertRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteLunch() {
        Long id = 1L;
        menuService.deleteLunch(id);
        verify(lunchRepository, times(1)).deleteById(id);
    }


    
    @Test
    void testGetBeverages() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Beverage> mockPage = new PageImpl<>(List.of(new Beverage()));

        when(beverageRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Beverage> result = menuService.getBeverages(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(beverageRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetMeals() {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Meal> mockPage = new PageImpl<>(List.of(new Meal()));

        when(mealRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Meal> result = menuService.getMeals(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(mealRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAdditionals() {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<BeverageAdditional> mockPage = new PageImpl<>(List.of(new BeverageAdditional()));

        when(beverageAdditionalRepository.findAll(pageable)).thenReturn(mockPage);

        Page<BeverageAdditional> result = menuService.getAdditionals(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(beverageAdditionalRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetLunches() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Lunch> mockPage = new PageImpl<>(List.of(new Lunch()));

        when(lunchRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Lunch> result = menuService.getLunches(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(lunchRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetDesserts() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Dessert> mockPage = new PageImpl<>(List.of(new Dessert()));

        when(dessertRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Dessert> result = menuService.getDesserts(page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(dessertRepository, times(1)).findAll(pageable);
    }
    

}
