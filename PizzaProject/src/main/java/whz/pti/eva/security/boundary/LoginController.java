package whz.pti.eva.security.boundary;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import whz.pti.eva.customerCartModel.service.CartService;
import whz.pti.eva.security.domain.CurrentUser;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.service.dto.UserDTO;
import whz.pti.eva.security.service.user.UserService;
import whz.pti.eva.security.service.user.UserServiceImpl;

@Controller
public class LoginController {




	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	private final UserServiceImpl userServiceImpl;
	
	@Autowired
    UserService userService;
	
	@Autowired
    private CartService cartService;
	
	LoginController(UserServiceImpl userServiceImpl){
		this.userServiceImpl = userServiceImpl;
	}
	 private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
	
	@GetMapping("/login")
	public String getLoginPage(@RequestParam("error") Optional<String> error,  Model model, HttpServletRequest request) {
		SavedRequest savedRequest = requestCache.getRequest(request, null);
		if (error.isPresent()) {
		    model.addAttribute("errorMessage", "Invalid username or password");
		}
		if (savedRequest != null) {
            // Debug or log the original requested URL
            System.out.println("Original URL: " + savedRequest.getRedirectUrl());
        }
		log.debug("hallo bei pizzaDelivery");
		return "login";
	}

	@GetMapping("/logout")
	public String getMyLogoutPage(HttpSession session, Model model) {
		// Get the user ID from the session
//        String userId = (String) session.getAttribute("id");
//		UserDTO userDto = new UserDTO();
//		String userId = String.valueOf(userDto.getId());
		
//		UserDTO userDto = getCurrentLoggedInUser();
//		String userId = String.valueOf(userDto.getId());
//        if (userId != null) {
//            cartService.clearCart(userId); // Clear the cart
//        }

        // Invalidate the session
        session.invalidate();
		log.debug("bye evaChatApp");
		return "login";
	}
	
	@GetMapping("/role-based-redirect")
    public String redirectBasedOnRole(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            return "redirect:/adminMenu"; // Admin-specific page
        }
        return "redirect:/"; // Default fallback
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
