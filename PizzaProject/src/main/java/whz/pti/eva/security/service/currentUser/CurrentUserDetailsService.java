package whz.pti.eva.security.service.currentUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import whz.pti.eva.security.domain.CurrentUser;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.service.user.UserService;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(CurrentUserDetailsService.class);
	private UserService userService;

	@Autowired
	public CurrentUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Authenticating user with username={}", username);
//    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String password = request.getParameter("password"); // get from request parameter
		User user = userService.getUserByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User with username= " + username + " cannot be not found"));

		return new CurrentUser(user);
	}

}

