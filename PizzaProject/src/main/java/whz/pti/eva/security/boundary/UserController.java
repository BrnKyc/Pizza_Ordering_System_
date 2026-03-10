package whz.pti.eva.security.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import whz.pti.eva.customerCartModel.domain.repositories.CustomerRepository;
import whz.pti.eva.customerCartModel.service.CustomerService;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserCreateForm;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;
import whz.pti.eva.security.service.validator.UserCreateFormValidator;

@RequestMapping("/user")
@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private UserService userService;
	private CustomerService customerService;
	private UserCreateFormValidator userCreateFormValidator;
	@Autowired
	private CustomerRepository customerRepository;

	public UserController(UserService userService,
			UserCreateFormValidator userCreateFormValidator) {
		this.userService = userService;
		this.userCreateFormValidator = userCreateFormValidator;
	}

	@InitBinder("myform")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(userCreateFormValidator);
	}

	@GetMapping("/users")
	public String getUsersPage(Model model) {
		log.info("Getting users page");
		model.addAttribute("users", userService.getAllUsers());
		return "users";
	}

//  @PreAuthorize("(hasAuthority('ADMIN') or hasAuthority('USER')) and #id == principal.id")
	@PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
	@RequestMapping(value = "/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String getUserPage(@PathVariable Long id, Model model) {
		log.debug("Getting user page for user= " + id);
		UserDTO userDTO = userService.getUserById(id);
//		Customer customer = customerService.getCustomerById
		model.addAttribute("user", userDTO);
		model.addAttribute("fromUser", userDTO.getUsername());
		return "user";
	}


	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/create")
	public String handleUserCreateForm(@Valid @ModelAttribute("myform") UserCreateForm form,
			BindingResult bindingResult, Model model) {
		log.info("Processing user create form= " + form + " bindingResult= " + bindingResult);
		model.addAttribute("users", userService.getAllUsers());
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", bindingResult.getGlobalError().getDefaultMessage());
			return "redirect:/user/users";
		}
		userService.create(form);
		return "redirect:/user/users";

	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/update")
	public String handleUserUpdateForm(@Valid @ModelAttribute("myform") UserCreateForm form,
			BindingResult bindingResult, @RequestParam("id") Long id, Model model) {
		log.info("Processing user create form= " + form + " bindingResult= " + bindingResult);
		model.addAttribute("users", userService.getAllUsers());
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", bindingResult.getGlobalError().getDefaultMessage());
			return "redirect:/user/users";
		}
		userService.update(form, id);
		return "redirect:/user/users";

	}
	
	@PostMapping("/{id}/disable")
    public ResponseEntity<String> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok("User disabled successfully");
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<String> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok("User enabled successfully");
    }
    
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		userService.deleteUser(id);
		return "redirect:/user/users";

	}
	
	
}



