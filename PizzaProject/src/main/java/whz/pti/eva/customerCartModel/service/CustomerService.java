package whz.pti.eva.customerCartModel.service;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import whz.pti.eva.customerCartModel.domain.dto.DeliveryAddressDTO;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.domain.repositories.DeliveryAddressRepository;
import whz.pti.eva.security.boundary.UserController;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.CustomerDTO;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;

@Service
public class CustomerService {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	private final CustomerRepository customerRepository;
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private DeliveryAddressRepository addressRepository;
	
	@Autowired
    UserService userService;
	
	@Autowired
	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public void addAddress(DeliveryAddressDTO addressDto, UserDTO userDto ) {
    	User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        DeliveryAddress address = new DeliveryAddress();
        address.setStreet(addressDto.getStreet());
        address.setHouseNumber(addressDto.getHouseNumber());
        address.setTown(addressDto.getTown());
        address.setPostalCode(addressDto.getPostalCode());
        customer.getDeliveryAddresses().add(address);
        customer.setCurrentDeliveryAddress(address);
        customerRepository.save(customer);
    }
	
	public void updateCurrentDeliveryAddress(Long addressId) {
		UserDTO userDto = getCurrentLoggedInUser();
	    User user = userRepository.findByUsername(userDto.getUsername())
	                    .orElseThrow(() -> new RuntimeException("User not found"));

	    Customer customer = customerRepository.findByUser(user)
	                    .orElseThrow(() -> new RuntimeException("Customer not found"));

        DeliveryAddress selectedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID"));
        
        customer.setCurrentDeliveryAddress(selectedAddress);
        customerRepository.save(customer);
    }
	
	private UserDTO getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername()); // Replace with your user lookup logic
        }
        throw new IllegalStateException("No authenticated user found");
    }
//	UserDTO getUserById(Long id) {
//		log.debug("Getting user={}", id);
//		Customer customer = customerRepository.findById(id)
//				.orElseThrow(() -> new NoSuchElementException(String.format(">>> User=%s not found", id)));
//		CustomerDTO customerDTO = new CustomerDTO(customer.getId(), customer.getUsername(), customer.getEmail(), customer.getPasswordHash());
//		return customerDTO;
//	}
	
}
