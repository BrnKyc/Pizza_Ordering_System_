package whz.pti.eva.security.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import whz.pti.eva.security.domain.CurrentUser;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserControllerAdvice.class);

	@ModelAttribute("currentUser")
	public CurrentUser getCurrentUser(Authentication authentication) {
        log.debug("authentication = " + authentication);
		return (authentication == null) ? null : (CurrentUser) authentication.getPrincipal();
	}

}
