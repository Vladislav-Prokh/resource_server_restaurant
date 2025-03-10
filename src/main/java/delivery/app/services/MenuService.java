package delivery.app.services;

import delivery.app.events.lunches.LunchCreatedEvent;
import delivery.app.events.lunches.LunchDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Dessert;
import delivery.app.entities.Meal;
import delivery.app.repositories.BeverageAdditionalRepository;
import delivery.app.repositories.BeverageRepository;
import delivery.app.repositories.DessertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import delivery.app.dto.LunchRequestDTO;
import delivery.app.entities.Lunch;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.LunchRepository;
import delivery.app.repositories.MealRepository;

import java.util.List;

@Service
public class MenuService {

	private final ApplicationEventPublisher applicationEventPublisher;

	private final BeverageRepository beverageRepository;
	private final DessertRepository dessertRepository;
	private final MealRepository mealRepository;
	private final LunchRepository lunchRepository;
	private final BeverageAdditionalRepository beverageAdditionalRepository;
	private final String NOT_FOUND_MESSAGE = "Resource not found";
	public MenuService(BeverageRepository beverageRepository, DessertRepository dessertRepository, MealRepository mealRepository, LunchRepository lunchRepository, BeverageAdditionalRepository beverageAdditionalRepository, ApplicationEventPublisher applicationEventPublisher) {
		this.beverageRepository = beverageRepository;
		this.dessertRepository = dessertRepository;
		this.mealRepository = mealRepository;
		this.lunchRepository = lunchRepository;
		this.beverageAdditionalRepository = beverageAdditionalRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}
	public Beverage findBeverageById(Long beverage_id) {
		return this.beverageRepository.findById(beverage_id)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
	}
	public Beverage saveBeverage(Beverage beverage) {
		return this.beverageRepository.save(beverage);
	}
	public Dessert findDessertById(Long dessertId) {
		return this.dessertRepository.findById(dessertId)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
	}
	public Dessert saveDessert(Dessert dessert) {
        return this.dessertRepository.save(dessert);
	}
	public Meal findMealById(Long meal_id) {
		return this.mealRepository.findById(meal_id)
				.orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
	}
	public Meal saveMeal(Meal meal) {
		return this.mealRepository.save(meal);
	}
	public List<Dessert> saveDesserts(List<Dessert> dessertItems) {
		return this.dessertRepository.saveAll(dessertItems);
	}
	public List<Meal> saveMeals(List<Meal> mealItems) {
		return this.mealRepository.saveAll(mealItems);
	}
	public BeverageAdditional saveBeverageAdditional(BeverageAdditional additional) {
		return this.beverageAdditionalRepository.save(additional);
	}

	@CachePut(value = "lunches", key = "#result.lunchId")
	public Lunch saveLunch(LunchRequestDTO lunchRequestDto) {
		Long mainCourseId = lunchRequestDto.getMainCourseId();
		Long dessertId = lunchRequestDto.getDessertId();
		Meal mainCourse = this.mealRepository.findById(mainCourseId)
				.orElseThrow(() -> new ResourceNotFoundException("main course not found"));
		Dessert dessert = this.dessertRepository.findById(dessertId)
				.orElseThrow(() -> new ResourceNotFoundException("dessert not found"));
		Lunch lunch = new Lunch();
		lunch.setMainCourse(mainCourse);
		lunch.setDessert(dessert);
		lunch = this.lunchRepository.save(lunch);
		applicationEventPublisher.publishEvent(new LunchCreatedEvent(this, lunch));
		return lunch;
	}
	public void deleteBeverageAdditional(Long id) {
		this.beverageAdditionalRepository.deleteById(id);
	}
	public void deleteBeverage(Long id)  {
		this.beverageRepository.deleteById(id);
	}
	public void deleteMeal(Long id) {
		this.mealRepository.deleteById(id);
	}
	public void deleteDessert(Long id) {
		this.dessertRepository.deleteById(id);
	}
	public void deleteLunch(Long id) {
		this.lunchRepository.deleteById(id);
		applicationEventPublisher.publishEvent(new LunchDeletedEvent(this, id));
	}
	public Page<Beverage> getBeverages(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return beverageRepository.findAll(pageable);
	}
	public Page<Meal> getMeals(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return mealRepository.findAll(pageable);
	}
	public Page<BeverageAdditional> getAdditionals(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return this.beverageAdditionalRepository.findAll(pageable);
	}
	public Page<Lunch> getLunches(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return lunchRepository.findAll(pageable);
	}

	@Cacheable(value = "lunches", key = "#id")
	public Lunch getLunchById(long id) {
		return this.lunchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE));
	}
	public Page<Dessert> getDesserts(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return dessertRepository.findAll(pageable);
	}
	public List<Beverage> saveBeverages(List<Beverage> list) {
		return this.beverageRepository.saveAll(list);
	}
}
