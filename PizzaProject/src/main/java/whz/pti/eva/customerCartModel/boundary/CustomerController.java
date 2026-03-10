package whz.pti.eva.customerCartModel.boundary;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import whz.pti.eva.customerCartModel.domain.dto.DeliveryAddressDTO;
import whz.pti.eva.customerCartModel.domain.dto.OrderDTO;
import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.domain.repositories.DeliveryAddressRepository;
import whz.pti.eva.customerCartModel.service.CustomerService;
import whz.pti.eva.customerCartModel.service.OrderService;
import whz.pti.eva.customerCartModel.service.SmmpService;
import whz.pti.eva.security.boundary.UserController;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.CustomerDTO;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserServiceImpl;

@RequestMapping("/customer")	
@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private DeliveryAddressRepository deliveryAddressRepository;
	@Autowired
	UserServiceImpl userService;
	@Autowired
    SmmpService smmpService;
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
//	@PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
	@RequestMapping(value = "/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String getUserPage(@PathVariable("id") Long id, Model model) {
		
		UserDTO userDto = getCurrentLoggedInUser(); // Implement this method as per your security setup
		// Fetch the logged-in user's details
        User user = userRepository.findByUsername(userDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Fetch the associated customer
        Customer customer = customerRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Customer not found"));
        
     // Get the last added delivery address (assuming ordered by insertion or by some criteria)
        DeliveryAddress currentAddress = customer.getCurrentDeliveryAddress();
    
        String operation = "get";
    	String to = "pizza";
    	String username = userDto.getUsername();
    	PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, operation);
    	
    	String aktuelleGuthaben = payActionResponse.getDescription();
        // Add attributes to model
        model.addAttribute("customer", customer);
        model.addAttribute("user", userDto);
        model.addAttribute("currentAddress", currentAddress);
        model.addAttribute("addresses", customer.getDeliveryAddresses());
        model.addAttribute("aktuelleGuthaben", aktuelleGuthaben);
        
//		log.debug("Getting user page for user= " + id);
//		CustomerDTO customerDTO = customerService.getCustomerById(id);
////		Customer customer = customerService.getCustomerById
//		model.addAttribute("user", customerDTO);
//		model.addAttribute("fromUser", customerDTO.getUsername());
		return "userInfo";
	}
	
	
	@PostMapping("/updateProfile")
	@ResponseBody
	public ResponseEntity<?> updateCustomerProfile(@RequestBody CustomerDTO customerDto) {
	    UserDTO userDto = getCurrentLoggedInUser();
	    User user = userRepository.findByUsername(userDto.getUsername())
	                    .orElseThrow(() -> new RuntimeException("User not found"));

	    Customer customer = customerRepository.findByUser(user)
	                    .orElseThrow(() -> new RuntimeException("Customer not found"));

	    // Update personal info
	    customer.setFirstname(customerDto.getFirstname());
	    customer.setLastname(customerDto.getLastname());
	    customer.setPhoneNumber(customerDto.getPhoneNumber());
	    user.setEmail(customerDto.getEmail());

	    customerRepository.save(customer);
	    userRepository.save(user);

	    return ResponseEntity.ok().build();
	}

	@PostMapping("/addAddress")
    public ResponseEntity<?> addAddress(@RequestBody DeliveryAddressDTO addressDto) {
		UserDTO userDto = getCurrentLoggedInUser();
		customerService.addAddress(addressDto, userDto );

        return ResponseEntity.ok().build();
    }
	
	@PostMapping("/updateCurrentAddress")
    public ResponseEntity<String> updateCurrentDeliveryAddress(@RequestBody Map<String, String> payload) {
        try {
            Long addressId = Long.parseLong(payload.get("addressId"));
            customerService.updateCurrentDeliveryAddress(addressId); // Update logic
            return ResponseEntity.ok("Current delivery address updated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update address.");
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
