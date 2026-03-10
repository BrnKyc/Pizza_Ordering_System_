package whz.pti.eva.customerCartModel.domain.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
import whz.pti.eva.common.BaseEntity;
import whz.pti.eva.security.domain.User;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
public class Customer extends BaseEntity<Long>{
	
	private String firstname;

	private String lastname;
	
	private String phoneNumber;
	
	
	
	// Many-to-Many relationship for all delivery addresses
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "customer_delivery_address",
        joinColumns = @JoinColumn(name = "customer_id"), // Customer foreign key
        inverseJoinColumns = @JoinColumn(name = "delivery_address_id") // DeliveryAddress foreign key
    )
    private Set<DeliveryAddress> deliveryAddresses = new HashSet<>();

    // One-to-One relationship for the currently selected delivery address
    @OneToOne
    @JoinColumn(name = "current_delivery_address_id") // Adds a foreign key to DeliveryAddress
    private DeliveryAddress currentDeliveryAddress;
	
	@OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to the associated User
	
	
}
