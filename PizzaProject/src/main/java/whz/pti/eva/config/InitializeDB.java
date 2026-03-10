package whz.pti.eva.config;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.customerCartModel.domain.entities.Pizza;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.domain.repositories.DeliveryAddressRepository;
import whz.pti.eva.customerCartModel.domain.repositories.PizzaRepository;
import whz.pti.eva.customerCartModel.service.SmmpService;
import whz.pti.eva.customerCartModel.service.SmmpServiceImpl;
import whz.pti.eva.security.domain.Role;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.UserDTO;

@Component
public class InitializeDB {

  private static final Logger log = LoggerFactory.getLogger(InitializeDB.class);

  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  PizzaRepository pizzaRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired 
  DeliveryAddressRepository deliveryAddressRepository;
  
  @Autowired
  SmmpService smmpService;
  

  @PostConstruct
  public void init() {
	  

	  log.debug(" >>> Db initialized");
    
	  PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	  createPizza("Margherita", BigDecimal.valueOf(4.50), BigDecimal.valueOf(3.50), BigDecimal.valueOf(2.00));
	  createPizza("Tonno", BigDecimal.valueOf(6.50), BigDecimal.valueOf(5.50), BigDecimal.valueOf(4.00));
	  createPizza("Three", BigDecimal.valueOf(6.50), BigDecimal.valueOf(5.50), BigDecimal.valueOf(4.00));
	  createPizza("Four", BigDecimal.valueOf(6.50), BigDecimal.valueOf(5.50), BigDecimal.valueOf(4.00));
	  createPizza("Five", BigDecimal.valueOf(6.50), BigDecimal.valueOf(5.50), BigDecimal.valueOf(4.00));
	  createPizza("Meow", BigDecimal.valueOf(6.50), BigDecimal.valueOf(5.50), BigDecimal.valueOf(4.00));

      
      User user1 = createUser("admin", "admin@gmail.com", "a1", Role.ADMIN, passwordEncoder);
      User user2 = createUser("bnutz", "bnutz@gmail.com", "n1", Role.CUSTOMER, passwordEncoder);
      User user3 = createUser("cnutz", "cnutz@gmail.com", "n2", Role.CUSTOMER, passwordEncoder);
      
//      DeliveryAddress address1 = createDeliveryAddress("Main Street","123","New York","10001");
//      DeliveryAddress address2 = createDeliveryAddress("Bain Street","223","New York1","20001");
//      DeliveryAddress address3 = createDeliveryAddress("Kain Street","323","New York2","30001");
//      DeliveryAddress address4 = createDeliveryAddress("Nain Street","423","New York3","40001");
//      DeliveryAddress address5 = createDeliveryAddress("Aain Street","523","New York4","50001");

        
        DeliveryAddress address1 = new DeliveryAddress();
        address1.setStreet("Main Street");
        address1.setHouseNumber("123");
        address1.setTown("New York");
        address1.setPostalCode("10001");
        DeliveryAddress address2 = new DeliveryAddress();

        address2.setStreet("Broadway");
        address2.setHouseNumber("456");
        address2.setTown("Los Angeles");
        address2.setPostalCode("90001");
  
        DeliveryAddress address3 = new DeliveryAddress();
        address3.setStreet("Oak Street");
        address3.setHouseNumber("789");
        address3.setTown("Chicago");
        address3.setPostalCode("60601");
  
        Customer customer1 = new Customer();
        customer1.setFirstname("John");
        customer1.setLastname("Doe");
        customer1.setPhoneNumber("112345");
        customer1.setUser(user2);
  
        Customer customer2 = new Customer();
        customer2.setFirstname("Jane");
        customer2.setLastname("Smith");
        customer2.setPhoneNumber("112345");
        customer2.setUser(user3);
        
     // Beziehung zwischen Customer und DeliveryAddresses
      addAddressToCustomer(customer1, address1);
      addAddressToCustomer(customer1, address2);
      addAddressToCustomer(customer2, address2);
      addAddressToCustomer(customer2, address3);
      
      
      deliveryAddressRepository.save(address1);
      deliveryAddressRepository.save(address2);
      deliveryAddressRepository.save(address3);
        
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        
        
  }
  
  private DeliveryAddress createDeliveryAddress(String street, String housenumber,String town, String postalCode) {
	  	DeliveryAddress address = new DeliveryAddress();
		address.setStreet(street);
        address.setHouseNumber(housenumber);
        address.setTown(town);
        address.setPostalCode(postalCode);
		try {
			deliveryAddressRepository.save(address);
			log.info("Address {} created successfully", address);
		} catch (Exception e) {
			log.error("Error creating Address {}: {}", address, e.getMessage());
		}
		return address;
	}
  
  private User createUser(String username, String email, String password, Role role,
			PasswordEncoder passwordEncoder) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(role);
		try {
			userRepository.save(user);
			log.info("User {} created successfully", username);
		} catch (Exception e) {
			log.error("Error creating user {}: {}", username, e.getMessage());
		}
		String pcontent = "open";
    	String to = "pizza";
    	PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, pcontent);
		
		return user;
	}
  
  private void createPizza(String name, BigDecimal priceLarge, BigDecimal priceMedium, BigDecimal priceSmall) {
		Pizza pizza = new Pizza();
		pizza.setName(name);
		pizza.setPriceLarge(priceLarge);
		pizza.setPriceMedium(priceMedium);
		pizza.setPriceSmall(priceSmall);
		try {
			pizzaRepository.save(pizza);
			log.info("Pizza {} created successfully", name);
		} catch (Exception e) {
			log.error("Error creating pizza {}: {}", name, e.getMessage());
		}
	}

  
  private void addAddressToCustomer(Customer customer, DeliveryAddress address) {
      customer.getDeliveryAddresses().add(address); // Owning side
      address.getCustomers().add(customer); // Inverse side
  }

  // Utility method to remove a DeliveryAddress from a Customer (if needed)
  private void removeAddressFromCustomer(Customer customer, DeliveryAddress address) {
      customer.getDeliveryAddresses().remove(address); // Owning side
      address.getCustomers().remove(customer); // Inverse side
  }
}