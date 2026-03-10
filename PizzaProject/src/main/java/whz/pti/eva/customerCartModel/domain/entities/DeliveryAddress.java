package whz.pti.eva.customerCartModel.domain.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
import whz.pti.eva.common.BaseEntity;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
public class DeliveryAddress extends BaseEntity<Integer>{

	private String street;
	private String houseNumber;
	private String town;
	private String postalCode;
	
	@ManyToMany(mappedBy = "deliveryAddresses", cascade = CascadeType.ALL)
	private Set<Customer> customers = new HashSet<>();
}
