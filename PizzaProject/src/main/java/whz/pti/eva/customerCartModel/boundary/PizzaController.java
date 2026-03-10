package whz.pti.eva.customerCartModel.boundary;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import whz.pti.eva.customerCartModel.domain.entities.Pizza;
import whz.pti.eva.customerCartModel.service.CartService;
import whz.pti.eva.customerCartModel.service.PizzaService;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserServiceImpl;

@Controller
public class PizzaController {
	@Autowired
	PizzaService pizzaService;
	@Autowired
	UserServiceImpl userService;
	@Autowired
    CartService cartService;
	
	
	@GetMapping({"/","/home","pizzas"})
    public String getPizzaMenu(Model model) {
		
		UserDTO userDto = getCurrentLoggedInUser(); // Implement this method as per your security setup
	    String userId = String.valueOf(userDto.getId());
        model.addAttribute("pizzas", pizzaService.getAllPizzas());
        model.addAttribute("totalPizzas", cartService.getTotalPizzas(userId));
        model.addAttribute("totalPrice", cartService.getTotalPrice(userId));
        
        model.addAttribute("users", userService.getAllUsers());
        return "index";
    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/adminMenu")
	public String getAdminHomePage(Model model) {
		model.addAttribute("pizzas", pizzaService.getAllPizzas());
		return "pizza";
	}

//    @PostMapping("/pizza/create")
//    public String addNewPizza(@RequestBody Pizza newPizza) {
//        pizzaService.save(new Pizza(newPizza.getName(), newPizza.getPriceLarge(),newPizza.getPriceMedium(),newPizza.getPriceSmall()));
//        return "redirect:/admin/home";
//    }
//    
	
	@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pizza/create")
    public String addNewPizza(@RequestParam("name") String name, 
    		@RequestParam("priceLarge") BigDecimal priceLarge, 
    		@RequestParam("priceMedium") BigDecimal priceMedium,
    		@RequestParam("priceSmall") BigDecimal priceSmall 
    		) {
        pizzaService.save(new Pizza(name, priceLarge, priceMedium, priceSmall));
        return "redirect:/adminMenu";
    }

//    @PostMapping("/pizza/update")
//    public String changePizza(@RequestBody Pizza newPizza){
//        Pizza p = pizzaService.findById(newPizza.getId());
//        p.setName(newPizza.getName());
//        p.setPriceLarge(newPizza.getPriceLarge());
//        p.setPriceMedium(newPizza.getPriceMedium());
//        p.setPriceSmall(newPizza.getPriceSmall());
//        pizzaService.save(p);
//        return "redirect:/admin/home";
//    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pizza/update")
    public String updatePizza(@RequestParam("id") Long id,
    		@RequestParam("name") String name, 
    		@RequestParam("priceLarge") BigDecimal priceLarge, 
    		@RequestParam("priceMedium") BigDecimal priceMedium,
    		@RequestParam("priceSmall") BigDecimal priceSmall 
    		) {
    	
    	Pizza p = pizzaService.findById(id);
	      p.setName(name);
	      p.setPriceLarge(priceLarge);
	      p.setPriceMedium(priceMedium);
	      p.setPriceSmall(priceSmall);
	      pizzaService.save(p);
        return "redirect:/adminMenu";
    }
    
	@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/deletePizza/")
    public String deletePizza(@RequestParam("pizzaId") Long pizzaId){
        pizzaService.pizzaDelete(pizzaId);
        return "redirect:/adminMenu";
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
