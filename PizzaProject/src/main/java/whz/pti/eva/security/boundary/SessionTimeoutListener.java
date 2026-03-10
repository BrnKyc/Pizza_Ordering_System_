package whz.pti.eva.security.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import whz.pti.eva.customerCartModel.service.CartService;

@Component
public class SessionTimeoutListener implements HttpSessionListener {

    @Autowired
    private CartService cartService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        // Get the user ID from the session
        String userId = (String) session.getAttribute("userId");

        if (userId != null) {
            cartService.clearCart(userId); // Clear the cart on session timeout
        }
    }
}
