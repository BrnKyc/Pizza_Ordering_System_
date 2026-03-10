package whz.pti.eva.customerCartModel.boundary;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import whz.pti.eva.customerCartModel.domain.PizzaSize;
import whz.pti.eva.customerCartModel.domain.dto.DeliveryAddressDTO;
import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;
import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.entities.Item;
import whz.pti.eva.customerCartModel.domain.entities.Ordered;
import whz.pti.eva.customerCartModel.domain.entities.Pizza;
import whz.pti.eva.customerCartModel.domain.repositories.CartRepository;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.service.CartService;
import whz.pti.eva.customerCartModel.service.OrderService;
import whz.pti.eva.customerCartModel.service.PizzaService;
import whz.pti.eva.customerCartModel.service.SmmpService;
import whz.pti.eva.customerCartModel.service.SmmpServiceImpl;
import whz.pti.eva.security.boundary.LoginController;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;

@RequestMapping("/cart")	
@Controller
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    PizzaService pizzaService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    SmmpService smmpService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    
    
	
	@PostMapping("/addToCart")
    public String addToCart(@RequestParam("pizzaId") Long pizzaId,
    		@RequestParam("size") String size,
    		@RequestParam("quantity") int quantity, 
    		Model model, RedirectAttributes redirectAttributes) {
		UserDTO userDto = getCurrentLoggedInUser(); // Implement this method as per your security setup
	    String userId = String.valueOf(userDto.getId());
   	 
     // Fetch the pizza from the database
        Pizza pizza = pizzaService.findById(pizzaId);
        if (pizza == null) {
            model.addAttribute("error", "Pizza not found");
            return "error";
        }
        
     // Calculate the price based on size
        BigDecimal price = switch (size) {
            case "Small" -> pizza.getPriceSmall();
            case "Medium" -> pizza.getPriceMedium();
            case "Large" -> pizza.getPriceLarge();
            default -> throw new RuntimeException("Invalid size");
        };
        
        // Add the pizza to the cart
        cartService.addItemToCart(pizza, PizzaSize.valueOf(size), quantity, userId, price);
        // Redirect with success message
        redirectAttributes.addFlashAttribute("successMessage", "Pizza added to cart!");
        // Redirect back to the pizza listing page
        return "redirect:/pizzas";
    }
	

	@GetMapping("/cart")
    public String viewCart(Model model) {
		UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
	    
	    Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
	    
	    if (cartOpt.isEmpty()){
	    	model.addAttribute("isCartEmpty", true); // Flag to indicate cart is empty
            return "cart"; // Show the cart page with the empty message
	    }
        Cart cart = cartOpt.get();
        
        model.addAttribute("items", cart.getItems());
        model.addAttribute("cart", cart);
        
        return "cart";
    }
	@GetMapping("/orderDetails")
    public String orderDetails(@RequestParam("id") Long id, Model model) {
		UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
	    
	    Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
	    
	    if (cartOpt.isEmpty()){
	    	model.addAttribute("isCartEmpty", true); // Flag to indicate cart is empty
            return "cart"; // Show the cart page with the empty message
	    }
        Cart cart = cartOpt.get();
     // Fetch the logged-in user's details
        User user = userRepository.findByUsername(userDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Fetch the associated customer
        Customer customer = customerRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Customer not found"));
        // Select the delivery address (first or default)
        DeliveryAddress selectedAddress = customer.getCurrentDeliveryAddress();
        
        
        String operation = "get";
    	String to = "pizza";
    	String username = userDto.getUsername();
    	PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, operation);
    	
    	String aktuelleGuthaben = payActionResponse.getDescription();
        // Fetch the associated customer
        model.addAttribute("totalPizzas", cartService.getTotalPizzas(userId));
        model.addAttribute("totalPrice", cartService.getTotalPrice(userId));
        model.addAttribute("aktuelleGuthaben", aktuelleGuthaben);
        model.addAttribute("customer", customer);
        model.addAttribute("items", cart.getItems());
        model.addAttribute("cart", cart);
        model.addAttribute("selectedAddress", selectedAddress);
        model.addAttribute("addresses", customer.getDeliveryAddresses());
       
        return "orderDetails";
    }

	
	
    @GetMapping("/removeItem")
    public String removItemFromCart(@RequestParam("itemId") Long itemId) {
    	UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
	    
    	Cart cart = cartService.findByUserId(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
        return "redirect:/cart/cart"; 
    }

    @PostMapping("/editItem")
    public String editItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity, @RequestParam("size") String size) {
    	UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
	    
    	Cart cart = cartService.findByUserId(userId);
        Item itemToUpdate = cart.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

//        Pizza pizza = pizzaService.getPizzaById(pizzaId);
        itemToUpdate.setQuantity(quantity);
        itemToUpdate.setPizzasize(PizzaSize.valueOf(size));
        cartRepository.save(cart);
        return "redirect:/cart/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
    	UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
        Cart cart = cartService.findByUserId(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        return "redirect:/cart/cart";
    }
    
//    @GetMapping("/payment")
//    public ResponseEntity<?> makePayment() {
//    	UserDTO userDto = getCurrentLoggedInUser();
//    	String userId = String.valueOf(userDto.getId());
//    	String paymentAmount = String.valueOf(cartService.getTotalPrice(userId));
//    	String k = String.valueOf(new BigDecimal(13.2));
//    	String pcontent = "transfer pizza "+paymentAmount;
//    	String to = "pizza";
//    	
//    	String username = userDto.getUsername();
//    	log.info("Payment content: {}", pcontent);
//    	PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, pcontent);
//    	if (payActionResponse.getPayment()) {
//            return ResponseEntity.ok(payActionResponse); // Success
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payActionResponse); // Failure
//        }
//
//    }
    
    @GetMapping("/payment")
    public String makePayment(Model model) {
        UserDTO userDto = getCurrentLoggedInUser();
        String userId = String.valueOf(userDto.getId());
        String paymentAmount = String.valueOf(cartService.getTotalPrice(userId));
        String pcontent = "transfer pizza " + paymentAmount;
        String to = "pizza";

        String username = userDto.getUsername();
        log.info("Payment content: {}", pcontent);

        PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, pcontent);

        if (payActionResponse.getPayment()) {
        	
    	    
    	    Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
    	    
    	    if (cartOpt.isEmpty()){
    	    	model.addAttribute("isCartEmpty", true); // Flag to indicate cart is empty
                return "cart"; // Show the cart page with the empty message
    	    }
            Cart cart = cartOpt.get();
            Ordered newOrder = orderService.addAllFromCartToOrdered(cart, userId);
            
            // Payment successful
            cartService.clearCart(userId); // Clear the cart after successful payment
            model.addAttribute("message", "Payment successful! Your order has been created.");
            model.addAttribute("paymentAmount", paymentAmount);
            model.addAttribute("description", payActionResponse.getDescription());
            return "orderedSuccessful"; // Redirect to success page
        } else {
            // Payment failed
            model.addAttribute("message", "Payment failed: " + payActionResponse.getDescription());
            return "payment-failure"; // Redirect to failure page
        }
    }

    
    private UserDTO getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername()); // Replace with your user lookup logic
        }
        throw new IllegalStateException("No authenticated user found");
    }

    
}


