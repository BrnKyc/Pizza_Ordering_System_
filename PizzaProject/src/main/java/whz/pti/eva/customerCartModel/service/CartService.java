package whz.pti.eva.customerCartModel.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import whz.pti.eva.customerCartModel.domain.PizzaSize;
import whz.pti.eva.customerCartModel.domain.dto.DeliveryAddressDTO;
import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.entities.Item;
import whz.pti.eva.customerCartModel.domain.entities.Pizza;
import whz.pti.eva.customerCartModel.domain.repositories.CartRepository;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.domain.repositories.ItemRepository;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.UserDTO;

@Service
public class CartService {
	
	

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    
    public Cart findCartByUserId(String userId){
        return cartRepository.findByUserId(userId).get();
    }
    public Cart findById(Long cartId){
        return cartRepository.findById(cartId).get();

    }

    public Cart findByUserId(String userId){
        return cartRepository.findByUserId(userId).get();
    }
   
    @Transactional
    public void addItemToCart(Pizza pizza, PizzaSize size, int quantity, String userId, BigDecimal price) {
        // Fetch the cart or create a new one
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        // Check if the item already exists in the cart
        Item existingItem = cart.getItems().stream()
                .filter(item -> item.getPizza().getId() == pizza.getId() && item.getPizzasize() == size)
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update the quantity of the existing item
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Create a new item and add it to the cart
            Item newItem = new Item(quantity, pizza, size, price);
//            newItem.setCart(cart);
            cart.getItems().add(newItem);
            itemRepository.save(newItem);
        }

        cart.setQuantity(cart.getItems().size());
        // Save the cart
        cartRepository.save(cart);
    }
    
    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found for user: " + userId));
        
        // Remove all items from the cart
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    
    
    public int getTotalPizzas(String userId) {
    	Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
    	if (cartOpt.isEmpty()) {
    		return 0;
    	}
    	Cart cart = cartOpt.get();
    	return cart.getTotalQuantity();
    	
    }
    public BigDecimal getTotalPrice(String userId) {
    	Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
    	if (cartOpt.isEmpty()) {
    		return null;
    	}
    	Cart cart = cartOpt.get();
    	return cart.getTotalPrice();
    }
    



}
