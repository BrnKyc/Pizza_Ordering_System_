package whz.pti.eva.customerCartModel.boundary;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.entities.Ordered;
import whz.pti.eva.customerCartModel.domain.entities.OrderedItem;
import whz.pti.eva.customerCartModel.domain.repositories.CartRepository;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.service.CartService;
import whz.pti.eva.customerCartModel.service.OrderService;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;

@RequestMapping("/order")
@Controller
public class OrderedController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;

    @GetMapping("/list_all")
    public ResponseEntity<List<Ordered>> getAllOrders() {
        List<Ordered> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/find")
    public String getOrderById(@RequestParam("id") Long id, Model model) {
        try {
        	
        	UserDTO userDto = getCurrentLoggedInUser();
    	    String userId = String.valueOf(userDto.getId());
            // Fetch the logged-in user's details
               User user = userRepository.findByUsername(userDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
               
               // Fetch the associated customer
               Customer customer = customerRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Customer not found"));
               // Select the delivery address (first or default)
               DeliveryAddress selectedAddress = customer.getCurrentDeliveryAddress();
               
               Ordered ordered = new Ordered();
               // Fetch the associated customer
               model.addAttribute("totalPizzas", ordered.getNumberOfItems());
               model.addAttribute("totalPrice", orderService.getTotalPrice(userId));
               model.addAttribute("customer", customer);
               model.addAttribute("selectedAddress", selectedAddress);
            Ordered order = orderService.getOrderById(id);
            List<OrderedItem> orderedItems = order.getOrderedItem();
            model.addAttribute("items", orderedItems);
            model.addAttribute("totalPizzas", cartService.getTotalPizzas(userId));
            model.addAttribute("totalPrice", cartService.getTotalPrice(userId));
            return "order";
        } catch (RuntimeException e) {
            return "error";
        }
    }


    @GetMapping("/list")
    public String getOrdersByUserId(Model model) {
    	UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
        List<Ordered> orders = orderService.findByUserId(userId);
        if (orders.isEmpty()){
	    	model.addAttribute("isOrderedEmpty", true); // Flag to indicate ordered is empty
            return "ordered"; // Show the ordered page with the empty message
	    }
        OrderedItem item = orders.getFirst().getOrderedItem().getFirst();
        model.addAttribute("orders", orders);
        model.addAttribute("orderedItem", item);
        
        return "ordered";
    }


    @GetMapping("/create_new_order")
    public String createOrderFromCart(@RequestParam("cartId") Long cartId, Model model) {
    	UserDTO userDto = getCurrentLoggedInUser();
	    String userId = String.valueOf(userDto.getId());
	    
	    Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
	    
	    if (cartOpt.isEmpty()){
	    	model.addAttribute("isCartEmpty", true); // Flag to indicate cart is empty
            return "cart"; // Show the cart page with the empty message
	    }
        Cart cart = cartOpt.get();
        Ordered newOrder = orderService.addAllFromCartToOrdered(cart, userId);
        int orderDuration = 30;
        model.addAttribute("orderDuration", orderDuration);
        
        return "orderedSuccessful";
    }

    private UserDTO getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername()); // Replace with your user lookup logic
        }
        throw new IllegalStateException("No authenticated user found");
    }


//    @PutMapping("/order/update")
//    public ResponseEntity<Ordered> updateOrder(@RequestParam Long id, @RequestBody Ordered updatedOrder) {
//        boolean isUpdated = orderService.updateOrder(id, updatedOrder);
//        if (isUpdated) {
//            return ResponseEntity.ok(updatedOrder);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/order/delete")
//    public ResponseEntity<Void> deleteOrderById(@RequestParam Long id) {
//        boolean isDeleted = orderService.deleteOrderById(id);
//        if (isDeleted) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
