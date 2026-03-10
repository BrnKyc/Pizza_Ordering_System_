package whz.pti.eva.customerCartModel.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import whz.pti.eva.customerCartModel.domain.PizzaSize;
import whz.pti.eva.customerCartModel.domain.entities.Pizza;
import whz.pti.eva.customerCartModel.domain.repositories.PizzaRepository;

@Service
public class PizzaService {
	private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }
    public void save(Pizza pizza){
        pizzaRepository.save(pizza);
    }
   
    public Pizza getPizzaById(Long id){
        return pizzaRepository.findById(id).get();
    }
    public BigDecimal pizzaPrice(Pizza pizza, PizzaSize size){
        switch (size) {
            case Large:
                return pizza.getPriceLarge();
            case Medium:
                return pizza.getPriceMedium();
            case Small:
                return pizza.getPriceSmall();
            default:
                throw new IllegalArgumentException("Unsupported pizza size: " + size);
        }
    }
    public void pizzaDelete(Long pizzaId) {
        pizzaRepository.findById(pizzaId).ifPresent(pizzaRepository::delete);
    }
    
    
    public Pizza findById(Long id){
        return pizzaRepository.findById(id).get();
    }
}
