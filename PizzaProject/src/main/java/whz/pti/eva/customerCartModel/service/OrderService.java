package whz.pti.eva.customerCartModel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Ordered;
import whz.pti.eva.customerCartModel.domain.entities.OrderedItem;
import whz.pti.eva.customerCartModel.domain.repositories.OrderedItemRepository;
import whz.pti.eva.customerCartModel.domain.repositories.OrderedRepository;

@Service
public class OrderService {

    @Autowired
    private OrderedItemRepository orderedItemRepo;

    @Autowired
    private OrderedRepository orderedRepo;
    
    @Autowired
    private CartService cartService;

    public void saveOrderedItem(OrderedItem orderedItem) {
        orderedItemRepo.save(orderedItem);
    }

    public Ordered addAllFromCartToOrdered(Cart cart, String userId) {
        Ordered ordered = new Ordered();

        ordered.setOrderedItem(cart.getItems().stream()
            .map(i -> new OrderedItem(
                i.getPizza(),
                i.getPizza().getName(),
                i.getQuantity(),
                userId,
                i.getPizzasize(),
                i.getPrice()))
            .collect(Collectors.toList()));

        ordered.setUserId(userId);
        ordered.setNumberOfItems(ordered.getOrderedItem().size());

        orderedRepo.save(ordered);
        ordered.getOrderedItem().forEach(orderedItemRepo::save);
        
     // Clear the cart
        cartService.clearCart(userId);

        return ordered;
    }

    public void removeFromOrdered(OrderedItem orderedItem) {
        orderedItemRepo.delete(orderedItem);
    }

    public void deleteOrder(Ordered ordered) {
        ordered.getOrderedItem().forEach(orderedItemRepo::delete);
        orderedRepo.delete(ordered);
    }

    public List<Ordered> getAllOrders() {
        return orderedRepo.findAll();
    }

    public List<Ordered> findByUserId(String userId){
        return orderedRepo.findAllByUserId(userId);
    }

    public Ordered getOrderById(Long id) {
        return orderedRepo.findById(id).get();
    }
    
    public int getTotalPizzas(String userId) {
    	Optional<Ordered> orderedOpt = orderedRepo.findByUserId(userId);
    	if (orderedOpt.isEmpty()) {
    		return 0;
    	}
    	Ordered ordered = orderedOpt.get();
    	return ordered.getTotalQuantity();
    	
    }
    public BigDecimal getTotalPrice(String userId) {
    	Optional<Ordered> orderedOpt = orderedRepo.findByUserId(userId);
    	if (orderedOpt.isEmpty()) {
    		return null;
    	}
    	Ordered ordered = orderedOpt.get();
    	return ordered.getTotalPrice();
    }

    public boolean updateOrder(Long id, Ordered updatedOrder) {
        return orderedRepo.findById(id).map(existingOrder -> {
            existingOrder.setOrderedItem(updatedOrder.getOrderedItem());
            existingOrder.setNumberOfItems(updatedOrder.getNumberOfItems());
            orderedRepo.save(existingOrder);
            return true;
        }).orElse(false);
    }

}
