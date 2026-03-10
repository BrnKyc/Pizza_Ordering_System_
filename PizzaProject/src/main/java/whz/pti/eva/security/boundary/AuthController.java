package whz.pti.eva.security.boundary;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;
import whz.pti.eva.customerCartModel.service.SmmpService;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.service.dto.CustomerDTO;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;
import whz.pti.eva.security.service.user.UserServiceImpl;


//@RequestMapping("/api/auth")	
@Controller
@RequiredArgsConstructor
public class AuthController {
	
    private final UserServiceImpl userServiceImpl;
    private final UserService userService;
    @Autowired
    SmmpService smmpService;
    
    
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    
    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	CustomerDTO user = new CustomerDTO();
        model.addAttribute("user", user);
        return "register";
    }
    
    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") CustomerDTO customerDto,
					            BindingResult result,
					            Model model){
    	Optional<User> existing = userService.getUserByUsername(customerDto.getUsername());
    	if (existing.isPresent()) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }
		if (result.hasErrors()) {
			model.addAttribute("user", customerDto);
			return "register";
		}
		userService.saveUser(customerDto);
		String operation = "open";
    	String to = "pizza";
    	String username = customerDto.getUsername();
    	PayActionResponseDTO payActionResponse = smmpService.doPayAction(username, to, operation);
		return "redirect:/login";
	}


}
