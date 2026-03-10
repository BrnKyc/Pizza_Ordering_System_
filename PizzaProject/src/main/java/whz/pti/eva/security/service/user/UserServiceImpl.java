package whz.pti.eva.security.service.user;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.security.domain.Role;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserCreateForm;
import whz.pti.eva.security.domain.UserRepository;
import whz.pti.eva.security.service.dto.CustomerDTO;
import whz.pti.eva.security.service.dto.UserDTO;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	@Autowired
	public UserServiceImpl(UserRepository userRepository, CustomerRepository customerRepository) {
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
	}
	


	@Override
	public UserDTO getUserById(Long id) {
		log.debug("Getting user={}", id);
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(String.format(">>> User=%s not found", id)));
		UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash());
		return userDTO;
	}
	@Override
	public UserDTO findByUsername(String username) {
		log.debug("Getting user={}", username);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NoSuchElementException(String.format(">>> User=%s not found", username)));
		UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash());
		return userDTO;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		log.debug("Getting user by email={}", email.replaceFirst("@.*", "@***"));
		return userRepository.findOneByEmail(email);
	}
	
	@Override
	public Optional<User> getUserByUsername(String username) {
		log.debug("Getting user by name={}");
		return userRepository.findByUsername(username);
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public List<UserDTO> getAllUsers() {
		log.debug("Getting all users");
		return userRepository.findAllByOrderByUsernameAsc().stream()
				.map(source -> new UserDTO(source.getId(), source.getUsername(), source.getEmail(), source.getPasswordHash()))
				.collect(Collectors.toList());
	}

	@Override
	public User create(UserCreateForm form) {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		User user = new User();
		user.setEmail(form.getEmail());
		user.setUsername(form.getUsername());
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		user.setRole(form.getRole());
		return userRepository.save(user);
	}
	
	// for registration
	public void saveUser(CustomerDTO customerDto) {
		User user = new User();
		user.setUsername(customerDto.getUsername());
		user.setEmail(customerDto.getEmail());
		user.setRole(Role.CUSTOMER);
		
		Customer customer = new Customer();
		customer.setFirstname(customerDto.getFirstname());
		customer.setLastname(customerDto.getLastname());
		customer.setPhoneNumber(customerDto.getPhoneNumber());
		customer.setUser(user);

        user.setPasswordHash(passwordEncoder.encode(customerDto.getPasswordHash()));
        userRepository.save(user);
        customerRepository.save(customer);
    }
	
	@Override
	public User update(UserCreateForm form, Long id) {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		Optional<User> userOpt = userRepository.findById(id);
		Customer customer = customerRepository.findByUser(userOpt).get();
		User user = userOpt.get();
		user.setEmail(form.getEmail());
		user.setUsername(form.getUsername());
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		user.setRole(form.getRole());
//		customer.setFirstname(form.getFirstname());
//		customer.setLastname(form.getLastname());
//		customer.setPhoneNumber(form.getPhoneNumber());
//		customer.setUser(user);
		User userSaved = userRepository.save(user);
		customerRepository.save(customer);
		
		return userSaved;
	}
	
	public void deleteUser(Long id) {
		userRepository.findById(id).ifPresent(userRepository::delete);
		
	}
	
	@Transactional
	public void deleteUserAndCustomer(Long id) {
	    log.info("Attempting to delete user with id: {}", id);
	    userRepository.findById(id).ifPresent(user -> {
	        log.info("Found user: {}", user);
	        Customer customer = user.getCustomer();
	        if (customer != null) {
	            log.info("Deleting associated customer: {}", customer);
	            customerRepository.delete(customer);
	        }
	        log.info("Deleting user: {}", user);
	        userRepository.delete(user);
	    });
	}

	
	 @Transactional
	    public void disableUser(Long userId) {
	        userRepository.findById(userId).ifPresent(user -> {
	            user.setDisabled(true);
	            userRepository.save(user);
	            log.info("User with ID {} has been disabled.", userId);
	        });
	    }

	    @Transactional
	    public void enableUser(Long userId) {
	        userRepository.findById(userId).ifPresent(user -> {
	            user.setDisabled(false);
	            userRepository.save(user);
	            log.info("User with ID {} has been enabled.", userId);
	        });
	    }
	
	public void performUserAction(Long userId) {
	    User user = userRepository.findById(userId)
	                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	    if (user.isDisabled()) {
	        throw new IllegalStateException("User is disabled and cannot perform this action.");
	    }

	    // Proceed with the action...
	}





}
