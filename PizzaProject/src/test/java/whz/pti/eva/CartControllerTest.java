//package whz.pti.eva;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.ui.Model;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//import java.util.ArrayList;
//import java.util.List;
//
//import whz.pti.eva.customerCartModel.boundary.CartController;
//import whz.pti.eva.customerCartModel.domain.PizzaSize;
//import whz.pti.eva.customerCartModel.domain.dto.DeliveryAddressDTO;
//import whz.pti.eva.customerCartModel.domain.entities.*;
//import whz.pti.eva.customerCartModel.service.*;
//import whz.pti.eva.security.service.dto.UserDTO;
//import whz.pti.eva.security.service.user.UserService;
//import whz.pti.eva.security.domain.*;
//import whz.pti.eva.customerCartModel.domain.repositories.*;
//
//public class CartControllerTest {
//
//    @InjectMocks
//    private CartController cartController;
//
//    @Mock
//    private CartService cartService;
//
//    @Mock
//    private PizzaService pizzaService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private Model model;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    @Mock
//    private Authentication authentication;
//
//    @Mock
//    private UserDetails userDetails;
//
//    private UserDTO mockUserDTO;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockUserDTO = new UserDTO();
//        mockUserDTO.setId(1L);
//        mockUserDTO.setUsername("testuser");
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn("testuser");
//        when(userService.findByUsername("testuser")).thenReturn(mockUserDTO);
//
//        SecurityContextHolder.setContext(securityContext);
//    }
//
//    @Test
//    public void testAddToCart_Success() {
//        Long pizzaId = 1L;
//        String size = "Small"; // Use a valid PizzaSize constant
//        int quantity = 2;
//        Pizza mockPizza = new Pizza();
//
//        when(pizzaService.findById(pizzaId)).thenReturn(mockPizza);
//
//        String result = cartController.addToCart(pizzaId, size, quantity, model);
//
//        verify(cartService).addItemToCart(mockPizza, PizzaSize.valueOf(size), quantity, String.valueOf(mockUserDTO.getId()));
//        assertEquals("redirect:/pizza", result);
//    }
//
//
//    @Test
//    public void testAddToCart_PizzaNotFound() {
//        Long pizzaId = 1L;
//        String size = "Medium";
//        int quantity = 2;
//
//        when(pizzaService.findById(pizzaId)).thenReturn(null);
//
//        String result = cartController.addToCart(pizzaId, size, quantity, model);
//
//        verify(model).addAttribute(eq("error"), eq("Pizza not found"));
//        assertEquals("error", result);
//    }
//
//    @Test
//    public void testViewCart_EmptyCart() {
//        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//        String result = cartController.viewCart(model);
//
//        verify(model).addAttribute(eq("isCartEmpty"), eq(true));
//        assertEquals("cart", result);
//    }
//
//    @Test
//    public void testViewCart_WithItems() {
//        Cart mockCart = new Cart();
//        mockCart.setItems(List.of(new Item()));
//
//        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(mockCart));
//
//        String result = cartController.viewCart(model);
//
//        verify(model).addAttribute(eq("items"), eq(mockCart.getItems()));
//        verify(model).addAttribute(eq("cart"), eq(mockCart));
//        assertEquals("cart", result);
//    }
//
//    @Test
//    public void testRemoveItemFromCart() {
//        Long itemId = 1L;
//        Cart mockCart = new Cart();
//        Item mockItem = new Item();
//        mockItem.setId(itemId);
//
//        mockCart.setItems(new ArrayList<>(List.of(mockItem)));
//
//        when(cartService.findByUserId(anyString())).thenReturn(mockCart);
//
//        String result = cartController.removItemFromCart(itemId);
//        assertTrue(mockCart.getItems().isEmpty());
//        verify(cartRepository).save(mockCart);
//        assertEquals("redirect:/cart", result);
//    }
//
//
//    @Test
//    public void testClearCart() {
//        Cart mockCart = new Cart();
//
//        when(cartService.findByUserId(anyString())).thenReturn(mockCart);
//
//        String result = cartController.clearCart();
//
//        verify(cartRepository).save(mockCart);
//        assertTrue(mockCart.getItems().isEmpty());
//        assertEquals("redirect:/cart", result);
//    }
//
//    @Test
//    public void testAddAddress() {
//        DeliveryAddressDTO addressDTO = new DeliveryAddressDTO();
//
//        ResponseEntity<?> response = cartController.addAddress(addressDTO);
//
//        verify(cartService).addAddress(addressDTO, mockUserDTO);
//        assertEquals(ResponseEntity.ok().build(), response);
//    }
//}
